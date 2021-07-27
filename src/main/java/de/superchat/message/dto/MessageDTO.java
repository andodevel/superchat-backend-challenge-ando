package de.superchat.message.dto;

import de.superchat.message.repository.Message;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class MessageDTO implements Serializable {

    private UUID id;
    private UserDTO sender;
    private UserDTO receiver;
    private String content;
    private Date created;

    public MessageDTO(Message entity, UserDTO sender, UserDTO receiver) {
        this.setId(entity.getId());
        this.setContent(entity.getContent());
        this.setCreated(entity.getCreated());
        this.setSender(sender);
        this.setReceiver(receiver);
    }

}
