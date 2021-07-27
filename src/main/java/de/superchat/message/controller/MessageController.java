package de.superchat.message.controller;

import de.superchat.message.dto.CreateRequest;
import de.superchat.message.dto.ListResponse;
import de.superchat.message.dto.MessageDTO;
import de.superchat.message.dto.UserDTO;
import de.superchat.message.repository.Message;
import de.superchat.message.service.MessageService;
import de.superchat.message.service.UnsupportException;
import de.superchat.message.service.UserService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
    @SecurityScheme(securitySchemeName = "apiKey",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer")}
)
public class MessageController {

    public static final Logger LOGGER = Logger.getLogger(MessageController.class);

    @Inject
    MessageService messageService;
    @RestClient
    UserService userService;

    /**
     * List previous messages ordered by created date.
     *
     * @param page
     * @param size
     * @return
     */
    @GET
    @RolesAllowed({"USER"})
    @SecurityRequirement(name = "apiKey")
    public Response list(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        PanacheQuery<Message> pagedMessages = messageService.list(page, size);
        List<Message> messages = pagedMessages.list();
        if (CollectionUtils.isEmpty(messages)) {
            return Response.ok(new ListResponse(
                0L,
                0,
                0,
                0, Collections.emptyList())).build();
        }

        Page pageData = pagedMessages.page();
        return Response.ok(new ListResponse(
            pagedMessages.count(),
            pagedMessages.pageCount(),
            pageData.index,
            pageData.size,
            messages.stream().map(message -> {
                UserDTO sender = userService.findUserById(message.getSenderId());
                UserDTO receiver = userService.findUserById(message.getReceiverId());
                return new MessageDTO(message, sender, receiver);
            }).collect(Collectors.toList()))).build();
    }

    /**
     * Send message from current logged user to a Superchat user
     *
     * @param securityContext
     * @param createRequest
     * @return
     */
    @POST
    @RolesAllowed({"USER"})
    @SecurityRequirement(name = "apiKey")
    public Response create(@Context SecurityContext securityContext, @Valid CreateRequest createRequest) {
        validateReceiver(createRequest);

        UUID uuid;
        try {
            uuid = messageService.create(securityContext, createRequest);
        } catch (UnsupportException e) {
            LOGGER.error("Failed to create new message");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }
        if (uuid == null) {
            LOGGER.error("Failed to create new message");
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }

        return Response.accepted().build();
    }

    private void validateReceiver(CreateRequest createRequest) {
        String receiverId = createRequest.getReceiverId();
        String receiverUsername = createRequest.getReceiverUsername();
        String receiverEmail = createRequest.getReceiverEmail();
        if (StringUtils.isAllBlank(receiverId, receiverUsername, receiverEmail)) {
            throw new ValidationException("Require at least one sender identification: "
                + "receiverId or receiverUsername or receiverEmail");
        }

        if (StringUtils.isNotBlank(receiverId)) {
            try {
                UUID.fromString(receiverId);
            } catch (Exception ex) {
                throw new ValidationException("receiverId must be UUID");
            }
        }


    }

}