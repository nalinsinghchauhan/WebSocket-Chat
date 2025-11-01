package com.example.websocketchat.controller;

import com.example.websocketchat.dto.ChatMessageDTO;
import com.example.websocketchat.model.Message;
import com.example.websocketchat.model.ReadReceipt;
import com.example.websocketchat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket controller for private/direct messaging.
 * 
 * Message mappings:
 * - /app/dm/send - Send a private message
 * - /app/dm/read - Mark a message as read
 * 
 * Message destinations:
 * - /user/{recipientUsername}/queue/messages - Private messages to specific user
 * - /user/{senderUsername}/queue/read-receipts - Read receipts to sender
 */
@Controller
public class PrivateMessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

    public PrivateMessageController(MessageService messageService,
                                   SimpMessagingTemplate messagingTemplate,
                                   SimpUserRegistry userRegistry) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.userRegistry = userRegistry;
    }

    /**
     * Handle sending a private/direct message.
     * Client sends to: /app/dm/send
     * Server sends to: /user/{recipientUsername}/queue/messages
     * 
     * @param messageDTO The message DTO containing recipient and content
     * @param headerAccessor Accessor for session attributes
     */
    @MessageMapping("/dm/send")
    public void sendPrivateMessage(@Payload ChatMessageDTO messageDTO,
                                   SimpMessageHeaderAccessor headerAccessor) {
        String senderUsername = (String) headerAccessor.getSessionAttributes().get("username");
        String recipientUsername = messageDTO.getRecipient();

        if (recipientUsername == null || recipientUsername.isEmpty()) {
            // Send error back to sender
            ChatMessageDTO errorMessage = new ChatMessageDTO();
            errorMessage.setType(Message.MessageType.DM);
            errorMessage.setSender("System");
            errorMessage.setContent("Error: Recipient username is required");
            messagingTemplate.convertAndSendToUser(senderUsername, "/queue/messages", errorMessage);
            return;
        }

        // Set sender and type
        messageDTO.setSender(senderUsername);
        messageDTO.setType(Message.MessageType.DM);
        messageDTO.setTimestamp(LocalDateTime.now());

        try {
            // Save message to database
            var savedMessage = messageService.savePrivateMessage(messageDTO, senderUsername, recipientUsername);
            messageDTO.setMessageId(savedMessage.getId());
            messageDTO.setDeliveredAt(savedMessage.getDeliveredAt());

            // Check if recipient is online
            boolean isRecipientOnline = userRegistry.getUsers().stream()
                    .anyMatch(user -> user.getName().equals(recipientUsername));

            // Send message to recipient
            messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/messages", messageDTO);

            // Send confirmation to sender
            ChatMessageDTO confirmation = new ChatMessageDTO();
            confirmation.setType(Message.MessageType.DM);
            confirmation.setSender("System");
            confirmation.setRecipient(recipientUsername);
            confirmation.setContent("Message delivered" + (isRecipientOnline ? " (user online)" : " (user offline)"));
            confirmation.setTimestamp(LocalDateTime.now());
            messagingTemplate.convertAndSendToUser(senderUsername, "/queue/messages", confirmation);

        } catch (Exception e) {
            // Send error to sender
            ChatMessageDTO errorMessage = new ChatMessageDTO();
            errorMessage.setType(Message.MessageType.DM);
            errorMessage.setSender("System");
            errorMessage.setContent("Error sending message: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(senderUsername, "/queue/messages", errorMessage);
        }
    }

    /**
     * Handle marking a message as read.
     * Client sends to: /app/dm/read
     * Server sends read receipt to: /user/{senderUsername}/queue/read-receipts
     * 
     * @param messageDTO The message DTO containing messageId
     * @param headerAccessor Accessor for session attributes
     */
    @MessageMapping("/dm/read")
    public void markMessageAsRead(@Payload ChatMessageDTO messageDTO,
                                  SimpMessageHeaderAccessor headerAccessor) {
        String readerUsername = (String) headerAccessor.getSessionAttributes().get("username");
        Long messageId = messageDTO.getMessageId();

        if (messageId == null) {
            return;
        }

        try {
            ReadReceipt receipt = messageService.markAsRead(messageId, readerUsername);
            
            // Create read receipt DTO
            ChatMessageDTO readReceiptDTO = new ChatMessageDTO();
            readReceiptDTO.setType(Message.MessageType.READ_RECEIPT);
            readReceiptDTO.setMessageId(messageId);
            readReceiptDTO.setSender(readerUsername);
            readReceiptDTO.setReadAt(receipt.getReadAt());
            readReceiptDTO.setTimestamp(LocalDateTime.now());

            // Get the original message to find the sender
            var messageOpt = messageService.getMessageById(messageId);
            if (messageOpt.isPresent()) {
                String senderUsername = messageOpt.get().getSender().getUsername();
                // Send read receipt to the original sender
                messagingTemplate.convertAndSendToUser(senderUsername, "/queue/read-receipts", readReceiptDTO);
            }

        } catch (Exception e) {
            System.err.println("Error marking message as read: " + e.getMessage());
        }
    }
}

