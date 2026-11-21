package com.animal.java.service.impl;

import com.animal.java.dto.BulkImportJob;
import com.animal.java.dto.BulkImportResult;
import com.animal.java.service.AnimalBulkImportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnimalBulkImportJobServiceTest {
    private final AnimalBulkImportService importer = mock(AnimalBulkImportService.class);
    private final AnimalBulkImportJobService jobs = new AnimalBulkImportJobService(importer);
    private final CountDownLatch release = new CountDownLatch(1);

    @AfterEach
    void tearDown() {
        release.countDown();
        jobs.shutdown();
    }

    @Test
    void submitsWithoutWaitingAndReturnsResult() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        BulkImportResult result = BulkImportResult.builder().totalRows(1001).imported(1001).rows(List.of()).build();
        when(importer.importAnimals(anyString(), any())).thenAnswer(invocation -> {
            started.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Timed out");
            return result;
        });

        BulkImportJob job = jobs.submit("animals.csv", new byte[]{1});
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(jobs.get(job.id()).status()).isEqualTo(BulkImportJob.Status.RUNNING);
        assertThat(jobs.get(UUID.randomUUID())).isNull();
        release.countDown();
        BulkImportJob completed = awaitTerminal(job.id());
        assertThat(completed.status()).isEqualTo(BulkImportJob.Status.COMPLETED);
        assertThat(completed.result().getImported()).isEqualTo(1001);
    }

    @Test
    void reportsUnexpectedFailures() throws Exception {
        when(importer.importAnimals(anyString(), any())).thenThrow(new IllegalStateException("failure"));
        BulkImportJob job = jobs.submit("animals.csv", new byte[]{1});
        BulkImportJob failed = awaitTerminal(job.id());
        assertThat(failed.status()).isEqualTo(BulkImportJob.Status.FAILED);
        assertThat(failed.error()).contains("Some animals may already have been imported");
    }

    @Test
    void rejectsJobsWhenQueueIsFull() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        when(importer.importAnimals(anyString(), any())).thenAnswer(invocation -> {
            started.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Timed out");
            return BulkImportResult.builder().rows(List.of()).build();
        });
        jobs.submit("animals.csv", new byte[]{1});
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        for (int i = 0; i < 10; i++) {
            assertThat(jobs.submit("animals.csv", new byte[]{1}).status()).isEqualTo(BulkImportJob.Status.QUEUED);
        }
        assertThatThrownBy(() -> jobs.submit("animals.csv", new byte[]{1}))
                .isInstanceOf(RejectedExecutionException.class);
    }

    private BulkImportJob awaitTerminal(UUID id) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        BulkImportJob job;
        do {
            job = jobs.get(id);
            if (job.status() == BulkImportJob.Status.COMPLETED || job.status() == BulkImportJob.Status.FAILED) {
                return job;
            }
            Thread.sleep(10);
        } while (System.nanoTime() < deadline);
        throw new AssertionError("Job did not finish");
    }
}
