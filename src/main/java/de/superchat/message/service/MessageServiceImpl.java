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
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
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
    @Inject
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
    public PanacheQuery<Message> list(UUID userId, Integer page, Integer size) {
        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 0 ? defaultPageSize : size;
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        PanacheQuery<PanacheEntityBase> all = Message.find("sender_id = ?1 or receiver_id = ?1",
            Sort.descending("created"),
            userId);
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
    public UUID create(UUID senderId, CreateRequest createRequest)
        throws UnsupportException, ResourceNotFoundException {
        UUID receiverId = null;
        if (senderId == null) {
            /*
             * TODO: Webhook would fail to retrieve user info due to its lack of auth context.
             * We could solve this issue by broadcast message to a fix data background jobs or
             * make consumer RunAs system/admin user.
             */
            String reqReceiverId = createRequest.getReceiverId();
            if (StringUtils.isNotBlank(reqReceiverId)) {
                receiverId = UUID.fromString(reqReceiverId.trim());
            }
        } else {
            UserDTO receiver = getReceiver(createRequest);
            if (receiver == null || receiver.getId() == null) {
                LOGGER.error("Receiver not found!");
                throw new ResourceNotFoundException();
            }

            if (StringUtils.isNotBlank(receiver.getSource()) && !"SC".equals(receiver.getSource())) {
                LOGGER.error("External receiver not supported!");
                throw new UnsupportException();
            }
            receiverId = receiver.getId();
        }

        if (receiverId == null) {
            LOGGER.error("Receiver not found!");
            throw new ResourceNotFoundException();
        }

        Message newMessage = new Message();
        newMessage.setSource("AN"); // Anonymous, expected to be called by webhook
        if (senderId != null) {
            newMessage.setSenderId(senderId);
            newMessage.setSource("SC");
        }
        newMessage.setReceiverId(receiverId);
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
        String receiverId = StringUtils.trim(createRequest.getReceiverId());
        String receiverUsername = StringUtils.trim(createRequest.getReceiverUsername());
        String receiverEmail = StringUtils.trim(createRequest.getReceiverEmail());

        Response apiResponse = null;
        if (StringUtils.isNoneBlank(receiverId)) {
            apiResponse = userService.findUserById(UUID.fromString(receiverId));
        } else if (StringUtils.isNoneBlank(receiverUsername)) {
            apiResponse = userService.findUserByUsernameOrEmail(receiverUsername);
        } else if (StringUtils.isNoneBlank(receiverEmail)) {
            apiResponse = userService.findUserByUsernameOrEmail(receiverEmail);
        }

        if (apiResponse != null && Status.OK.getStatusCode() == apiResponse.getStatus()) {
            return apiResponse.readEntity(UserDTO.class);
        }

        if (StringUtils.isNoneBlank(receiverId)) {
            UserDTO webhookReceiver = new UserDTO();
            webhookReceiver.setId(UUID.fromString(receiverId));
            return webhookReceiver;
        }

        return null;
    }
}
