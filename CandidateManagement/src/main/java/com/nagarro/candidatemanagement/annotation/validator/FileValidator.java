package com.nagarro.candidatemanagement.annotation.validator;

import com.nagarro.candidatemanagement.annotation.NotEmptyFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator implements ConstraintValidator<NotEmptyFile, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file != null && !file.isEmpty();
    }
}
