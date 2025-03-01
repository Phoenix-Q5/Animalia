package com.animal.java.validation.pipeline.context;

import com.animal.java.validation.pipeline.ValidationScope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ValidationObjectContext {

    private final ValidationScope scope = new ValidationScope();

    private final Map<String, Object> cache = new HashMap<>();
}
