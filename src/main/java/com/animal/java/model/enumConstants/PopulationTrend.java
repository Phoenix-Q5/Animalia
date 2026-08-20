package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import lombok.Getter;

@Getter
public enum PopulationTrend implements EnumId {
    INCREASING("Increasing"),
    DECREASING("Decreasing"),
    STABLE("Stable"),
    UNKNOWN("Unknown");

    private final String description;

    PopulationTrend(String description) {
        this.description = description;
    }
}
