package com.nagarro.candidatemanagement.gateway;

import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import java.util.List;

public interface UsersValidator {
    boolean areUsersValid(List<UserEmailRoleDTO> users);

    boolean areRequestedRolesValid(List<UserEmailRoleDTO> users);
}
