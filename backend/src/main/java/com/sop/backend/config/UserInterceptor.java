package com.sop.backend.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.Objects;

/**
 * Intercepts WebSocket messages to set the user principal based on the userId stored during handshake.
 * This allows Spring to route user-specific messages correctly.
 * Only processes connections to /ws-notifications endpoint.
 */
@Component
public class UserInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String destination = accessor.getDestination();
            System.out.println("=== UserInterceptor: Processing CONNECT ===");
            System.out.println("Destination: " + destination);

            Object userIdObj = Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");

            if (userIdObj != null) {
                String userId = userIdObj.toString();
                System.out.println("UserId from session: " + userId);

                Principal principal = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                );

                accessor.setUser(principal);
                System.out.println("Principal set for user: " + userId);
            } else {
                System.out.println("No userId in session - skipping (probably /ws endpoint)");
            }
        }

        return message;
    }
}