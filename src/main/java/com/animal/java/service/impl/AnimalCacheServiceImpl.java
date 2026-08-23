package com.animal.java.service.impl;

import com.animal.java.config.SearchCacheConfig;
import com.animal.java.service.AnimalCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalCacheServiceImpl implements AnimalCacheService {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void evictAllListCaches() {
        clearCache(SearchCacheConfig.CACHE_ANIMAL_SEARCH);
        clearCache(SearchCacheConfig.CACHE_ANIMAL_FACETS);
        try {
            Set<String> keys = redisTemplate.keys("animalSearch::*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            keys = redisTemplate.keys("animalFacets::*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception ex) {
            log.warn("Redis list-cache eviction failed: {}", ex.getMessage());
        }
    }

    @Override
    public void evictAnimal(String animalId) {
        clearCache(SearchCacheConfig.CACHE_ANIMAL_DETAIL);
        try {
            redisTemplate.delete(SearchCacheConfig.CACHE_ANIMAL_DETAIL + "::" + animalId);
        } catch (Exception ex) {
            log.warn("Redis detail-cache eviction failed: {}", ex.getMessage());
        }
        evictAllListCaches();
    }

    private void clearCache(String name) {
        try {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        } catch (Exception ex) {
            log.warn("Cache clear failed for {}: {}", name, ex.getMessage());
        }
    }
}
