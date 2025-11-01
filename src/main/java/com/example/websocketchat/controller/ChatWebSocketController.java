package com.example.websocketchat.controller;

import com.example.websocketchat.dto.ChatMessageDTO;
import com.example.websocketchat.model.Message;
import com.example.websocketchat.service.ChatRoomService;
import com.example.websocketchat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket controller for room chat messages.
 * 
 * Message mappings:
 * - /app/chat/{roomName}/send - Send a message to a specific room
 * - /app/chat/{roomName}/join - Join a room
 * - /app/chat/{roomName}/leave - Leave a room
 * 
 * Message destinations:
 * - /topic/room.{roomName} - Room-specific messages
 */
@Controller
public class ChatWebSocketController {

    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(MessageService messageService, ChatRoomService chatRoomService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.chatRoomService = chatRoomService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle messages sent to a specific room.
     * Client sends to: /app/chat/send
     * The messageDTO must contain roomName.
     * Server broadcasts to: /topic/room.{roomName}
     * 
     * @param messageDTO The message DTO (must contain roomName)
     * @param headerAccessor Accessor for session attributes
     */
    @MessageMapping("/chat/send")
    public void sendRoomMessage(@Payload ChatMessageDTO messageDTO,
                                SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomName = messageDTO.getRoomName();

        if (roomName == null || roomName.isEmpty()) {
            return; // Invalid message
        }

        messageDTO.setSender(username);
        messageDTO.setType(Message.MessageType.CHAT);
        messageDTO.setTimestamp(LocalDateTime.now());

        try {
            var room = chatRoomService.getRoomEntityByName(roomName);
            var savedMessage = messageService.saveRoomMessage(messageDTO, room, username);
            messageDTO.setMessageId(savedMessage.getId());
            messageDTO.setDeliveredAt(savedMessage.getDeliveredAt());
        } catch (Exception e) {
            System.err.println("Error saving room message: " + e.getMessage());
        }

        // Broadcast to the specific room
        messagingTemplate.convertAndSend("/topic/room." + roomName, messageDTO);
    }

    /**
     * Handle user joining a room.
     * Client sends to: /app/chat/join
     * The messageDTO must contain roomName.
     * Server broadcasts to: /topic/room.{roomName}
     * 
     * @param messageDTO The message DTO (must contain roomName)
     * @param headerAccessor Accessor for session attributes
     */
    @MessageMapping("/chat/join")
    public void joinRoom(@Payload ChatMessageDTO messageDTO,
                         SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomName = messageDTO.getRoomName();

        if (roomName == null || roomName.isEmpty()) {
            return;
        }

        ChatMessageDTO message = new ChatMessageDTO();
        message.setType(Message.MessageType.JOIN);
        message.setSender(username);
        message.setRoomName(roomName);
        message.setContent(username + " joined the room");
        message.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/room." + roomName, message);
    }

    /**
     * Handle user leaving a room.
     * Client sends to: /app/chat/leave
     * The messageDTO must contain roomName.
     * Server broadcasts to: /topic/room.{roomName}
     * 
     * @param messageDTO The message DTO (must contain roomName)
     * @param headerAccessor Accessor for session attributes
     */
    @MessageMapping("/chat/leave")
    public void leaveRoom(@Payload ChatMessageDTO messageDTO,
                          SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomName = messageDTO.getRoomName();

        if (roomName == null || roomName.isEmpty()) {
            return;
        }

        ChatMessageDTO message = new ChatMessageDTO();
        message.setType(Message.MessageType.LEAVE);
        message.setSender(username);
        message.setRoomName(roomName);
        message.setContent(username + " left the room");
        message.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/room." + roomName, message);
    }
}

