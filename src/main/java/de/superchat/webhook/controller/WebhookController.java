package de.superchat.webhook.controller;

import de.superchat.webhook.repository.Webhook;
import java.util.Date;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

@Path("/api/webhooks")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
@SecuritySchemes(value = {
    @SecurityScheme(securitySchemeName = "apiKey",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer")}
)
public class WebhookController {

    /*
     * NOTE: Due to the simplicity of this webhook, I directly use the repository access.
     */

    /**
     * Add new webhook
     *
     * @return
     */
    @POST
    @RolesAllowed({"USER"})
    @SecurityRequirement(name = "apiKey")
    @Transactional
    public Response create(@Context SecurityContext securityContext) {
        Webhook newHook = new Webhook();
        newHook.setUserId(UUID.fromString(securityContext.getUserPrincipal().getName()));
        newHook.setCreated(new Date());
        newHook.persist();

        return Response.ok().entity(newHook.getId().toString()).build();
    }

    /**
     * Delete webhook by id
     *
     * @param id
     * @return
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"USER"})
    @SecurityRequirement(name = "apiKey")
    @Transactional
    public Response delete(@Context SecurityContext securityContext, @PathParam("id") UUID id) {
        UUID userId = UUID.fromString(securityContext.getUserPrincipal().getName());
        Webhook.delete("id = ?1 and user_id = ?2", id, userId);
        return Response.accepted().build();
    }

    /**
     * Receive message from external source.I assume the message is anonymous and raw text. Because message api is
     * secured, we should not call it directly but broadcast an event.
     *
     * @param message raw string message.
     * @return
     */
    @POST
    @Path("/{id}/messages")
    public Response receiveMessage(@PathParam("id") UUID id, String message) {
        return Response.accepted().build();
    }

}
