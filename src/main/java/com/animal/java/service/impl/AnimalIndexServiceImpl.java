package com.animal.java.service.impl;

import com.animal.java.dao.AnimalRepository;
import com.animal.java.model.animal.Animal;
import com.animal.java.search.AnimalDocument;
import com.animal.java.search.AnimalDocumentMapper;
import com.animal.java.search.AnimalSearchRepository;
import com.animal.java.service.AnimalIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalIndexServiceImpl implements AnimalIndexService {

    private static final int BATCH = 200;

    private final AnimalSearchRepository animalSearchRepository;
    private final AnimalDocumentMapper documentMapper;
    private final AnimalRepository animalRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void index(Animal animal) {
        if (animal == null || animal.getId() == null) {
            return;
        }
        try {
            AnimalDocument document = documentMapper.toDocument(animal);
            if (document != null) {
                animalSearchRepository.save(document);
            }
        } catch (Exception ex) {
            log.warn("Failed to index animal {}: {}", animal.getId(), ex.getMessage());
        }
    }

    @Override
    public void delete(UUID animalId) {
        if (animalId == null) {
            return;
        }
        try {
            animalSearchRepository.deleteById(animalId.toString());
        } catch (Exception ex) {
            log.warn("Failed to delete animal {} from index: {}", animalId, ex.getMessage());
        }
    }

    @Override
    public long reindexAll() {
        recreateIndex();
        List<UUID> ids = animalRepository.findAllIds();
        long indexed = 0;
        List<AnimalDocument> batch = new ArrayList<>(BATCH);
        for (UUID id : ids) {
            try {
                Animal animal = animalRepository.getById(id);
                if (animal == null) {
                    continue;
                }
                AnimalDocument document = documentMapper.toDocument(animal);
                if (document == null) {
                    continue;
                }
                batch.add(document);
                if (batch.size() >= BATCH) {
                    animalSearchRepository.saveAll(batch);
                    indexed += batch.size();
                    batch.clear();
                }
            } catch (Exception ex) {
                log.warn("Reindex skipped for {}: {}", id, ex.getMessage());
            }
        }
        if (!batch.isEmpty()) {
            animalSearchRepository.saveAll(batch);
            indexed += batch.size();
        }
        log.info("Reindexed {} animals into Elasticsearch", indexed);
        return indexed;
    }

    private void recreateIndex() {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(AnimalDocument.class);
            if (indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
            log.info("Recreated Elasticsearch index for AnimalDocument");
        } catch (Exception ex) {
            log.warn("Could not recreate Elasticsearch index: {}", ex.getMessage());
        }
    }
}
