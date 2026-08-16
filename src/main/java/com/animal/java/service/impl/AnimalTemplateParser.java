package com.animal.java.service.impl;

import com.animal.java.dto.ParsedTemplateRow;
import com.animal.java.exception.AnimaliaException;
import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.AnimalExtendedInfo;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnimalTemplateParser {

    private static final String[] CANONICAL_HEADERS = {
            "name", "scientificName", "phylum", "family", "genus", "species",
            "order", "kingdom", "description", "populationSize", "lifeSpan",
            "topSpeed", "weight", "length", "wingSpan"
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
                "Bengal tiger",
                "Panthera tigris tigris",
                "Chordata",
                "Felidae",
                "Panthera",
                "tigris",
                "Carnivora",
                "Animal",
                "The largest living cat species",
                "3900",
                "10-15 years",
                "65 km/h",
                "220",
                "2.8 m",
                ""
        );
        return (header + "\n" + example + "\n").getBytes(StandardCharsets.UTF_8);
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
            Map<String, String> values = new LinkedHashMap<>();
            Map<String, String> headerMap = resolveHeaders(toList(node.fieldNames()));
            if (!headerMap.isEmpty()) {
                recognized = true;
            }
            for (Map.Entry<String, String> header : headerMap.entrySet()) {
                JsonNode value = node.get(header.getKey());
                values.put(header.getValue(), value == null || value.isNull() ? null : value.asText());
            }
            rows.add(toRow(rowNumber, values));
        }
        if (!recognized) {
            throw new AnimaliaException(
                    "Unrecognized template. Include columns such as name, scientificName, phylum, family, and species");
        }
        return rows;
    }

    private List<String> toList(Iterator<String> iterator) {
        List<String> values = new ArrayList<>();
        iterator.forEachRemaining(values::add);
        return values;
    }

    private ParsedTemplateRow toRow(int rowNumber, Map<String, String> values) {
        AnimalExtendedInfo extendedInfo = AnimalExtendedInfo.builder()
                .populationSize(trim(values.get("populationSize")))
                .lifeSpan(trim(values.get("lifeSpan")))
                .topSpeed(trim(values.get("topSpeed")))
                .weight(parseWeight(values.get("weight")))
                .length(trim(values.get("length")))
                .wingSpan(trim(values.get("wingSpan")))
                .build();
        Animal animal = Animal.builder()
                .name(trim(values.get("name")))
                .scientificName(trim(values.get("scientificName")))
                .phylum(trim(values.get("phylum")))
                .family(trim(values.get("family")))
                .genus(trim(values.get("genus")))
                .species(trim(values.get("species")))
                .order(trim(values.get("order")))
                .kingdom(trim(values.get("kingdom")))
                .description(trim(values.get("description")))
                .animalExtendedInfo(extendedInfo)
                .build();
        return ParsedTemplateRow.builder()
                .rowNumber(rowNumber)
                .animal(animal)
                .build();
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
            case "description", "desc", "about", "summary" -> "description";
            case "populationsize", "population" -> "populationSize";
            case "lifespan", "life" -> "lifeSpan";
            case "topspeed", "speed" -> "topSpeed";
            case "weight", "mass" -> "weight";
            case "length", "size" -> "length";
            case "wingspan" -> "wingSpan";
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
