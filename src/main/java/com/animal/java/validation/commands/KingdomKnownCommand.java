package com.animal.java.validation.commands;

import com.animal.java.model.enumConstants.Kingdom;
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
        validation = "animal.validation.kingdom.known",
        conditions = {
                @AnimaliaValidation.Condition(category = ValidationCategory.TARGET, values = ValidationTag.ANIMAL_OBJECT)
        }
)
public class KingdomKnownCommand extends AnimalValidationCommand {
    @Override
    protected void validate(AnimalValidationObject object, AnimalValidationObjectContext ctx, ValidationResult result) {
        if (StringUtils.isBlank(object.getKingdom())) {
            return;
        }
        for (Kingdom kingdom : Kingdom.values()) {
            if (kingdom.name().equalsIgnoreCase(object.getKingdom())
                    || kingdom.getId().equalsIgnoreCase(object.getKingdom())) {
                return;
            }
        }
        result.getNotifications().add(buildNotification(object.getObjectId(), "kingdom", ValidationMessage.KINGDOM_UNKNOWN));
    }
}
