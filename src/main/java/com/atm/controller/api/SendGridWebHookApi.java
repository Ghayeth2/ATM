package com.atm.controller.api;

//import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SendGrid WebHook endpoint is only for monitoring mails
 * delivery to users via SendGrid server.
 * The requests will be received from ATM's publicly
 * exposed endpoint vai Ngrok
 */
@RestController
public class SendGridWebHookApi {

    private static final Logger log = LoggerFactory.getLogger(SendGridWebHookApi.class);

    // Webhook endpoint to receive SendGrid events
    @PostMapping("/sendgrid/webhook")
    public void handleSendGridEvent(@RequestBody String payload) {
        try {
            // Parse the incoming event payload (JSON)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode events = mapper.readTree(payload);

            for (JsonNode event : events) {
                String eventType = event.get("event").asText();
                String email = event.get("email").asText();
                String reason = event.has("reason") ? event.get("reason").asText() : null;
                String bounceReason = event.has("bounce_reason") ? event.get("bounce_reason").asText() : null;
                String status = event.has("status") ? event.get("status").asText() : null;

                // Log based on event type
                switch (eventType) {
                    case "delivered":
                        log.info("Email delivered to {}.", email);
                        break;
                    case "bounced":
                        log.error("Email to {} bounced. Reason: {}. Status: {}", email, bounceReason, status);
                        break;
                    case "blocked":
                        log.error("Email to {} blocked. Reason: {}", email, reason);
                        break;
                    case "spam_report":
                        log.warn("Email to {} marked as spam.", email);
                        break;
                    case "deferred":
                        log.warn("Email to {} deferred. Reason: {}", email, reason);
                        break;
                    default:
                        log.info("Unknown event type: {} for {}", eventType, email);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error processing SendGrid event webhook", e);
        }
    }
}
