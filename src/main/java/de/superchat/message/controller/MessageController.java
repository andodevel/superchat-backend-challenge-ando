package de.superchat.message.controller;

import de.superchat.message.dto.ListResponse;
import de.superchat.message.dto.MessageDTO;
import de.superchat.message.dto.UserDTO;
import de.superchat.message.repository.Message;
import de.superchat.message.service.MessageService;
import de.superchat.message.service.UserService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
    @SecurityScheme(securitySchemeName = "apiKey",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer")}
)
public class MessageController {

    @Inject
    MessageService messageService;
    @RestClient
    UserService userService;

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

//    @POST
//    @RolesAllowed({"USER"})
//    @SecurityRequirement(name = "apiKey")
//    public Response create(@Valid CreateRequest createRequest) {
//        UUID uuid;
//        try {
//            uuid = userService.create(createRequest);
//        } catch (Exception e) {
//            LOGGER.error("Failed to create new user");
//            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
//        }
//
//        if (uuid == null) {
//            LOGGER.error("Failed to create new user");
//            return Response.status(Status.CONFLICT).build();
//        }
//
//        return Response.created(URI.create("/api/users/" + uuid)).build();
//    }

}