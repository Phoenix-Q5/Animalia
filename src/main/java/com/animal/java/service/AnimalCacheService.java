package com.animal.java.service;

public interface AnimalCacheService {

    void evictAllListCaches();

    void evictAnimal(String animalId);
}
