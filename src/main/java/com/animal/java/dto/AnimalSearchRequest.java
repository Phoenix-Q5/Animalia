package com.animal.java.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalSearchRequest {

    private String q;
    private String letter;
    private String species;
    private String habitat;
    private String biome;
    private String climate;
    private String status;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 24;

    @Builder.Default
    private String sort = "name,asc";

    public String cacheKey() {
        return String.join("|",
                nullToEmpty(q),
                nullToEmpty(letter),
                nullToEmpty(species),
                nullToEmpty(habitat),
                nullToEmpty(biome),
                nullToEmpty(climate),
                nullToEmpty(status),
                String.valueOf(page),
                String.valueOf(size),
                nullToEmpty(sort)
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
