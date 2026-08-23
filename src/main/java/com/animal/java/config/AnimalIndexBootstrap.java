package com.animal.java.config;

import com.animal.java.service.AnimalIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Populates Elasticsearch on startup when the animals index is empty
 * (e.g. first boot after enabling search).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnimalIndexBootstrap implements ApplicationRunner {

    private final AnimalIndexService animalIndexService;
    private final co.elastic.clients.elasticsearch.ElasticsearchClient elasticsearchClient;
    private final String animalIndexName;

    @Override
    public void run(ApplicationArguments args) {
        try {
            long count = elasticsearchClient.count(c -> c.index(animalIndexName)).count();
            if (count > 0) {
                log.info("Elasticsearch index '{}' already has {} documents", animalIndexName, count);
                return;
            }
            log.info("Elasticsearch index '{}' is empty — starting reindex from Postgres", animalIndexName);
            long indexed = animalIndexService.reindexAll();
            log.info("Startup reindex complete: {} animals indexed", indexed);
        } catch (Exception ex) {
            log.warn("Startup Elasticsearch reindex skipped: {}", ex.getMessage());
        }
    }
}
