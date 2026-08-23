package com.animal.java.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.animal.java.config.SearchCacheConfig;
import com.animal.java.dao.AnimalRepository;
import com.animal.java.dto.AnimalFacets;
import com.animal.java.dto.AnimalSearchRequest;
import com.animal.java.dto.PageResponse;
import com.animal.java.model.animal.Animal;
import com.animal.java.search.AnimalDocument;
import com.animal.java.search.AnimalDocumentMapper;
import com.animal.java.service.AnimalSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalSearchServiceImpl implements AnimalSearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final AnimalDocumentMapper documentMapper;
    private final AnimalRepository animalRepository;
    private final String animalIndexName;

    @Value("${animalia.search.default-page-size:24}")
    private int defaultPageSize;

    @Value("${animalia.search.max-page-size:100}")
    private int maxPageSize;

    @Override
    @Cacheable(cacheNames = SearchCacheConfig.CACHE_ANIMAL_SEARCH, key = "#request.cacheKey()")
    public PageResponse<Animal> search(AnimalSearchRequest request) {
        AnimalSearchRequest normalized = normalize(request);
        try {
            PageResponse<Animal> fromEs = searchElasticsearch(normalized);
            // Empty index (or zero hits with no ES data) — use Postgres until reindex completes.
            if (fromEs.getTotalElements() == 0 && isIndexEmpty()) {
                log.warn("Elasticsearch index '{}' is empty; falling back to Postgres", animalIndexName);
                return animalRepository.searchPage(normalized);
            }
            return fromEs;
        } catch (Exception ex) {
            log.warn("Elasticsearch search failed, falling back to Postgres: {}", ex.getMessage());
            return animalRepository.searchPage(normalized);
        }
    }

    private boolean isIndexEmpty() {
        try {
            return elasticsearchClient.count(c -> c.index(animalIndexName)).count() == 0;
        } catch (Exception ex) {
            return true;
        }
    }

    @Override
    @Cacheable(cacheNames = SearchCacheConfig.CACHE_ANIMAL_FACETS, key = "'all'")
    public AnimalFacets facets() {
        try {
            return facetsElasticsearch();
        } catch (Exception ex) {
            log.warn("Elasticsearch facets failed, falling back to Postgres: {}", ex.getMessage());
            return animalRepository.loadFacets();
        }
    }

    private PageResponse<Animal> searchElasticsearch(AnimalSearchRequest request) throws Exception {
        String[] sortParts = parseSort(request.getSort());
        String sortField = mapSortField(sortParts[0]);
        SortOrder order = "desc".equalsIgnoreCase(sortParts[1]) ? SortOrder.Desc : SortOrder.Asc;

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(animalIndexName)
                .from(request.getPage() * request.getSize())
                .size(request.getSize())
                .query(buildQuery(request))
                .sort(sort -> sort.field(f -> f.field(sortField).order(order)))
        );

        SearchResponse<AnimalDocument> response =
                elasticsearchClient.search(searchRequest, AnimalDocument.class);

        List<Animal> content = new ArrayList<>();
        for (Hit<AnimalDocument> hit : response.hits().hits()) {
            if (hit.source() != null) {
                content.add(documentMapper.toAnimal(hit.source()));
            }
        }
        long total = response.hits().total() == null ? content.size() : response.hits().total().value();
        return PageResponse.of(content, request.getPage(), request.getSize(), total, request.getSort());
    }

    private AnimalFacets facetsElasticsearch() throws Exception {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(animalIndexName)
                .size(0)
                .query(q -> q.matchAll(m -> m))
                .aggregations("letters", a -> a.terms(t -> t.field("nameLetter").size(26)))
                .aggregations("species", a -> a.terms(t -> t.field("speciesKeyword").size(50)))
                .aggregations("habitats", a -> a.terms(t -> t.field("habitatKeyword").size(40)))
                .aggregations("biomes", a -> a.terms(t -> t.field("biomes").size(20)))
                .aggregations("climates", a -> a.terms(t -> t.field("climateZones").size(20)))
                .aggregations("statuses", a -> a.terms(t -> t.field("conservationStatus").size(20)))
        );

        SearchResponse<AnimalDocument> response =
                elasticsearchClient.search(searchRequest, AnimalDocument.class);

        long total = response.hits().total() == null ? 0 : response.hits().total().value();
        return AnimalFacets.builder()
                .totalAnimals(total)
                .letters(toBuckets(response, "letters"))
                .species(toBuckets(response, "species"))
                .habitats(toBuckets(response, "habitats"))
                .biomes(toBuckets(response, "biomes"))
                .climates(toBuckets(response, "climates"))
                .statuses(toBuckets(response, "statuses"))
                .build();
    }

    private List<AnimalFacets.FacetBucket> toBuckets(SearchResponse<AnimalDocument> response, String name) {
        if (response.aggregations() == null || !response.aggregations().containsKey(name)) {
            return List.of();
        }
        var aggregate = response.aggregations().get(name);
        if (aggregate == null || !aggregate.isSterms()) {
            return List.of();
        }
        List<AnimalFacets.FacetBucket> result = new ArrayList<>();
        for (StringTermsBucket bucket : aggregate.sterms().buckets().array()) {
            String key = bucket.key().stringValue();
            if (StringUtils.isBlank(key)) {
                continue;
            }
            result.add(AnimalFacets.FacetBucket.builder()
                    .key(key)
                    .label(key)
                    .count(bucket.docCount())
                    .build());
        }
        return result;
    }

    private Query buildQuery(AnimalSearchRequest request) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        boolean filtered = false;

        if (StringUtils.isNotBlank(request.getQ())) {
            String q = request.getQ().trim();
            bool.must(m -> m.multiMatch(mm -> mm
                    .query(q)
                    .fields("name^3", "scientificName^2", "species", "genus", "family", "description",
                            "habitat", "diet", "behavior", "phylum")
            ));
            filtered = true;
        }
        if (StringUtils.isNotBlank(request.getLetter())) {
            bool.filter(f -> f.term(t -> t.field("nameLetter").value(request.getLetter().toUpperCase(Locale.ROOT))));
            filtered = true;
        }
        if (StringUtils.isNotBlank(request.getSpecies())) {
            bool.filter(f -> f.term(t -> t.field("speciesKeyword").value(request.getSpecies())));
            filtered = true;
        }
        if (StringUtils.isNotBlank(request.getHabitat())) {
            bool.filter(f -> f.term(t -> t.field("habitatKeyword").value(request.getHabitat())));
            filtered = true;
        }
        if (StringUtils.isNotBlank(request.getBiome())) {
            bool.filter(f -> f.term(t -> t.field("biomes").value(request.getBiome().toUpperCase(Locale.ROOT))));
            filtered = true;
        }
        if (StringUtils.isNotBlank(request.getClimate())) {
            bool.filter(f -> f.term(t -> t.field("climateZones").value(request.getClimate())));
            filtered = true;
        }
        if (StringUtils.isNotBlank(request.getStatus())) {
            bool.filter(f -> f.term(t -> t.field("conservationStatus")
                    .value(request.getStatus().toUpperCase(Locale.ROOT))));
            filtered = true;
        }

        if (!filtered) {
            return Query.of(q -> q.matchAll(m -> m));
        }
        return Query.of(q -> q.bool(bool.build()));
    }

    private AnimalSearchRequest normalize(AnimalSearchRequest request) {
        AnimalSearchRequest req = request == null ? new AnimalSearchRequest() : request;
        int size = req.getSize() <= 0 ? defaultPageSize : Math.min(req.getSize(), maxPageSize);
        int page = Math.max(req.getPage(), 0);
        String sort = StringUtils.defaultIfBlank(req.getSort(), "name,asc");
        return AnimalSearchRequest.builder()
                .q(StringUtils.trimToNull(req.getQ()))
                .letter(StringUtils.trimToNull(req.getLetter()))
                .species(StringUtils.trimToNull(req.getSpecies()))
                .habitat(StringUtils.trimToNull(req.getHabitat()))
                .biome(StringUtils.trimToNull(req.getBiome()))
                .climate(StringUtils.trimToNull(req.getClimate()))
                .status(StringUtils.trimToNull(req.getStatus()))
                .page(page)
                .size(size)
                .sort(sort)
                .build();
    }

    private String[] parseSort(String sort) {
        if (StringUtils.isBlank(sort)) {
            return new String[]{"name", "asc"};
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim() : "asc";
        return new String[]{field, direction};
    }

    private String mapSortField(String field) {
        return switch (field.toLowerCase(Locale.ROOT)) {
            case "scientificname", "scientific" -> "scientificNameKeyword";
            case "species" -> "speciesKeyword";
            case "family" -> "family";
            case "status", "conservationstatus" -> "conservationStatus";
            default -> "nameKeyword";
        };
    }
}
