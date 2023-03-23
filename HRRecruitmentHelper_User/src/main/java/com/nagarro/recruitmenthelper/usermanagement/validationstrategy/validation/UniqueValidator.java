package com.nagarro.recruitmenthelper.usermanagement.validationstrategy.validation;

import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.FieldValidationStrategy;
import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.Unique;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    private final ApplicationContext applicationContext;
    private FieldValidationStrategy strategy;
    private String fieldName;

    public UniqueValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(Unique unique) {
        Class<? extends FieldValidationStrategy> clazz = unique.strategy();
        this.fieldName = unique.fieldName();
        String serviceQualifier = unique.serviceQualifier();
        if (!serviceQualifier.equals("")) {
            this.strategy = this.applicationContext.getBean(serviceQualifier, clazz);
        } else {
            this.strategy = this.applicationContext.getBean(clazz);
        }
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return !getFieldValidationStrategy().checkIfValueExists(o, getFieldName());
    }

    public FieldValidationStrategy getFieldValidationStrategy() {
        return this.strategy;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}
