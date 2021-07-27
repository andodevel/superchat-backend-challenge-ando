package de.superchat.user.service;

import de.superchat.user.dto.CreateRequest;
import de.superchat.user.repository.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.UUID;

public interface UserService {

    User find(UUID id);

    User findByUsernameOrEmail(String usernameOrEmail);

    PanacheQuery<User> list(Integer page, Integer size);

    UUID create(CreateRequest createRequest);

}
