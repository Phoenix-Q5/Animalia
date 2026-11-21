package com.animal.java.controller;

import com.animal.java.dto.BulkImportJob;
import com.animal.java.service.impl.AnimalBulkImportJobService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnimalBulkImportControllerTest {
    private final AnimalBulkImportJobService jobs = mock(AnimalBulkImportJobService.class);
    private final AnimalController controller = new AnimalController(null, null, null, jobs, null, null);

    @Test
    void acceptsAndRetrievesJobs() throws Exception {
        UUID id = UUID.randomUUID();
        BulkImportJob job = new BulkImportJob(id, BulkImportJob.Status.QUEUED, null, null);
        when(jobs.submit(anyString(), any())).thenReturn(job);
        when(jobs.get(id)).thenReturn(job);
        MockMultipartFile file = new MockMultipartFile("file", "animals.csv", "text/csv", new byte[]{1});

        assertThat(controller.bulkImport(file).getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(controller.bulkImportStatus(id).getBody()).isEqualTo(job);
        assertThat(controller.bulkImportStatus(UUID.randomUUID()).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void rejectsEmptyFilesAndFullQueues() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("file", new byte[0]);
        assertThat(controller.bulkImport(empty).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        when(jobs.submit(anyString(), any())).thenThrow(new RejectedExecutionException());
        MockMultipartFile file = new MockMultipartFile("file", "animals.csv", "text/csv", new byte[]{1});
        assertThat(controller.bulkImport(file).getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
