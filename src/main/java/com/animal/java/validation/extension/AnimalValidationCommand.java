package com.animal.java.validation.extension;

import com.animal.java.validation.pipeline.ValidationCommand;
import com.animal.java.validation.pipeline.ValidationNotificationType;
import com.animal.java.validation.pipeline.ValidationObject;
import com.animal.java.validation.pipeline.ValidationResult;
import com.animal.java.validation.pipeline.context.AnimalValidationObjectContext;
import com.animal.java.validation.pipeline.context.ValidationObjectContext;

public abstract class AnimalValidationCommand extends AbstractValidationCommand<AnimalValidationObject> {

    protected AnimalValidationCommand() {
        super(AnimalValidationObject.class, ValidationNotificationType.fromGroup(
                ValidationNotificationType.Group.ANIMAL
        ));
    }

    @Override
    protected void validate(AnimalValidationObject object, ValidationObjectContext ctx, ValidationResult result) {
        validate(object, AnimalValidationObjectContext.of(ctx), result);
    }

    protected abstract void validate(AnimalValidationObject object, AnimalValidationObjectContext ctx, ValidationResult result);
}
