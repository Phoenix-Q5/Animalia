package com.animal.java.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BulkImportRowResult {

    private final int rowNumber;
    private final String name;
    private final boolean success;
    private final List<String> errors;
}
