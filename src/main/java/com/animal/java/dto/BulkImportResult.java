package com.animal.java.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BulkImportResult {

    private final int totalRows;
    private final int imported;
    private final int failed;
    private final String fileError;
    private final List<BulkImportRowResult> rows;
}
