package de.superchat.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {

    @Size(min = 6, max = 64)
    @NotBlank(message="username may not be blank")
    @Pattern(regexp = "^[ ]*[a-zA-Z0-9_-]+[ ]*$")
    private String username;
    @Email
    @Size(min = 4, max = 128)
    @NotBlank(message="email may not be blank")
    private String email;
    @Size(min = 8, max = 64)
    @NotBlank(message="password may not be blank")
    private String password;
    @Size(min = 1, max = 64)
    private String firstname;
    @Size(min = 1, max = 64)
    private String lastname;

}
