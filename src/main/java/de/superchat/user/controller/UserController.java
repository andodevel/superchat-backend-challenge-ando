package de.superchat.user.controller;

import de.superchat.user.dto.CreateRequest;
import de.superchat.user.dto.ListResponse;
import de.superchat.user.dto.SimpleResponse;
import de.superchat.user.dto.UserDTO;
import de.superchat.user.repository.User;
import de.superchat.user.service.UserService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.logging.Logger;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    public static final Logger LOGGER = Logger.getLogger(UserController.class);

    @Inject
    UserService userService;

    @GET
    @RolesAllowed({"USER"})
    public Response list(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        PanacheQuery<User> pagedUsers = userService.list(page, size);
        List<User> users = pagedUsers.list();
        if (CollectionUtils.isEmpty(users)) {
            return Response.ok(new ListResponse(
                0L,
                0,
                0,
                0, Collections.emptyList())).build();
        }

        Page pageData = pagedUsers.page();
        return Response.ok(new ListResponse(
            pagedUsers.count(),
            pagedUsers.pageCount(),
            pageData.index,
            pageData.size,
            users.stream().map(UserDTO::new).collect(Collectors.toList()))).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER"})
    public Response get(@PathParam("id") UUID id) {
        User user = userService.find(id);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(new SimpleResponse(new UserDTO(user))).build();
    }

    @GET
    @Path("/search/{usernameOrEmail}")
    @RolesAllowed({"USER"})
    public Response search(@PathParam("usernameOrEmail") String usernameOrEmail) {
        User user = userService.findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(new SimpleResponse(new UserDTO(user))).build();
    }

    @POST
    @PermitAll
    public Response create(@Valid CreateRequest createRequest) {
        UUID uuid;
        try {
            uuid = userService.create(createRequest);
        } catch (Exception e) {
            LOGGER.error("Failed to create new user");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if (uuid == null) {
            LOGGER.error("Failed to create new user");
            return Response.status(Status.CONFLICT).build();
        }

        return Response.created(URI.create("/api/users/" + uuid)).build();
    }

}
