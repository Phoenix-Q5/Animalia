package com.animal.java.service;

import com.animal.java.dto.BulkImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface AnimalBulkImportService {

    BulkImportResult importAnimals(MultipartFile file);

    byte[] canonicalTemplate();
}
