package com.example.websocketchat.service;

import com.example.websocketchat.dto.ChatMessageDTO;
import com.example.websocketchat.model.*;
import com.example.websocketchat.repository.MessageRepository;
import com.example.websocketchat.repository.ReadReceiptRepository;
import com.example.websocketchat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for message operations (saving, retrieving, read receipts).
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReadReceiptRepository readReceiptRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository,
                         ReadReceiptRepository readReceiptRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.readReceiptRepository = readReceiptRepository;
    }

    /**
     * Save a room message.
     * 
     * @param messageDTO Message DTO.
     * @param room The chat room.
     * @param senderUsername The sender's username.
     * @return Saved Message entity.
     */
    @Transactional
    public Message saveRoomMessage(ChatMessageDTO messageDTO, ChatRoom room, String senderUsername) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setType(messageDTO.getType());
        message.setSender(sender);
        message.setRoom(room);
        message.setDeliveredAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Save a private/direct message.
     * 
     * @param messageDTO Message DTO.
     * @param senderUsername The sender's username.
     * @param recipientUsername The recipient's username.
     * @return Saved Message entity.
     */
    @Transactional
    public Message savePrivateMessage(ChatMessageDTO messageDTO, String senderUsername, String recipientUsername) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setType(Message.MessageType.DM);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setDeliveredAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Get messages for a chat room.
     * 
     * @param room The chat room.
     * @return List of messages.
     */
    public List<ChatMessageDTO> getRoomMessages(ChatRoom room) {
        return messageRepository.findByRoomOrderByTimestampAsc(room).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get private messages between two users.
     * 
     * @param username1 First user.
     * @param username2 Second user.
     * @return List of messages.
     */
    public List<ChatMessageDTO> getPrivateMessages(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> messages1 = messageRepository.findBySenderAndRecipientOrderByTimestampAsc(user1, user2);
        List<Message> messages2 = messageRepository.findBySenderAndRecipientOrderByTimestampAsc(user2, user1);
        
        return java.util.stream.Stream.concat(
                messages1.stream(),
                messages2.stream()
        ).sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    }

    /**
     * Get a message by ID.
     * 
     * @param messageId The message ID.
     * @return Optional Message entity.
     */
    public Optional<Message> getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * Mark a message as read.
     * 
     * @param messageId The message ID.
     * @param readerUsername The username of the reader.
     * @return ReadReceipt entity.
     */
    @Transactional
    public ReadReceipt markAsRead(Long messageId, String readerUsername) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        User reader = userRepository.findByUsername(readerUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if read receipt already exists
        if (readReceiptRepository.existsByMessageAndUser(message, reader)) {
            return readReceiptRepository.findByMessageAndUser(message, reader)
                    .orElseThrow(() -> new RuntimeException("Read receipt not found"));
        }

        ReadReceipt receipt = new ReadReceipt(message, reader);
        return readReceiptRepository.save(receipt);
    }

    /**
     * Convert Message entity to DTO.
     * 
     * @param message The Message entity.
     * @return ChatMessageDTO.
     */
    private ChatMessageDTO convertToDTO(Message message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setType(message.getType());
        dto.setContent(message.getContent());
        dto.setSender(message.getSender().getUsername());
        dto.setTimestamp(message.getTimestamp());
        dto.setDeliveredAt(message.getDeliveredAt());
        dto.setMessageId(message.getId());

        if (message.getRecipient() != null) {
            dto.setRecipient(message.getRecipient().getUsername());
        }
        if (message.getRoom() != null) {
            dto.setRoomName(message.getRoom().getName());
        }
        if (!message.getReadReceipts().isEmpty()) {
            // Get the earliest read receipt for this message
            message.getReadReceipts().stream()
                    .min((r1, r2) -> r1.getReadAt().compareTo(r2.getReadAt()))
                    .ifPresent(receipt -> dto.setReadAt(receipt.getReadAt()));
        }

        return dto;
    }
}

