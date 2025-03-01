package com.animal.java.validation.extension;

import com.animal.java.validation.ValidationNotification;
import com.animal.java.validation.pipeline.*;
import com.animal.java.validation.pipeline.context.ValidationObjectContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractValidationCommand<V extends ValidationObject> implements ValidationCommand {

    private final Class<V> objectType;
    private final ValidationNotificationType notificationType;

    @Override
    public void execute(ValidationRequest request, ValidationResult result) {
        if (objectType.isAssignableFrom(request.getObject().getClass())) {
            validate((V)request.getObject(), request.getContext(), result);
        }
    }

    protected abstract void validate(V object, ValidationObjectContext request, ValidationResult result);

    protected ValidationNotification buildNotification(String objectId, String attribute, String message) {
        return ValidationNotification.builder()
                .attribute(attribute)
                .message(message)
                .type(notificationType)
                .build().withEntityId(ValidationObject.class, objectId);
    }

    protected ValidationNotification buildNotification(String objectId, String attribute, String message, ValidationNotification.Level level) {
        return ValidationNotification.builder()
                .message(message)
                .attribute(attribute)
                .type(notificationType)
                .level(level)
                .build().withEntityId(ValidationObject.class, objectId);
    }
}
