package de.superchat.webhook.controller;

import de.superchat.message.service.EventBasedMessagingService;
import de.superchat.webhook.repository.Webhook;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.reactive.messaging.kafka.Record;
import java.util.Date;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@Path("/api/webhooks")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
@SecuritySchemes(value = {
    @SecurityScheme(securitySchemeName = "apiKey",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer")}
)
public class WebhookController {

    private final Logger LOGGER = Logger.getLogger(WebhookController.class);

    /*
     * NOTE: Due to the simplicity of this webhook, I directly use the repository access.
     */

    @Inject
    @Channel("webhook-message-out")
    Emitter<Record<String, String>> emitter;

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
     * secured, we should not call it directly but broadcast an event to Kafka.
     *
     * @param message raw string message.
     * @return
     */
    @POST
    @Path("/{id}/messages")
    public Response receiveMessage(@PathParam("id") UUID id, String message) {
        Webhook webhook = Webhook.findById(id);
        if (webhook == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        String receiverId = webhook.getUserId().toString();
        emitter.send(Record.of(receiverId, message));
        LOGGER.info("Sent webhook message to Kafka. Receiver id " + receiverId);
        return Response.accepted().build();
    }

}
