package de.superchat.message.service;

import java.util.UUID;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Singleton
@Path("/users")
@RegisterRestClient(configKey = "user-api")
public interface UserService {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 2, delay = 200)
    @Fallback(FindUserByIdFallback.class)
    Response findUserById(@PathParam("id") UUID id);

    @GET
    @Path("/search/{usernameOrEmail}")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 2, delay = 200)
    @Fallback(FindUserByUsernameOrEmailFallback.class)
    Response findUserByUsernameOrEmail(@PathParam("usernameOrEmail") String usernameOrEmail);

}