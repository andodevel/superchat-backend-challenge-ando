package de.superchat.auth.controller;

import de.superchat.auth.dto.LoginRequest;
import de.superchat.auth.dto.LoginResponse;
import de.superchat.auth.dto.SimpleResponse;
import de.superchat.auth.repository.Role;
import de.superchat.auth.repository.User;
import de.superchat.auth.service.AuthService;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.jboss.logging.Logger;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    public static final Logger LOGGER = Logger.getLogger(AuthController.class);

    @Inject
    AuthService authService;

    /**
     * This API is target for Web application to exchange user/password for JWT access token. Other REST API client
     * should send stateless request with basic auth or token bearer header. Thus, XSS and CSRF are not concern of the
     * API but Web server and client.
     *
     * @param loginRequest username and password
     * @return JWT access token or error
     */
    @PermitAll
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            LOGGER.info("User " + loginRequest.getUsername() + " has been logged in");
            return Response.ok(new LoginResponse(authService.generateJWTToken(user))).build();
        } else {
            LOGGER.info("User " + loginRequest.getUsername() + " was failed to log in");
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Query current logged in user.
     *
     * @param securityContext injected current security context
     * @return logged in username/email
     */
    @RolesAllowed({Role.USER_ROLE})
    @GET
    @Path("/me")
    public Response me(@Context SecurityContext securityContext) {
        return Response.ok(new SimpleResponse(securityContext.getUserPrincipal().getName())).build();
    }

}
