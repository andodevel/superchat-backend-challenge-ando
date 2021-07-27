package de.superchat.auth.controller;

import de.superchat.auth.dto.LoginRequest;
import de.superchat.auth.repository.AuthUser;
import de.superchat.auth.service.AuthService;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.jboss.logging.Logger;

@Path("/api/auth")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
    @SecurityScheme(securitySchemeName = "apiKey",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer")}
)
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
    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest loginRequest) {
        AuthUser authUser = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (authUser != null) {
            LOGGER.info("User " + loginRequest.getUsername() + " has been logged in");
            return Response.ok(authService.generateJWTToken(authUser)).build();
        } else {
            LOGGER.info("User " + loginRequest.getUsername() + " was failed to log in");
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Query current logged in user.
     *
     * @param securityContext injected current security context
     * @return logged in user id
     */
    @GET
    @Path("/me")
    @RolesAllowed({"USER"})
    @SecurityRequirement(name = "apiKey")
    public Response me(@Context SecurityContext securityContext) {
        return Response.ok(securityContext.getUserPrincipal().getName()).build();
    }

}
