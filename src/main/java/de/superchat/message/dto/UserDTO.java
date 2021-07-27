package de.superchat.message.dto;

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

}
