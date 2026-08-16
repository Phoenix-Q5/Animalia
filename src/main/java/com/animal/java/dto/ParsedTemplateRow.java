package com.animal.java.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParsedTemplateRow {

    private final int rowNumber;
    private final com.animal.java.model.animal.Animal animal;
}
