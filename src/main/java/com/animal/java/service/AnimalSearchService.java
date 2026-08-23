package com.animal.java.service;

import com.animal.java.dto.AnimalFacets;
import com.animal.java.dto.AnimalSearchRequest;
import com.animal.java.dto.PageResponse;
import com.animal.java.model.animal.Animal;

public interface AnimalSearchService {

    PageResponse<Animal> search(AnimalSearchRequest request);

    AnimalFacets facets();
}
