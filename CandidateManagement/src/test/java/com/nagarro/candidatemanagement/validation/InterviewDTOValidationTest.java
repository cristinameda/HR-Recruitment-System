package com.nagarro.candidatemanagement.validation;

import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import com.nagarro.candidatemanagement.service.InterviewService;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class InterviewDTOValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;
    @MockBean
    private InterviewService interviewService;

    private static Stream<Arguments> interviewDTOFieldsProvider() {
        return Stream.of(
                Arguments.of("candidateId", -1),
                Arguments.of("dateTime", null),
                Arguments.of("dateTime", LocalDateTime.now().minus(10, ChronoUnit.DAYS)),
                Arguments.of("location", null),
                Arguments.of("location", " "));
    }

    @Test
    public void testValidInterviewDTO() {
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(1L);

        when(interviewService.scheduleInterview(interviewDTO)).thenReturn(interviewDTO);

        Set<ConstraintViolation<InterviewDTO>> constraintViolations =
                validator.validate(interviewDTO);

        assertThat(constraintViolations.size()).isZero();
    }

    @ParameterizedTest
    @MethodSource("interviewDTOFieldsProvider")
    void testInvalidCandidateDTO(String fieldName, Object invalidValue) {
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(1L);

        try {
            Field field = interviewDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(interviewDTO, invalidValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        when(interviewService.scheduleInterview(interviewDTO)).thenReturn(interviewDTO);

        Set<ConstraintViolation<InterviewDTO>> constraintViolations =
                validator.validate(interviewDTO);

        constraintViolations.forEach(System.out::println);

        assertThat(constraintViolations.size()).isGreaterThanOrEqualTo(1);
    }
}
