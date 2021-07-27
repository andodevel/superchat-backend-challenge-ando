package de.superchat.message.service;

import de.superchat.message.repository.Message;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

public interface MessageService {

    /**
     * List message with pagination. Support filters
     *
     * @param page
     * @param size
     * @return
     */
    PanacheQuery<Message> list(Integer page, Integer size);

}
