package com.animal.java.dto;

import java.util.UUID;

public record BulkImportJob(UUID id, Status status, BulkImportResult result, String error) {
    public enum Status {
        QUEUED, RUNNING, COMPLETED, FAILED
    }
}
