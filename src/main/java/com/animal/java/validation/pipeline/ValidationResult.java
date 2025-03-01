package com.animal.java.validation.pipeline;

import com.animal.java.validation.ValidationNotification;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public class ValidationResult {

    private final Collection<ValidationNotification> notifications = new ConcurrentLinkedDeque<>();

    public boolean isSucceeded() {
        return CollectionUtils.isEmpty(notifications);
    }
}
