package com.animal.java.validation;

import com.animal.java.validation.pipeline.ValidationCategory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnimaliaValidation {

    String validation();

    Condition[] conditions() default {};

    Arguments[] arguments() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface Condition {
        ValidationCategory category();
        String[] values() default {}; // Values of category
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface Arguments {
        Condition[] conditions() default {};
        String[] values() default {};

    }

}
