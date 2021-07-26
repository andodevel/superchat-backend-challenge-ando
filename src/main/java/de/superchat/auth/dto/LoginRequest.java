package de.superchat.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message="username may not be blank")
    private String username;
    @NotBlank(message="password may not be blank")
    private String password;

}
