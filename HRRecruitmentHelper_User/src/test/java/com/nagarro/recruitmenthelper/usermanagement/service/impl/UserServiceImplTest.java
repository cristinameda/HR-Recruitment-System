package com.nagarro.recruitmenthelper.usermanagement.service.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.RoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.Role;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserNotFoundException;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private PasswordHashGenerator passwordHashGenerator;

    @Test
    void testAddUser() {
        Role role = new Role(1L, "Role");
        RoleDTO roleDTO = new RoleDTO(1L, "Role");
        User user = new User("Mary Doe", "marydoe@yahoo.com", "password", role);
        user.setId(1L);
        UserDTO userDTO = new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "d3e3224a59d69e9a000f1ce6782cb6a8be1eb3155610ff41bffbcbc95adc5d7", roleDTO);

        when(passwordHashGenerator.hashPassword(user.getPassword())).thenReturn("d3e3224a59d69e9a000f1ce6782cb6a8be1eb3155610ff41bffbcbc95adc5d7");
        when(userRepository.addUser(user)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);

        var storedUser = userService.addUser(userDTO);

        verify(passwordHashGenerator).hashPassword("password");
        verify(userRepository).addUser(user);
        TestUtils.equals(userDTO, storedUser);
    }

    @Test
    void testFindById_shouldReturnUser() {
        Role role = new Role(1L, "Role");
        RoleDTO roleDTO = new RoleDTO(1L, "Role");
        User user = new User("Mary Doe", "marydoe@yahoo.com", "password", role);
        user.setId(1L);
        UserDTO userDTO = new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "password", roleDTO);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO foundUser = userService.findById(1L);

        TestUtils.equals(userDTO, foundUser);
    }

    @Test
    void testFindById_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindAll_shouldReturnUsersList() {
        Role firstRole = new Role(1L, "Role1");
        RoleDTO firstRoleDTO = new RoleDTO(1L, "Role1");
        Role secondRole = new Role(2L, "Role2");
        RoleDTO secondRoleDTO = new RoleDTO(2L, "Role2");
        User firstUser = new User("Mary Doe", "marydoe@yahoo.com", "password", firstRole);
        firstUser.setId(1L);
        User secondUser = new User("Jane Doe", "janedoe@yahoo.com", "password", secondRole);
        secondUser.setId(2L);
        UserDTO firstUserDTO = new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "password", firstRoleDTO);
        UserDTO secondUserDTO = new UserDTO(2L, "Jane Doe", "janedoe@yahoo.com", "password", secondRoleDTO);
        when(userRepository.findAll(1, 2)).thenReturn(List.of(firstUser, secondUser));
        when(modelMapper.map(firstUser, UserDTO.class)).thenReturn(firstUserDTO);
        when(modelMapper.map(secondUser, UserDTO.class)).thenReturn(secondUserDTO);

        List<UserDTO> userDTOs = userService.findAll(1, 2);

        assertEquals(2, userDTOs.size());
    }

    @Test
    void testFindAll_shouldReturnEmptyList() {
        when(userRepository.findAll(1, 2)).thenReturn(Collections.emptyList());

        List<UserDTO> userDTOs = userService.findAll(1, 2);

        assertTrue(userDTOs.isEmpty());
    }

    @Test
    void testFindAllInvalidPage_shouldThrowIllegalArgumentsException() {
        int page = 0;
        int pageSize = 2;

        assertThrows(IllegalArgumentException.class, () -> userService.findAll(page, pageSize));
    }
    @Test
    void testFindAllInvalidPageSize_shouldThrowIllegalArgumentsException() {
        int page = 1;
        int pageSize = -3;

        assertThrows(IllegalArgumentException.class, () -> userService.findAll(page, pageSize));
    }

    @Test
    void testDeleteUser() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        userService.delete(userId);

        verify(userRepository).delete(userId);
    }

    @Test
    void testAreUsersValid_shouldReturnTrue() {
        User user1 = TestUtils.buildUser(1);
        User user2 = TestUtils.buildUser(2);
        UserEmailRoleDTO userEmailRoleDTO1 = new UserEmailRoleDTO(user1.getEmail(), user1.getRole().getName());
        UserEmailRoleDTO userEmailRoleDTO2 = new UserEmailRoleDTO(user2.getEmail(), user2.getRole().getName());
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(userRepository.findUserByEmail(user2.getEmail())).thenReturn(Optional.of(user2));

        assertTrue(userService.areUsersValid(List.of(userEmailRoleDTO1, userEmailRoleDTO2)));
    }

    @Test
    void testAreUsersValid_whenUserDoesntExist_shouldReturnFalse() {
        UserEmailRoleDTO userEmailRoleDTO = new UserEmailRoleDTO("some@email.com", TestUtils.buildRole(1).getName());
        when(userRepository.findUserByEmail("some@email.com")).thenReturn(Optional.empty());

        assertFalse(userService.areUsersValid(List.of(userEmailRoleDTO)));
    }

    @Test
    void testAreUsersValid_whenUserDoesntHaveGivenRole_shouldReturnFalse() {
        User user = TestUtils.buildUser(1);
        UserEmailRoleDTO userEmailRoleDTO = new UserEmailRoleDTO(user.getEmail(), user.getRole().getName());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(TestUtils.buildUser(2)));

        assertFalse(userService.areUsersValid(List.of(userEmailRoleDTO)));
    }

    @Test
    void testDeleteUser_shouldThrowUserNotFoundException() {
        long userId = 100L;

        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void findUserByEmail() {
        String email = "email";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User()));

        userService.findUserByEmail(email);

        verify(userRepository).findUserByEmail(email);
    }

    @Test
    void findUserByEmail_shouldThrowException() {
        String email = "email";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail(email));

        verify(userRepository).findUserByEmail(email);
    }
}