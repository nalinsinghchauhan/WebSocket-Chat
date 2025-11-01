package com.example.websocketchat.dto;

import com.example.websocketchat.model.Message;
import java.time.LocalDateTime;

/**
 * DTO for chat messages sent/received via WebSocket.
 */
public class ChatMessageDTO {

    private Message.MessageType type;
    private String content;
    private String sender;
    private String recipient; // For DMs
    private String roomName; // For room messages
    private LocalDateTime timestamp;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt; // For read receipts
    private Long messageId; // For read receipts

    // Constructors
    public ChatMessageDTO() {
    }

    public ChatMessageDTO(Message.MessageType type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Message.MessageType getType() {
        return type;
    }

    public void setType(Message.MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}

