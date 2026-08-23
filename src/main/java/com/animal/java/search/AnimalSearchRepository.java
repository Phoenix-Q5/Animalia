package com.animal.java.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface AnimalSearchRepository extends ElasticsearchRepository<AnimalDocument, String> {
}
