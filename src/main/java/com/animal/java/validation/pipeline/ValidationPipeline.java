package com.animal.java.validation.pipeline;

import com.animal.java.validation.AnimaliaValidation;
import com.animal.java.validation.extension.AnimalValidationObject;
import com.animal.java.validation.pipeline.context.AnimalValidationObjectContext;
import com.animal.java.validation.pipeline.context.ValidationObjectContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationPipeline {

    private final List<ValidationCommand> commands;

    public ValidationResult validate(AnimalValidationObject object) {
        ValidationObjectContext context = new ValidationObjectContext();
        context.getScope().withTag(ValidationCategory.TARGET, "animal");
        AnimalValidationObjectContext.of(context).setSpecies(StringUtils.trimToNull(object.getSpecies()));

        ValidationRequest request = ValidationRequest.builder()
                .object(object)
                .context(context)
                .scope(context.getScope())
                .build();

        ValidationResult result = new ValidationResult();
        for (ValidationCommand command : commands) {
            if (matches(command, request.getScope())) {
                command.execute(request, result);
            }
        }
        return result;
    }

    private boolean matches(ValidationCommand command, ValidationScope scope) {
        AnimaliaValidation annotation = AopUtils.getTargetClass(command).getAnnotation(AnimaliaValidation.class);
        if (annotation == null || annotation.conditions().length == 0) {
            return true;
        }
        for (AnimaliaValidation.Condition condition : annotation.conditions()) {
            Set<String> tags = scope.getTags().getOrDefault(condition.category(), Set.of());
            boolean matched = Arrays.stream(condition.values())
                    .map(StringUtils::lowerCase)
                    .anyMatch(tags::contains);
            if (!matched) {
                return false;
            }
        }
        return true;
    }
}
