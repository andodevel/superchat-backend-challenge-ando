package de.superchat.user.model;

import de.superchat.user.repository.User;
import de.superchat.user.repository.UserInfo;

public class UserDTO {

    public String username;

    public String password;

    public UserDTO(User user, UserInfo userInfo) {

    }
}
