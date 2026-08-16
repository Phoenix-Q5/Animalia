package com.animal.java.validation.commands;

import com.animal.java.dao.AnimalRepository;
import com.animal.java.validation.AnimaliaValidation;
import com.animal.java.validation.commands.utils.ValidationMessage;
import com.animal.java.validation.commands.utils.ValidationTag;
import com.animal.java.validation.extension.AnimalValidationCommand;
import com.animal.java.validation.extension.AnimalValidationObject;
import com.animal.java.validation.pipeline.ValidationCategory;
import com.animal.java.validation.pipeline.ValidationResult;
import com.animal.java.validation.pipeline.context.AnimalValidationObjectContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AnimaliaValidation(
        validation = "animal.validation.duplicate",
        conditions = {
                @AnimaliaValidation.Condition(category = ValidationCategory.TARGET, values = ValidationTag.ANIMAL_OBJECT)
        }
)
@RequiredArgsConstructor
public class DuplicateAnimalCommand extends AnimalValidationCommand {

    private final AnimalRepository animalRepository;

    @Override
    protected void validate(AnimalValidationObject object, AnimalValidationObjectContext ctx, ValidationResult result) {
        if (StringUtils.isNotBlank(object.getName()) && animalRepository.existsByName(object.getName())) {
            result.getNotifications().add(buildNotification(object.getObjectId(), "name", ValidationMessage.NAME_ALREADY_EXISTS));
        }
        if (StringUtils.isNotBlank(object.getScientificName())
                && animalRepository.existsByScientificName(object.getScientificName())) {
            result.getNotifications().add(buildNotification(object.getObjectId(), "scientificName", ValidationMessage.SCIENTIFIC_NAME_ALREADY_EXISTS));
        }
    }
}
