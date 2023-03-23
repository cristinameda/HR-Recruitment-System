package com.nagarro.recruitmenthelper.usermanagement.repository;

import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    /**
     * Adds a new user
     *
     * @param user the User that will be inserted
     * @return the user just added with the id set
     */
    User addUser(User user);

    /**
     * Returns the user from database with the given id.
     *
     * @param id the id of the user
     * @return Optional<User> an Optional containing the user, or
     * an empty Optional.
     */
    Optional<User> findById(Long id);

    /**
     * Returns users from database using pagination.
     *
     * @param page     the number of the page (according to the pageSize)
     * @param pageSize the number of users included on a page
     * @return List<User> the list of users
     */
    List<User> findAll(int page, int pageSize);

    /**
     * Deletes a user by id
     *
     * @param userId the identifier for the User that will be erased
     */
    void delete(long userId);

    /**
     * Finds a user by email
     *
     * @param email String identifier for user
     * @return empty Optional if the User is not found or Optional of user
     */
    Optional<User> findUserByEmail(String email);
}
