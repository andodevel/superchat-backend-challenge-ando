package de.superchat.message.service;

import de.superchat.message.dto.CreateRequest;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.Record;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventBasedMessagingService {

    private final Logger LOGGER = Logger.getLogger(EventBasedMessagingService.class);

    @Inject
    MessageService messageService;

    @Blocking
    @Incoming("webhook-message-in")
    public void receiveWebhookMessage(Record<String, String> record) {
        String receiverId = record.key();
        String content = record.value();
        LOGGER.info("Received webhook message from Kafka. Receiver id " + receiverId);
        CreateRequest request = new CreateRequest();
        request.setReceiverId(receiverId);
        request.setContent(content);
        try {
            messageService.create(null, request);
        } catch (Exception e) {
            LOGGER.error("Failed to create webhook message!", e);
        }
    }

}
