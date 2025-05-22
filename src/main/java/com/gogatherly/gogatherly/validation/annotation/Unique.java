package com.gogatherly.gogatherly.validation.annotation;

import com.gogatherly.gogatherly.validation.constraint.UniqueConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = UniqueConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    String message() default "{jakarta.validation.constraints.Email.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Object> entity() ;

    String column();
}
