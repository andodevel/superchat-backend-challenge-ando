package de.superchat.message.service;

import de.superchat.message.dto.CreateRequest;
import de.superchat.message.repository.Message;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.UUID;
import javax.ws.rs.core.SecurityContext;

public interface MessageService {

    /**
     * List conversations with pagination.
     *
     * @param page
     * @param size
     * @return message that current logged in user is sender or receiver.
     */
    PanacheQuery<Message> list(Integer page, Integer size);

    /**
     * Create message. Not allowed external user to be receiver.
     *
     * @param createRequest
     * @return
     */
    UUID create(SecurityContext securityContext, CreateRequest createRequest) throws UnsupportException;

}
