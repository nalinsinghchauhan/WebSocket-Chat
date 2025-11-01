package com.example.websocketchat.event;

import com.example.websocketchat.dto.ChatMessageDTO;
import com.example.websocketchat.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

/**
 * WebSocket Event Listener.
 * Listens for session disconnect events.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messageSendingOperations;

    public WebSocketEventListener(SimpMessageSendingOperations messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    /**
     * Listens for WebSocket session disconnect events.
     *
     * @param event The disconnect event.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        // Retrieve the username from the session
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        
        if (username != null) {
            logger.info("User Disconnected: " + username);

            // Create a LEAVE message (for general notification if needed)
            ChatMessageDTO chatMessage = new ChatMessageDTO();
            chatMessage.setType(Message.MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setContent(username + " disconnected");
            chatMessage.setTimestamp(LocalDateTime.now());

            // Note: In a multi-room system, you might want to broadcast to all rooms
            // or track which rooms the user was in. For now, this is a general disconnect.
            // You can enhance this to send LEAVE messages to specific rooms.
        }
    }
}

