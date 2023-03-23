package com.nagarro.recruitmenthelper.usermanagement.controller.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.RoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.ValidationResponse;
import com.nagarro.recruitmenthelper.usermanagement.service.UserService;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerImplTest {

    @InjectMocks
    private UserControllerImpl userController;
    @Mock
    private UserService userService;

    @Test
    public void testAddUser_shouldReturnStatusCode201() {
        UserDTO userDTO = TestUtils.buildUserDTO();
        ResponseEntity<UserDTO> expectedResponseEntity = new ResponseEntity<>(
                userDTO,
                HttpStatus.CREATED
        );

        when(userService.addUser(userDTO)).thenReturn(userDTO);

        var actualResponseEntity = userController.addUser(userDTO);

        assertEquals(expectedResponseEntity.getStatusCode(), actualResponseEntity.getStatusCode());
        verify(userService).addUser(userDTO);
    }

    @Test
    public void testFindById_shouldReturnUserDTOAndStatusCode200() {
        UserDTO userDTO = new UserDTO(1L, "John Doe", "johndoe@yahoo.com", "password", new RoleDTO());

        ResponseEntity<UserDTO> expectedResponseEntity = new ResponseEntity<>(userDTO, HttpStatus.OK);

        when(userService.findById(1L)).thenReturn(userDTO);

        ResponseEntity<UserDTO> actualResponseEntity = userController.findById(1L);

        assertEquals(expectedResponseEntity, actualResponseEntity);
    }

    @Test
    public void testFindAll_shouldReturnUserDTOListAndStatusCode200() {
        UserDTO firstUserDTO = new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "password", new RoleDTO());
        UserDTO secondUserDTO = new UserDTO(2L, "Jane Doe", "janedoe@yahoo.com", "password", new RoleDTO());

        ResponseEntity<List<UserDTO>> expectedResponseEntity =
                new ResponseEntity<>(List.of(firstUserDTO, secondUserDTO), HttpStatus.OK);

        when(userService.findAll(1, 2)).thenReturn(List.of(firstUserDTO, secondUserDTO));

        ResponseEntity<List<UserDTO>> actualResponseEntity = userController.findAll(1, 2);

        assertEquals(expectedResponseEntity.getStatusCode(), actualResponseEntity.getStatusCode());
    }

    @Test
    public void testFindAll_shouldReturnEmptyListAndStatusCode200() {
        ResponseEntity<List<UserDTO>> expectedResponseEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(userService.findAll(1, 2)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserDTO>> actualResponseEntity = userController.findAll(1, 2);

        assertEquals(expectedResponseEntity.getStatusCode(), actualResponseEntity.getStatusCode());
    }

    @Test
    public void testDeleteUser() {
        long userId = 1L;
        ResponseEntity<?> expectedResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        var actualResponse = userController.delete(userId);

        verify(userService).delete(userId);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    @Rollback
    public void testAreUsersValid_shouldRespondWithStatusCode200AndReturnTrue() {
        List<UserEmailRoleDTO> userEmailRoleDTOS = getListOfUserEmailRoleDTOS();

        ResponseEntity<ValidationResponse> expectedResponse =
                new ResponseEntity<>(new ValidationResponse(true), HttpStatus.OK);

        when(userService.areUsersValid(userEmailRoleDTOS)).thenReturn(true);

        var actualResponse = userController.areUsersValid(userEmailRoleDTOS);

        compareStatusCodeAndBody(expectedResponse, actualResponse);
    }

    @Test
    @Rollback
    public void testAreUsersValid_shouldRespondWithStatusCode200AndReturnFalse() {
        List<UserEmailRoleDTO> userEmailRoleDTOS = getListOfUserEmailRoleDTOS();

        ResponseEntity<ValidationResponse> expectedResponse =
                new ResponseEntity<>(new ValidationResponse(false), HttpStatus.OK);

        when(userService.areUsersValid(userEmailRoleDTOS)).thenReturn(false);

        var actualResponse = userController.areUsersValid(userEmailRoleDTOS);

        compareStatusCodeAndBody(expectedResponse, actualResponse);
    }

    private List<UserEmailRoleDTO> getListOfUserEmailRoleDTOS() {
        UserEmailRoleDTO userEmailRoleDTO = new UserEmailRoleDTO("valid@email.com", TestUtils.buildRole(1).getName());
        return List.of(userEmailRoleDTO);
    }

    private void compareStatusCodeAndBody(ResponseEntity<ValidationResponse> expectedResponse,
                                          ResponseEntity<ValidationResponse> actualResponse) {
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertNotNull(expectedResponse.getBody());
        assertEquals(expectedResponse.getBody().getValid(), expectedResponse.getBody().getValid());
    }

}