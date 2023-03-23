package com.nagarro.recruitmenthelper.usermanagement.controller.dto;

import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserDtoValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;
    @MockBean
    private UserRepository userRepository;

    private static Stream<Arguments> provideFieldAndInValidValue() {
        return Stream.of(
                Arguments.of("name", null),
                Arguments.of("email", null),
                Arguments.of("password", null),
                Arguments.of("name", ""),
                Arguments.of("email", ""),
                Arguments.of("password", ""),
                Arguments.of("name", " "),
                Arguments.of("email", " "),
                Arguments.of("password", " "),
                Arguments.of("password", "aa"),
                Arguments.of("password", "abcdefsgagussa"),
                Arguments.of("password", "Abcdefsgagussa"),
                Arguments.of("password", "aA!"),
                Arguments.of("password", "!bcdefsgagussa"),
                Arguments.of("password", "!ABVCSDGSEED"),
                Arguments.of("email", "aa"),
                Arguments.of("email", "@yahoo.com"),
                Arguments.of("email", "test@yahoo..com"),
                Arguments.of("email", "test@")
        );
    }

    @BeforeEach
    public void setUp() {
        String email = "marydoe@yahoo.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
    }

    @Test
    public void testValidation_success() {
        UserDTO userDTO = TestUtils.buildUserDTO();

        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(userDTO);

        assertThat(constraintViolations.size()).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideFieldAndInValidValue")
    public void testValidation_inValidEmailFields(String fieldName, Object invalidValue) throws NoSuchFieldException, IllegalAccessException {
        UserDTO userDTO = TestUtils.buildUserDTO();

        Field field = UserDTO.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(userDTO, invalidValue);

        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(userDTO);

        assertThat(constraintViolations.size()).isOne();
    }
}
