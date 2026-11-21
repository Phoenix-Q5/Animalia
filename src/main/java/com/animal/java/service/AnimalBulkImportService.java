package com.animal.java.service;

import com.animal.java.dto.BulkImportResult;

public interface AnimalBulkImportService {

    BulkImportResult importAnimals(String filename, byte[] contents);

    byte[] canonicalTemplate();
}
