package com.animal.java.validation.pipeline;

import com.animal.java.validation.pipeline.context.ValidationObjectContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ValidationRequest {

    private final ValidationScope scope;

    private final ValidationObject object;

    private final ValidationObjectContext context;

    @Override
    public String toString() {
        return object.getClass().getSimpleName() + " {" + object.getObjectId() + '}';
    }
}
