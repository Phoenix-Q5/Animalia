package com.animal.java.service.impl;

import com.animal.java.dto.ParsedTemplateRow;
import com.animal.java.exception.AnimaliaException;
import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.AnimalExtendedInfo;
import com.animal.java.model.animal.AnimalInfo;
import com.animal.java.model.animal.AnimalReference;
import com.animal.java.model.animal.Image;
import com.animal.java.model.animal.Subspecies;
import com.animal.java.model.distribution.Biome;
import com.animal.java.model.distribution.ClimateZones;
import com.animal.java.model.distribution.Distribution;
import com.animal.java.model.distribution.DistributionLocation;
import com.animal.java.model.distribution.Population;
import com.animal.java.model.enumConstants.BiomeId;
import com.animal.java.model.enumConstants.ClimateZone;
import com.animal.java.model.enumConstants.LocationType;
import com.animal.java.model.enumConstants.PopulationTrend;
import com.animal.java.model.enumConstants.VulnerabilityStatus;
import com.animal.java.model.info.Appearance;
import com.animal.java.model.info.Habits;
import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnimalTemplateParser {

    /**
     * Flat CSV / Excel columns. Pipe ({@code |}) separates multi-value fields.
     * Nested JSON uploads may use the full {@link Animal} object shape instead.
     */
    private static final String[] CANONICAL_HEADERS = {
            "name", "scientificName", "phylum", "family", "genus", "species",
            "order", "kingdom", "subphylum", "animalClass", "suborder", "description",
            "populationSize", "lifeSpan", "topSpeed", "weight", "length", "wingSpan",
            "diet", "habitat", "behavior", "habitsLifestyle",
            "appearanceDescription", "habitsDescription", "habitsSeasonality",
            "habitsGroupName", "habitsLifestyleTags",
            "distributionDescription",
            "continents", "subcontinents", "countries", "biogeographicalRealms", "wwfBiomes",
            "biomes", "climateZones",
            "populationThreats", "populationNumber", "ecologicalNiche",
            "populationTrend", "populationStatus", "conservationStatus",
            "imageUrls", "appearanceImageUrls", "distributionMapUrl",
            "subspeciesName", "subspeciesScientificName", "subspeciesDescription",
            "subspeciesJson",
            "referenceTitle", "referenceUrl", "referenceCitation",
            "referencesJson"
    };

    private final ObjectMapper objectMapper;

    public List<ParsedTemplateRow> parse(MultipartFile file) {
        try {
            String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase(Locale.ROOT);
            byte[] bytes = file.getBytes();
            if (filename.endsWith(".json") || looksLikeJson(bytes)) {
                return parseJson(bytes);
            }
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                return parseExcel(bytes);
            }
            return parseCsv(bytes);
        } catch (AnimaliaException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AnimaliaException("Unable to read the template file");
        }
    }

    public byte[] canonicalTemplate() {
        String header = String.join(",", CANONICAL_HEADERS);
        String example = String.join(",",
                csv("Bengal tiger"),
                csv("Panthera tigris tigris"),
                csv("Chordata"),
                csv("Felidae"),
                csv("Panthera"),
                csv("tigris"),
                csv("Carnivora"),
                csv("Animalia"),
                csv("Vertebrata"),
                csv("Mammalia"),
                csv("Feliformia"),
                csv("The largest living cat species"),
                csv("3900"),
                csv("10-15 years"),
                csv("65 km/h"),
                csv("220"),
                csv("2.8 m"),
                csv(""),
                csv("Carnivore — deer, wild boar"),
                csv("Tropical moist deciduous forests"),
                csv("Solitary territorial apex predator"),
                csv("Mostly nocturnal; strong swimmer"),
                csv("Orange coat with black vertical stripes"),
                csv("Solitary hunter; marks territory with scent"),
                csv("Year-round"),
                csv("solitary"),
                csv("nocturnal|terrestrial"),
                csv("Indian subcontinent"),
                csv("Asia"),
                csv("Indian subcontinent"),
                csv("India|Bangladesh|Nepal|Bhutan"),
                csv("Indomalayan"),
                csv("Tropical and subtropical moist broadleaf forests"),
                csv("FOREST|GRASSLAND|SWAMP|RIPARIAN"),
                csv("Tropical|Temperate"),
                csv("Poaching and habitat loss"),
                csv("~3900 mature individuals"),
                csv("Apex predator"),
                csv("DECREASING"),
                csv("Protected"),
                csv("EN"),
                csv("https://example.com/tiger-1.jpg|https://example.com/tiger-2.jpg"),
                csv(""),
                csv(""),
                csv("Bengal tiger"),
                csv("Panthera tigris tigris"),
                csv("Nominate subspecies"),
                csv(""),
                csv("IUCN Red List"),
                csv("https://www.iucnredlist.org/"),
                csv("IUCN 2023"),
                csv("")
        );
        return (header + "\n" + example + "\n").getBytes(StandardCharsets.UTF_8);
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private List<ParsedTemplateRow> parseCsv(byte[] bytes) throws Exception {
        String content = new String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "");
        if (StringUtils.isBlank(content)) {
            throw new AnimaliaException("Template file is empty");
        }
        String firstLine = content.lines().filter(StringUtils::isNotBlank).findFirst().orElse("");
        char delimiter = detectDelimiter(firstLine);
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter)
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .setIgnoreHeaderCase(true)
                .build();
        try (CSVParser parser = CSVParser.parse(content, format)) {
            Map<String, String> headerMap = resolveHeaders(parser.getHeaderMap().keySet());
            assertRecognized(headerMap);
            List<ParsedTemplateRow> rows = new ArrayList<>();
            int rowNumber = 1;
            for (CSVRecord record : parser) {
                rowNumber++;
                Map<String, String> values = new LinkedHashMap<>();
                for (Map.Entry<String, String> header : headerMap.entrySet()) {
                    if (record.isMapped(header.getKey())) {
                        values.put(header.getValue(), record.get(header.getKey()));
                    }
                }
                if (values.values().stream().allMatch(StringUtils::isBlank)) {
                    continue;
                }
                rows.add(toRow(rowNumber, values));
            }
            return rows;
        }
    }

    private List<ParsedTemplateRow> parseExcel(byte[] bytes) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new AnimaliaException("Template file is empty");
            }
            DataFormatter formatter = new DataFormatter();
            Iterator<Row> iterator = sheet.iterator();
            if (!iterator.hasNext()) {
                throw new AnimaliaException("Template file is empty");
            }
            Row headerRow = iterator.next();
            List<String> rawHeaders = new ArrayList<>();
            headerRow.forEach(cell -> rawHeaders.add(formatter.formatCellValue(cell)));
            Map<String, String> headerMap = resolveHeaders(rawHeaders);
            assertRecognized(headerMap);
            List<String> originalHeaders = new ArrayList<>(rawHeaders);
            List<ParsedTemplateRow> rows = new ArrayList<>();
            while (iterator.hasNext()) {
                Row excelRow = iterator.next();
                Map<String, String> values = new LinkedHashMap<>();
                boolean empty = true;
                for (int i = 0; i < originalHeaders.size(); i++) {
                    String rawHeader = originalHeaders.get(i);
                    String canonical = headerMap.get(rawHeader);
                    if (canonical == null) {
                        continue;
                    }
                    String value = formatter.formatCellValue(excelRow.getCell(i));
                    if (StringUtils.isNotBlank(value)) {
                        empty = false;
                    }
                    values.put(canonical, value);
                }
                if (empty) {
                    continue;
                }
                rows.add(toRow(excelRow.getRowNum() + 1, values));
            }
            return rows;
        }
    }

    private List<ParsedTemplateRow> parseJson(byte[] bytes) throws Exception {
        JsonNode root = objectMapper.readTree(bytes);
        JsonNode array = root;
        if (root.isObject() && root.has("animals")) {
            array = root.get("animals");
        }
        List<JsonNode> nodes = new ArrayList<>();
        if (array.isArray()) {
            array.forEach(nodes::add);
        } else if (root.isObject()) {
            nodes.add(root);
        } else {
            throw new AnimaliaException("JSON template must be an object or an array");
        }
        List<ParsedTemplateRow> rows = new ArrayList<>();
        int rowNumber = 0;
        boolean recognized = false;
        for (JsonNode node : nodes) {
            rowNumber++;
            if (isNestedAnimalNode(node)) {
                recognized = true;
                Animal animal = objectMapper.treeToValue(node, Animal.class);
                if (animal == null) {
                    animal = Animal.builder().build();
                }
                rows.add(ParsedTemplateRow.builder().rowNumber(rowNumber).animal(animal).build());
                continue;
            }
            Map<String, String> values = new LinkedHashMap<>();
            Map<String, String> headerMap = resolveHeaders(toList(node.fieldNames()));
            if (!headerMap.isEmpty()) {
                recognized = true;
            }
            for (Map.Entry<String, String> header : headerMap.entrySet()) {
                JsonNode value = node.get(header.getKey());
                values.put(header.getValue(), scalarText(value));
            }
            rows.add(toRow(rowNumber, values));
        }
        if (!recognized) {
            throw new AnimaliaException(
                    "Unrecognized template. Include columns such as name, scientificName, phylum, family, and species");
        }
        return rows;
    }

    private boolean isNestedAnimalNode(JsonNode node) {
        if (node == null || !node.isObject()) {
            return false;
        }
        return node.has("animalInfo")
                || node.has("animalExtendedInfo")
                || node.has("animalImages")
                || node.has("subspecies")
                || node.has("references")
                || node.has("galleryImages");
    }

    private String scalarText(JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isArray()) {
            List<String> parts = new ArrayList<>();
            value.forEach(item -> {
                if (item != null && !item.isNull()) {
                    parts.add(item.isTextual() ? item.asText() : item.toString());
                }
            });
            return String.join("|", parts);
        }
        if (value.isObject()) {
            return value.toString();
        }
        return value.asText();
    }

    private List<String> toList(Iterator<String> iterator) {
        List<String> values = new ArrayList<>();
        iterator.forEachRemaining(values::add);
        return values;
    }

    private ParsedTemplateRow toRow(int rowNumber, Map<String, String> values) {
        Animal animal = buildAnimalFromFlat(values);
        return ParsedTemplateRow.builder()
                .rowNumber(rowNumber)
                .animal(animal)
                .build();
    }

    private Animal buildAnimalFromFlat(Map<String, String> values) {
        AnimalExtendedInfo extendedInfo = AnimalExtendedInfo.builder()
                .populationSize(trim(values.get("populationSize")))
                .lifeSpan(trim(values.get("lifeSpan")))
                .topSpeed(trim(values.get("topSpeed")))
                .weight(parseWeight(values.get("weight")))
                .length(trim(values.get("length")))
                .wingSpan(trim(values.get("wingSpan")))
                .build();

        AnimalInfo animalInfo = buildAnimalInfo(values);
        List<Image> images = new ArrayList<>(toImages(splitList(values.get("imageUrls"))));

        return Animal.builder()
                .name(trim(values.get("name")))
                .scientificName(trim(values.get("scientificName")))
                .phylum(trim(values.get("phylum")))
                .family(trim(values.get("family")))
                .genus(trim(values.get("genus")))
                .species(trim(values.get("species")))
                .order(trim(values.get("order")))
                .kingdom(trim(values.get("kingdom")))
                .subphylum(trim(values.get("subphylum")))
                .animalClass(trim(values.get("animalClass")))
                .suborder(trim(values.get("suborder")))
                .description(trim(values.get("description")))
                .animalExtendedInfo(extendedInfo)
                .animalInfo(animalInfo)
                .animalImages(images)
                .subspecies(buildSubspecies(values))
                .references(buildReferences(values))
                .build();
    }

    private AnimalInfo buildAnimalInfo(Map<String, String> values) {
        Appearance appearance = Appearance.builder()
                .description(trim(values.get("appearanceDescription")))
                .images(toImages(splitList(values.get("appearanceImageUrls"))))
                .build();

        Set<String> groupNames = new LinkedHashSet<>(splitList(values.get("habitsGroupName")));
        Set<String> lifestyle = new LinkedHashSet<>(splitList(values.get("habitsLifestyleTags")));
        Habits habits = Habits.builder()
                .description(trim(values.get("habitsDescription")))
                .groupName(groupNames)
                .lifestyle(lifestyle)
                .seasonality(trim(values.get("habitsSeasonality")))
                .build();

        Distribution distribution = buildDistribution(values);
        Biome biome = buildBiome(values.get("biomes"));
        ClimateZones climateZones = buildClimateZones(values.get("climateZones"));
        Population population = buildPopulation(values);

        return AnimalInfo.builder()
                .diet(trim(values.get("diet")))
                .habitat(trim(values.get("habitat")))
                .behavior(trim(values.get("behavior")))
                .habitsLifestyle(trim(values.get("habitsLifestyle")))
                .appearance(appearance)
                .habits(habits)
                .distribution(distribution)
                .biome(biome)
                .climateZones(climateZones)
                .population(population)
                .build();
    }

    private Distribution buildDistribution(Map<String, String> values) {
        List<DistributionLocation> locations = new ArrayList<>();
        addLocations(locations, LocationType.CONTINENT, splitList(values.get("continents")));
        addLocations(locations, LocationType.SUBCONTINENT, splitList(values.get("subcontinents")));
        addLocations(locations, LocationType.COUNTRY, splitList(values.get("countries")));
        addLocations(locations, LocationType.BIOGEOGRAPHICAL_REALM, splitList(values.get("biogeographicalRealms")));
        addLocations(locations, LocationType.WWF_BIOME, splitList(values.get("wwfBiomes")));

        Image map = toImage(trim(values.get("distributionMapUrl")));

        boolean hasContent = StringUtils.isNotBlank(values.get("distributionDescription"))
                || !locations.isEmpty()
                || map != null;
        if (!hasContent) {
            return null;
        }

        return Distribution.builder()
                .description(trim(values.get("distributionDescription")))
                .locations(locations)
                .distributionMap(map)
                .build();
    }

    private void addLocations(List<DistributionLocation> target, LocationType type, List<String> values) {
        for (String value : values) {
            target.add(DistributionLocation.builder()
                    .locationType(type)
                    .value(value)
                    .build());
        }
    }

    private Biome buildBiome(String raw) {
        List<BiomeId> biomes = splitList(raw).stream()
                .map(v -> EnumId.parseId(BiomeId.class, v, null))
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (biomes.isEmpty()) {
            return null;
        }
        return Biome.builder().biomes(biomes).build();
    }

    private ClimateZones buildClimateZones(String raw) {
        List<ClimateZone> zones = splitList(raw).stream()
                .map(v -> EnumId.parseId(ClimateZone.class, v, null))
                .filter(zone -> zone != null)
                .collect(Collectors.toList());
        if (zones.isEmpty()) {
            return null;
        }
        return ClimateZones.builder().zones(zones).build();
    }

    private Population buildPopulation(Map<String, String> values) {
        String threats = trim(values.get("populationThreats"));
        String number = trim(values.get("populationNumber"));
        String niche = trim(values.get("ecologicalNiche"));
        String trendRaw = trim(values.get("populationTrend"));
        String status = trim(values.get("populationStatus"));
        String conservationRaw = trim(values.get("conservationStatus"));

        PopulationTrend trend = trendRaw == null ? null : EnumId.parseId(PopulationTrend.class, trendRaw, null);
        VulnerabilityStatus conservation = conservationRaw == null
                ? null
                : EnumId.parseId(VulnerabilityStatus.class, conservationRaw, null);

        if (threats == null && number == null && niche == null && trend == null
                && status == null && conservation == null) {
            return null;
        }

        return Population.builder()
                .populationThreats(threats)
                .populationNumber(number)
                .ecologicalNiche(niche)
                .populationTrend(trend)
                .populationStatus(status)
                .conservationStatus(conservation)
                .build();
    }

    private List<Subspecies> buildSubspecies(Map<String, String> values) {
        List<Subspecies> list = new ArrayList<>();
        String json = trim(values.get("subspeciesJson"));
        if (json != null) {
            list.addAll(parseJsonList(json, Subspecies.class));
        }
        String name = trim(values.get("subspeciesName"));
        if (name != null) {
            list.add(Subspecies.builder()
                    .name(name)
                    .scientificName(trim(values.get("subspeciesScientificName")))
                    .description(trim(values.get("subspeciesDescription")))
                    .build());
        }
        return list;
    }

    private List<AnimalReference> buildReferences(Map<String, String> values) {
        List<AnimalReference> list = new ArrayList<>();
        String json = trim(values.get("referencesJson"));
        if (json != null) {
            list.addAll(parseJsonList(json, AnimalReference.class));
        }
        String title = trim(values.get("referenceTitle"));
        String url = trim(values.get("referenceUrl"));
        if (title != null || url != null) {
            list.add(AnimalReference.builder()
                    .title(title)
                    .url(url)
                    .citation(trim(values.get("referenceCitation")))
                    .sortOrder(0)
                    .build());
        }
        return list;
    }

    private <T> List<T> parseJsonList(String json, Class<T> type) {
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.isArray()) {
                List<T> items = new ArrayList<>();
                for (JsonNode child : node) {
                    items.add(objectMapper.treeToValue(child, type));
                }
                return items;
            }
            T single = objectMapper.treeToValue(node, type);
            return single == null ? List.of() : List.of(single);
        } catch (Exception ex) {
            return List.of();
        }
    }

    private List<Image> toImages(List<String> urls) {
        List<Image> images = new ArrayList<>();
        for (String url : urls) {
            Image image = toImage(url);
            if (image != null) {
                images.add(image);
            }
        }
        return images;
    }

    private Image toImage(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String trimmed = url.trim();
        String name = trimmed;
        int slash = trimmed.lastIndexOf('/');
        if (slash >= 0 && slash < trimmed.length() - 1) {
            name = trimmed.substring(slash + 1);
        }
        int query = name.indexOf('?');
        if (query > 0) {
            name = name.substring(0, query);
        }
        return Image.builder()
                .url(trimmed)
                .name(StringUtils.defaultIfBlank(name, "cdn-image"))
                .type("image/jpeg")
                .build();
    }

    private List<String> splitList(String raw) {
        if (StringUtils.isBlank(raw)) {
            return List.of();
        }
        String normalized = raw.trim();
        String delimiter = normalized.contains("|") ? "\\|" : ";";
        String[] parts = normalized.split(delimiter);
        List<String> values = new ArrayList<>();
        for (String part : parts) {
            String trimmed = StringUtils.trimToNull(part);
            if (trimmed != null) {
                values.add(trimmed);
            }
        }
        return values;
    }

    private Map<String, String> resolveHeaders(Iterable<String> headers) {
        Map<String, String> resolved = new LinkedHashMap<>();
        for (String header : headers) {
            if (StringUtils.isBlank(header)) {
                continue;
            }
            String canonical = canonicalize(header);
            if (canonical != null) {
                resolved.put(header, canonical);
            }
        }
        return resolved;
    }

    private void assertRecognized(Map<String, String> headerMap) {
        if (headerMap.isEmpty()) {
            throw new AnimaliaException(
                    "Unrecognized template. Include columns such as name, scientificName, phylum, family, and species");
        }
    }

    private String canonicalize(String header) {
        String normalized = normalize(header);
        return switch (normalized) {
            case "name", "commonname", "animalname", "animal", "common" -> "name";
            case "scientificname", "scientific", "latinname", "latin", "binomial" -> "scientificName";
            case "phylum" -> "phylum";
            case "family" -> "family";
            case "genus" -> "genus";
            case "species" -> "species";
            case "order", "animalorder" -> "order";
            case "kingdom" -> "kingdom";
            case "subphylum" -> "subphylum";
            case "animalclass", "class", "classis" -> "animalClass";
            case "suborder" -> "suborder";
            case "description", "desc", "about", "summary" -> "description";
            case "populationsize", "population" -> "populationSize";
            case "lifespan", "life" -> "lifeSpan";
            case "topspeed", "speed" -> "topSpeed";
            case "weight", "mass" -> "weight";
            case "length", "size" -> "length";
            case "wingspan" -> "wingSpan";
            case "diet" -> "diet";
            case "habitat" -> "habitat";
            case "behavior", "behaviour" -> "behavior";
            case "habitslifestyle", "lifestyletext" -> "habitsLifestyle";
            case "appearancedescription", "appearance", "appearancedesc" -> "appearanceDescription";
            case "habitsdescription", "habits", "habitsdesc" -> "habitsDescription";
            case "habitsseasonality", "seasonality" -> "habitsSeasonality";
            case "habitsgroupname", "groupname", "groupnames" -> "habitsGroupName";
            case "habitslifestyletags", "lifestyletags", "lifestyle" -> "habitsLifestyleTags";
            case "distributiondescription", "distribution", "range", "distributiondesc" -> "distributionDescription";
            case "continents", "continent" -> "continents";
            case "subcontinents", "subcontinent" -> "subcontinents";
            case "countries", "country" -> "countries";
            case "biogeographicalrealms", "biogeographicalrealm", "realms", "realm" -> "biogeographicalRealms";
            case "wwfbiomes", "wwfbiome" -> "wwfBiomes";
            case "biomes", "biome" -> "biomes";
            case "climatezones", "climatezone", "climates", "climate" -> "climateZones";
            case "populationthreats", "threats" -> "populationThreats";
            case "populationnumber", "populationcount" -> "populationNumber";
            case "ecologicalniche", "niche" -> "ecologicalNiche";
            case "populationtrend", "trend" -> "populationTrend";
            case "populationstatus" -> "populationStatus";
            case "conservationstatus", "iucn", "iucnstatus", "redlist" -> "conservationStatus";
            case "imageurls", "images", "gallery", "galleryurls", "animalimages", "photourls" -> "imageUrls";
            case "appearanceimageurls", "appearanceimages" -> "appearanceImageUrls";
            case "distributionmapurl", "mapurl", "distributionmap" -> "distributionMapUrl";
            case "subspeciesname" -> "subspeciesName";
            case "subspeciesscientificname" -> "subspeciesScientificName";
            case "subspeciesdescription" -> "subspeciesDescription";
            case "subspeciesjson", "subspecies" -> "subspeciesJson";
            case "referencetitle" -> "referenceTitle";
            case "referenceurl" -> "referenceUrl";
            case "referencecitation" -> "referenceCitation";
            case "referencesjson", "references" -> "referencesJson";
            default -> null;
        };
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String trim(String value) {
        return StringUtils.trimToNull(value);
    }

    private double parseWeight(String value) {
        if (StringUtils.isBlank(value)) {
            return 0d;
        }
        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (NumberFormatException ex) {
            return 0d;
        }
    }

    private char detectDelimiter(String headerLine) {
        long commas = headerLine.chars().filter(ch -> ch == ',').count();
        long semis = headerLine.chars().filter(ch -> ch == ';').count();
        long tabs = headerLine.chars().filter(ch -> ch == '\t').count();
        if (tabs >= commas && tabs >= semis && tabs > 0) {
            return '\t';
        }
        if (semis > commas) {
            return ';';
        }
        return ',';
    }

    private boolean looksLikeJson(byte[] bytes) {
        String trimmed = new String(bytes, StandardCharsets.UTF_8).stripLeading();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }
}
