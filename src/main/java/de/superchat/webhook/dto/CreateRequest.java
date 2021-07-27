package de.superchat.webhook.dto;

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