package com.nagarro.recruitmenthelper.usermanagement.utils;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.RoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.Role;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static User buildUser(int count) {
        return new User("User" + count, "user" + count + "@email.com", "b982b70ab5106c703497b73af8b723e7cfbf00b87b9955ed05fb81a34f97df77", buildRole(count));
    }

    public static Role buildRole(int count) {
        return new Role((long) count, "Role" + count);
    }

    public static Role buildAdminRole() {
        return new Role(1L, "Admin");
    }

    public static Role buildHrRepresentativeRole() {
        return new Role(2L, "HrRepresentative");
    }

    public static UserDTO buildUserDTO(int count) {
        return new UserDTO((long) count, "User" + count, "user" + count + "@email.com", "paSsword123!" + count, buildRoleDTO(count));
    }

    public static RoleDTO buildRoleDTO(int count) {
        return new RoleDTO((long) count, "Role" + count);
    }

    public static UserDTO buildUserDTO() {
        return new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "paSsword123!", new RoleDTO(1L, "Admin"));
    }

    public static UserDTO buildUserDTOInvalidPassword() {
        return new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "123", new RoleDTO());
    }

    public static UserDTO buildPTEUserDTO() {
        return new UserDTO(1L, "Mary Doe", "marydoe@yahoo.com", "123", new RoleDTO(4L, "PTE"));
    }

    public static User buildPTEUser() {
        return new User("Mary Doe", "marydoe@yahoo.com", "123", new Role(4L, "PTE"));
    }

    public static User buildUser() {
        return new User("Mary Doe", "marydoe@yahoo.com", "paSsword123!", new Role(1L, "Admin"));
    }

    public static String generateToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDTO.getRoleDTO().getName());
        return Jwts.builder().setClaims(claims)
                .setSubject(userDTO.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1))
                .signWith(SignatureAlgorithm.HS512, "mock").compact();
    }

    public static void equals(User firstUser, User secondUser) {
        assertEquals(firstUser.getId(), secondUser.getId());
        assertEquals(firstUser.getName(), secondUser.getName());
        assertEquals(firstUser.getEmail(), secondUser.getEmail());
        assertEquals(firstUser.getPassword(), secondUser.getPassword());
        assertEquals(firstUser.getRole().getId(), secondUser.getRole().getId());
        assertEquals(firstUser.getRole().getName(), secondUser.getRole().getName());
    }

    public static void equals(UserDTO firstUser, UserDTO secondUser) {
        assertEquals(firstUser.getId(), secondUser.getId());
        assertEquals(firstUser.getName(), secondUser.getName());
        assertEquals(firstUser.getEmail(), secondUser.getEmail());
        assertEquals(firstUser.getPassword(), secondUser.getPassword());
        assertEquals(firstUser.getRoleDTO().getId(), secondUser.getRoleDTO().getId());
        assertEquals(firstUser.getRoleDTO().getName(), secondUser.getRoleDTO().getName());
    }
}
