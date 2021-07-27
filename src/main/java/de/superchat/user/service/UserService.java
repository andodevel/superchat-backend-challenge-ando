package de.superchat.user.service;

import de.superchat.user.dto.CreateRequest;
import de.superchat.user.repository.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.UUID;

public interface UserService {

    /**
     * Find user by their id
     *
     * @param id
     * @return
     */
    User find(UUID id);

    /**
     * Find user by username or email
     *
     * @param usernameOrEmail
     * @return
     */
    User findByUsernameOrEmail(String usernameOrEmail);

    /**
     * List users with pagination
     *
     * @param page
     * @param size
     * @return
     */
    PanacheQuery<User> list(Integer page, Integer size);

    /**
     * Create new user
     *
     * @param createRequest
     * @return
     */
    UUID create(CreateRequest createRequest) throws ResourceConflictException;

}
