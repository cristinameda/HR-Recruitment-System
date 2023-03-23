package com.nagarro.recruitmenthelper.usermanagement.service.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserNotFoundException;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.service.UserService;
import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordHashGenerator passwordHashGenerator;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordHashGenerator passwordHashGenerator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordHashGenerator = passwordHashGenerator;
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        User user = mapToUser(userDTO);

        encryptPassword(user);

        return mapToUserDTO(userRepository.addUser(user));
    }

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserDTO)
                .orElseThrow(() -> new UserNotFoundException("User with id: '" + id + "' was not found"));
    }

    @Override
    public List<UserDTO> findAll(int page, int pageSize) {
        if (page <= 0 || pageSize < 0) {
            throw new IllegalArgumentException("Page must be positive and pageSize positive or 0");
        }
        List<User> users = userRepository.findAll(page, pageSize);
        return users.stream().map(this::mapToUserDTO).toList();
    }

    @Override
    public void delete(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.delete(userId);
        } else {
            throw new UserNotFoundException("User with id: '" + userId + "' not found");
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: '" + email + "' was not found"));
    }

    @Override
    public boolean areUsersValid(List<UserEmailRoleDTO> userEmailRoleDTOS) {
        return userEmailRoleDTOS.stream().
                allMatch(this::isUserValid);
    }

    private User mapToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private boolean isUserValid(UserEmailRoleDTO userRoleDTO) {
        return userRepository.findUserByEmail(userRoleDTO.getEmail())
                .stream()
                .anyMatch(user -> user.getRole().getName().equals(userRoleDTO.getRoleName()));
    }

    private void encryptPassword(User user) {
        String hash = passwordHashGenerator.hashPassword(user.getPassword());
        if (hash == null) {
            throw new IllegalArgumentException("Hash algorithm not found! The hash cannot be passed as a null argument.");
        }
        user.setPassword(hash);
    }
}
