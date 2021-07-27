package de.superchat.message.service;

import de.superchat.message.dto.UserDTO;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/rate")
@RegisterRestClient
@RegisterClientHeaders
public interface UserService {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 1000)
    @Fallback(UserServiceFallback.class)
    UserDTO findUserById(@PathParam("id") UUID id);

}