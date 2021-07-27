package de.superchat.user.dto;

import de.superchat.user.repository.User;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class UserDTO implements Serializable {

    private UUID id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;

    public UserDTO(User entity) {
        this.setId(entity.getId());
        this.setUsername(entity.getUsername());
        this.setEmail(entity.getEmail());
        this.setFirstname(entity.getUserInfo().getFirstname());
        this.setLastname(entity.getUserInfo().getLastname());
    }

}
