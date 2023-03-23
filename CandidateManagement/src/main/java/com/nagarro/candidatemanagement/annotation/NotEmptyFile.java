package com.nagarro.candidatemanagement.annotation;

import com.nagarro.candidatemanagement.annotation.validator.FileValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface NotEmptyFile {
    String message() default "File is mandatory and it must have content!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
