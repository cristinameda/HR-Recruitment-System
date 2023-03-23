package com.nagarro.candidatemanagement.annotation;

import com.nagarro.candidatemanagement.annotation.validator.UniqueEmailValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "email must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
