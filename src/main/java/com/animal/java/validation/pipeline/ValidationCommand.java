package com.animal.java.validation.pipeline;

public interface ValidationCommand {

    void execute(ValidationRequest request, ValidationResult result);
}
