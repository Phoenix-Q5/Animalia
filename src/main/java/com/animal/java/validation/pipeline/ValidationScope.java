package com.animal.java.validation.pipeline;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ValidationScope {

    @Getter
    private final Map<ValidationCategory, Set<String>> tags = new EnumMap<>(ValidationCategory.class);

    public ValidationScope withTag(ValidationCategory category, String tag) {
        if (Objects.nonNull(tag)) {
            tags.computeIfAbsent(category, k -> new HashSet<>()).add(StringUtils.lowerCase(tag));
        }
        return this;
    }

    public ValidationScope withTag(ValidationCategory category, Set<String> tag) {
        if (CollectionUtils.isNotEmpty(tag)) {
            tags.computeIfAbsent(category, k -> new HashSet<>())
                    .addAll(tag.stream().map(StringUtils::lowerCase).collect(Collectors.toSet()));
        }
        return this;
    }

    public void copyTags(ValidationScope scope) {
        if (Objects.nonNull(scope)) {
            scope.getTags().forEach(this::withTag);
        }
    }
}