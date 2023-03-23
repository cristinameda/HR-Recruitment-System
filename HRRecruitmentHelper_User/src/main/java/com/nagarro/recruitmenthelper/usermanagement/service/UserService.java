package com.nagarro.recruitmenthelper.usermanagement.service;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    /**
     * Adds a user
     * Maps the userDTO into a User for Repository
     *
     * @param userDTO the received user that will be inserted
     * @return UserDTO with the id set
     */
    UserDTO addUser(UserDTO userDTO);

    /**
     * Returns the user with the given id.
     *
     * @param id the id of the user
     * @return UserDTO the user information
     * @throws UserNotFoundException if no user with that id is found
     */
    UserDTO findById(Long id);

    /**
     * Returns users using pagination.
     *
     * @param page     the number of the page (according to the pageSize)
     * @param pageSize the number of users included on a page
     * @return List<UserDTO> the list of users
     */
    List<UserDTO> findAll(int page, int pageSize);

    /**
     * Deletes a user
     *
     * @param userId the identifier of the user
     * @throws UserNotFoundException if no user with that id is found
     */
    void delete(long userId);

    /**
     * Returns the user with the given email.
     *
     * @param email the email of the user
     * @return User the user information
     * @throws UserNotFoundException if no user with that email is found
     */
    User findUserByEmail(String email);

    /**
     * Checks if the given users exist and have the given role.
     *
     * @param userEmailRoleDTOS the emails which will be checked
     * @return true if they exist and have the given role, false if not
     */
    boolean areUsersValid(List<UserEmailRoleDTO> userEmailRoleDTOS);
}
