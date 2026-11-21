package com.animal.java.service.impl;

import com.animal.java.dto.BulkImportResult;
import com.animal.java.model.animal.Animal;
import com.animal.java.service.AnimalService;
import com.animal.java.validation.pipeline.ValidationPipeline;
import com.animal.java.validation.pipeline.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnimalBulkImportServiceImplTest {
    @Test
    void importsMoreThanOneThousandRowsAndReportsDuplicates() {
        ValidationPipeline validation = mock(ValidationPipeline.class);
        AnimalService animals = mock(AnimalService.class);
        when(validation.validate(any())).thenReturn(new ValidationResult());
        when(animals.insertAnimal(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AnimalBulkImportServiceImpl importer = new AnimalBulkImportServiceImpl(
                new AnimalTemplateParser(new ObjectMapper()), validation, animals);
        StringBuilder csv = new StringBuilder("name,scientificName\n");
        for (int i = 0; i < 1001; i++) {
            csv.append("Animal ").append(i).append(",Scientific ").append(i).append('\n');
        }
        csv.append("Animal 0,Another name\n");

        BulkImportResult result = importer.importAnimals("animals.csv", csv.toString().getBytes(StandardCharsets.UTF_8));

        assertThat(result.getTotalRows()).isEqualTo(1002);
        assertThat(result.getImported()).isEqualTo(1001);
        assertThat(result.getFailed()).isEqualTo(1);
        assertThat(result.getRows().get(1001).getErrors()).contains("Duplicate name in this file");
        verify(animals, times(1001)).insertAnimal(any(Animal.class));
    }
}
