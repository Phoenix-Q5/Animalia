package com.animal.java.service.impl;

import com.animal.java.dto.BulkImportResult;
import com.animal.java.dto.BulkImportRowResult;
import com.animal.java.dto.ParsedTemplateRow;
import com.animal.java.exception.AnimaliaException;
import com.animal.java.model.animal.Animal;
import com.animal.java.service.AnimalBulkImportService;
import com.animal.java.service.AnimalService;
import com.animal.java.validation.ValidationNotification;
import com.animal.java.validation.pipeline.ValidationPipeline;
import com.animal.java.validation.pipeline.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalBulkImportServiceImpl implements AnimalBulkImportService {

    private final AnimalTemplateParser parser;
    private final ValidationPipeline validationPipeline;
    private final AnimalService animalService;

    @Override
    public BulkImportResult importAnimals(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return BulkImportResult.builder()
                    .totalRows(0)
                    .imported(0)
                    .failed(0)
                    .fileError("A template file is required")
                    .rows(List.of())
                    .build();
        }

        List<ParsedTemplateRow> parsedRows;
        try {
            parsedRows = parser.parse(file);
        } catch (AnimaliaException ex) {
            return BulkImportResult.builder()
                    .totalRows(0)
                    .imported(0)
                    .failed(0)
                    .fileError(ex.getMessage())
                    .rows(List.of())
                    .build();
        }

        if (parsedRows.isEmpty()) {
            return BulkImportResult.builder()
                    .totalRows(0)
                    .imported(0)
                    .failed(0)
                    .fileError("The template did not contain any animal rows")
                    .rows(List.of())
                    .build();
        }

        Set<String> namesInFile = new HashSet<>();
        Set<String> scientificNamesInFile = new HashSet<>();
        List<BulkImportRowResult> rowResults = new ArrayList<>();
        int imported = 0;
        int failed = 0;

        for (ParsedTemplateRow parsedRow : parsedRows) {
            Animal animal = parsedRow.getAnimal();
            List<String> errors = new ArrayList<>();

            ValidationResult validationResult = validationPipeline.validate(animal);
            if (!validationResult.isSucceeded()) {
                errors.addAll(validationResult.getNotifications().stream()
                        .map(this::toMessage)
                        .collect(Collectors.toList()));
            }

            String nameKey = normalizeKey(animal.getName());
            String scientificKey = normalizeKey(animal.getScientificName());
            if (nameKey != null && !namesInFile.add(nameKey)) {
                errors.add("Duplicate name in this file");
            }
            if (scientificKey != null && !scientificNamesInFile.add(scientificKey)) {
                errors.add("Duplicate scientific name in this file");
            }

            boolean success = errors.isEmpty();
            if (success) {
                try {
                    animalService.insertAnimal(animal);
                    imported++;
                } catch (Exception ex) {
                    success = false;
                    errors.add("Could not save this animal");
                    failed++;
                }
            } else {
                failed++;
            }

            rowResults.add(BulkImportRowResult.builder()
                    .rowNumber(parsedRow.getRowNumber())
                    .name(animal.getName())
                    .success(success)
                    .errors(errors)
                    .build());
        }

        return BulkImportResult.builder()
                .totalRows(parsedRows.size())
                .imported(imported)
                .failed(failed)
                .rows(rowResults)
                .build();
    }

    @Override
    public byte[] canonicalTemplate() {
        return parser.canonicalTemplate();
    }

    private String toMessage(ValidationNotification notification) {
        if (StringUtils.isNotBlank(notification.getAttribute())) {
            return notification.getAttribute() + ": " + notification.getMessage();
        }
        return notification.getMessage();
    }

    private String normalizeKey(String value) {
        return StringUtils.isBlank(value) ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
