package com.animal.java.validation.commands;

import com.animal.java.validation.AnimaliaValidation;
import com.animal.java.validation.commands.utils.ValidationMessage;
import com.animal.java.validation.commands.utils.ValidationTag;
import com.animal.java.validation.extension.AnimalValidationCommand;
import com.animal.java.validation.extension.AnimalValidationObject;
import com.animal.java.validation.pipeline.ValidationCategory;
import com.animal.java.validation.pipeline.ValidationResult;
import com.animal.java.validation.pipeline.context.AnimalValidationObjectContext;

@AnimaliaValidation(
        validation = "animal.validation.species.exist",
        conditions = {
                @AnimaliaValidation.Condition(category = ValidationCategory.TARGET, values = ValidationTag.ANIMAL_OBJECT)
        }
)
public class SpeciesExistCommand extends AnimalValidationCommand {
        @Override
        protected void validate(AnimalValidationObject object, AnimalValidationObjectContext ctx, ValidationResult result) {
                if(ctx.getSpecies() == null) {
                        result.getNotifications().add(buildNotification(object.getObjectId(), null, ValidationMessage.SPECIES_NOT_EMPTY));
                }
        }
}
