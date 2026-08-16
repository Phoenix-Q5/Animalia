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
        validation = "animal.validation.field.length",
        conditions = {
                @AnimaliaValidation.Condition(category = ValidationCategory.TARGET, values = ValidationTag.ANIMAL_OBJECT)
        }
)
public class FieldLengthCommand extends AnimalValidationCommand {

    private static final int MAX_LENGTH = 255;

    @Override
    protected void validate(AnimalValidationObject object, AnimalValidationObjectContext ctx, ValidationResult result) {
        checkLength(object, result, "name", object.getName());
        checkLength(object, result, "scientificName", object.getScientificName());
        checkLength(object, result, "phylum", object.getPhylum());
        checkLength(object, result, "family", object.getFamily());
        checkLength(object, result, "kingdom", object.getKingdom());
        checkLength(object, result, "species", object.getSpecies());
    }

    private void checkLength(AnimalValidationObject object, ValidationResult result, String attribute, String value) {
        if (StringUtils.isNotBlank(value) && value.length() > MAX_LENGTH) {
            result.getNotifications().add(buildNotification(
                    object.getObjectId(),
                    attribute,
                    attribute + " " + ValidationMessage.FIELD_TOO_LONG
            ));
        }
    }
}
