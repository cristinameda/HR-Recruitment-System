package com.nagarro.candidatemanagement.validation;

import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.service.CandidateService;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CandidateDTOValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;

    @MockBean
    private CandidateService candidateService;

    private static Stream<Arguments> candidateDTOFieldsProvider() {
        return Stream.of(
                Arguments.of("name", null),
                Arguments.of("phoneNumber", null),
                Arguments.of("phoneNumber", "12345678"),
                Arguments.of("email", null),
                Arguments.of("email", "a-b*c@abc.com"),
                Arguments.of("email", "candidate7@yahoo.com"),
                Arguments.of("city", null),
                Arguments.of("experienceYears", -1),
                Arguments.of("faculty", null),
                Arguments.of("recruitmentChannel", null),
                Arguments.of("birthDate", null),
                Arguments.of("birthDate", LocalDate.of(2010, 2, 1)),
                Arguments.of("interestedPositionsDTO", null)
        );
    }


    @Test
    public void testValidCandidateDTO() {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate7", "candidate7@yahoo.com");

        when(candidateService.findByEmail("candidate7@yahoo.com")).thenThrow(CandidateNotFoundException.class);

        Set<ConstraintViolation<CandidateDTO>> constraintViolations =
                validator.validate(candidateDTO);

        assertThat(constraintViolations.size()).isZero();
    }

    @ParameterizedTest
    @MethodSource("candidateDTOFieldsProvider")
    void testInvalidCandidateDTO(String fieldName, Object invalidValue) {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate7", "candidate7@yahoo.com");

        try {
            Field field = candidateDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(candidateDTO, invalidValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        when(candidateService.findByEmail("candidate7@yahoo.com")).thenReturn(candidateDTO);

        Set<ConstraintViolation<CandidateDTO>> constraintViolations =
                validator.validate(candidateDTO);

        constraintViolations.forEach(System.out::println);

        assertThat(constraintViolations.size()).isGreaterThanOrEqualTo(1);
    }
}
