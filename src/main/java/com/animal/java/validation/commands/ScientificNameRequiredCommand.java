package com.animal.java.validation.commands;

import com.animal.java.validation.AnimaliaValidation;
import com.animal.java.validation.commands.utils.ValidationMessage;
import com.animal.java.validation.commands.utils.ValidationTag;
import com.animal.java.validation.extension.AnimalValidationCommand;
import com.animal.java.validation.extension.AnimalValidationObject;
import com.animal.java.validation.pipeline.ValidationCategory;
import com.animal.java.validation.pipeline.ValidationResult;
import com.animal.java.validation.pipeline.context.AnimalValidationObjectContext;
import org.apache.commons.lang3.StringUtils;

@AnimaliaValidation(
        validation = "animal.validation.scientificName.exist",
        conditions = {
                @AnimaliaValidation.Condition(category = ValidationCategory.TARGET, values = ValidationTag.ANIMAL_OBJECT)
        }
)
public class ScientificNameRequiredCommand extends AnimalValidationCommand {
    @Override
    protected void validate(AnimalValidationObject object, AnimalValidationObjectContext ctx, ValidationResult result) {
        if (StringUtils.isBlank(object.getScientificName())) {
            result.getNotifications().add(buildNotification(object.getObjectId(), "scientificName", ValidationMessage.SCIENTIFIC_NAME_NOT_EMPTY));
        }
    }
}
