package com.animal.java.service.impl;

import com.animal.java.dto.BulkImportJob;
import com.animal.java.dto.BulkImportResult;
import com.animal.java.service.AnimalBulkImportService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.animal.java.utils.UUIDUtils.newID;

@Service
@Slf4j
public class AnimalBulkImportJobService {
    private static final Duration RETENTION = Duration.ofHours(24);
    private static final int MAX_FINISHED_JOBS = 100;

    private final AnimalBulkImportService importService;
    private final Map<UUID, JobState> jobs = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    public AnimalBulkImportJobService(AnimalBulkImportService importService) {
        this.importService = importService;
    }

    public synchronized BulkImportJob submit(String filename, byte[] contents) {
        log.info("Submitting bulk import job for file: {}", filename);
        cleanUp();
        UUID id = newID();
        JobState state = new JobState(new BulkImportJob(id, BulkImportJob.Status.QUEUED, null, null));
        jobs.put(id, state);
        try {
            executor.execute(() -> run(state, filename, contents));
        } catch (RejectedExecutionException ex) {
            jobs.remove(id);
            throw ex;
        }
        return state.snapshot;
    }

    public BulkImportJob get(UUID id) {
        JobState state = jobs.get(id);
        if (state == null) {
            return null;
        }
        if (state.finishedAt != null && state.finishedAt.plus(RETENTION).isBefore(Instant.now())) {
            jobs.remove(id, state);
            return null;
        }
        return state.snapshot;
    }

    private void run(JobState state, String filename, byte[] contents) {
        UUID id = state.snapshot.id();
        state.snapshot = new BulkImportJob(id, BulkImportJob.Status.RUNNING, null, null);
        try {
            BulkImportResult result = importService.importAnimals(filename, contents);
            state.snapshot = new BulkImportJob(id, BulkImportJob.Status.COMPLETED, result, null);
        } catch (Exception ex) {
            log.error("Bulk import job {} failed", id, ex);
            state.snapshot = new BulkImportJob(id, BulkImportJob.Status.FAILED, null,
                    "The import stopped unexpectedly. Some animals may already have been imported; check the catalog before retrying.");
        } finally {
            state.finishedAt = Instant.now();
        }
    }

    private void cleanUp() {
        Instant cutoff = Instant.now().minus(RETENTION);
        jobs.entrySet().removeIf(entry -> entry.getValue().finishedAt != null
                && entry.getValue().finishedAt.isBefore(cutoff));
        var finished = jobs.entrySet().stream()
                .filter(entry -> entry.getValue().finishedAt != null)
                .sorted(Comparator.comparing(entry -> entry.getValue().finishedAt))
                .toList();
        for (int i = 0; i < finished.size() - MAX_FINISHED_JOBS + 1; i++) {
            jobs.remove(finished.get(i).getKey(), finished.get(i).getValue());
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    private static class JobState {
        private volatile BulkImportJob snapshot;
        private volatile Instant finishedAt;

        private JobState(BulkImportJob snapshot) {
            this.snapshot = snapshot;
        }
    }
}
