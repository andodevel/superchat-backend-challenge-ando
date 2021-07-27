package de.superchat.message.dto;

import java.util.UUID;
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

    private String receiverId;
    private String receiverUsername;
    private String receiverEmail;
    @Size(min = 1, max = 102400)
    private String content;

}