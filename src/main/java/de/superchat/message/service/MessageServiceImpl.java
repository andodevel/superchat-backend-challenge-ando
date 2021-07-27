package de.superchat.message.service;

import de.superchat.message.dto.CreateRequest;
import de.superchat.message.dto.UserDTO;
import de.superchat.message.repository.Message;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.Date;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MessageServiceImpl implements MessageService {

    public static final Logger LOGGER = Logger.getLogger(MessageService.class);

    @ConfigProperty(name = "de.superchat.auth.default.page.size")
    Integer defaultPageSize;
    @ConfigProperty(name = "de.superchat.auth.max.page.size")
    Integer maxPageSize;
    @RestClient
    UserService userService;

    /**
     * List conversations with pagination.
     *
     * @param page
     * @param size
     * @return message that current logged in user is sender or receiver.
     */
    @Override
    public PanacheQuery<Message> list(Integer page, Integer size) {
        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 0 ? defaultPageSize : size;
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        PanacheQuery<PanacheEntityBase> all = Message.findAll(Sort.descending("created"));
        LOGGER.info("Query messages with page " + page + ", " + size);
        return all.page(Page.of(pageIndex, pageSize));
    }

    /**
     * Create message. Not allowed external user to be receiver.
     *
     * @param createRequest
     * @return
     */
    @Override
    @Transactional
    public UUID create(SecurityContext securityContext, CreateRequest createRequest) throws UnsupportException {
        UserDTO receiver = getReceiver(createRequest);
        if (receiver == null) {
            LOGGER.error("Receiver not found!");
            throw new UnsupportException();
        }

        if (!"SC".equals(receiver.getSource())) {
            LOGGER.error("External receiver not supported!");
            throw new UnsupportException();
        }

        Message newMessage = new Message();
        newMessage.setSource("SC");
        if (securityContext != null) {
            newMessage.setSenderId(UUID.fromString(securityContext.getUserPrincipal().getName()));
            newMessage.setSource("AN"); // Anonymous, expected to be called by webhook
        }
        newMessage.setReceiverId(receiver.getId());
        newMessage.setContent(createRequest.getContent());
        newMessage.setRoomId(null);
        newMessage.setCreated(new Date());
        newMessage.persist();

        return newMessage.getId();
    }

    /**
     * Get receiver by query user service through REST call
     *
     * @param createRequest
     * @return user if exists
     */
    private UserDTO getReceiver(CreateRequest createRequest) {
        String receiverId = createRequest.getReceiverId().trim();
        String receiverUsername = createRequest.getReceiverUsername().trim();
        String receiverEmail = createRequest.getReceiverEmail().trim();

        if (StringUtils.isNoneBlank(receiverId)) {
            return userService.findUserById(UUID.fromString(receiverId));
        }

        if (StringUtils.isNoneBlank(receiverUsername)) {
            return userService.findUserByUsernameOrEmail(receiverUsername);
        }

        if (StringUtils.isNoneBlank(receiverUsername)) {
            return userService.findUserByUsernameOrEmail(receiverEmail);
        }

        return null;
    }
}
