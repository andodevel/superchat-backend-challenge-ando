package de.superchat.user.controller;

import de.superchat.user.dto.CreateRequest;
import de.superchat.user.dto.ListResponse;
import de.superchat.user.dto.SimpleResponse;
import de.superchat.user.dto.UserDTO;
import de.superchat.user.repository.User;
import de.superchat.user.repository.UserInfo;
import de.superchat.user.utils.Utils;
import io.quarkus.elytron.security.common.BcryptUtil;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    // This CRUD controller call repository directly, not through a redundant user service.

    public static final Logger LOGGER = Logger.getLogger(UserController.class);

    @ConfigProperty(name = "de.superchat.auth.bcrypt.secret")
    String bcryptSecret;
    @ConfigProperty(name = "de.superchat.auth.bcrypt.count")
    short bcryptCount;

    @GET
    @RolesAllowed({"USER"})
    public Response list() {
        List<UserDTO> users = User.<User>listAll().stream().map(UserDTO::new).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(users)) {
            return Response.ok(new ListResponse(0, 0, 0, Collections.emptyList())).build();
        }

        return Response.ok(new ListResponse(0, 0, 0, users)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER"})
    public Response get(@PathParam("id") UUID id) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(new SimpleResponse(new UserDTO(user))).build();
    }

    @GET
    @Path("/search/{usernameOrEmail}")
    @RolesAllowed({"USER"})
    public Response search(@PathParam("usernameOrEmail") String usernameOrEmail) {
        User user = User.findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(new SimpleResponse(new UserDTO(user))).build();
    }

    @POST
    @Transactional
    public Response create(@Valid CreateRequest createRequest) {
        String username = createRequest.getUsername().trim();
        String email = createRequest.getEmail().trim();
        User dbUser = User.find("username = ?1 OR email = ?2", username, email).firstResult();
        if (dbUser != null) {
            return Response.status(Status.CONFLICT).build();
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        byte[] salt = Utils.randomBcryptSalt();
        String password = createRequest.getPassword().trim();
        String hashedPassword = BcryptUtil.bcryptHash(bcryptSecret + password, bcryptCount, salt);
        newUser.setSalt(Hex.encodeHexString(salt));
        newUser.setPassword(hashedPassword);
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(newUser);
        userInfo.setFirstname(createRequest.getFirstname());
        userInfo.setLastname(createRequest.getLastname());
        userInfo.persist();
        newUser.persist();

        return Response.created(URI.create("/api/users/" + newUser.getId())).build();
    }

}
