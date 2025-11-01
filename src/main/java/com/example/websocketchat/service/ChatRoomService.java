package com.example.websocketchat.service;

import com.example.websocketchat.dto.ChatRoomDTO;
import com.example.websocketchat.model.ChatRoom;
import com.example.websocketchat.model.User;
import com.example.websocketchat.repository.ChatRoomRepository;
import com.example.websocketchat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for chat room operations.
 */
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new chat room.
     * 
     * @param roomDTO Room information.
     * @param creatorUsername Username of the room creator.
     * @return Created ChatRoomDTO.
     * @throws RuntimeException if room name already exists.
     */
    @Transactional
    public ChatRoomDTO createRoom(ChatRoomDTO roomDTO, String creatorUsername) {
        if (chatRoomRepository.existsByName(roomDTO.getName())) {
            throw new RuntimeException("Room name already exists");
        }

        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatRoom room = new ChatRoom(roomDTO.getName(), roomDTO.getDescription(), creator);
        room = chatRoomRepository.save(room);

        return convertToDTO(room);
    }

    /**
     * Get all chat rooms.
     * 
     * @return List of all chat rooms.
     */
    public List<ChatRoomDTO> getAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a chat room by name.
     * 
     * @param roomName The name of the room.
     * @return ChatRoomDTO if found.
     * @throws RuntimeException if room not found.
     */
    public ChatRoomDTO getRoomByName(String roomName) {
        ChatRoom room = chatRoomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return convertToDTO(room);
    }

    /**
     * Get a ChatRoom entity by name.
     * 
     * @param roomName The name of the room.
     * @return ChatRoom entity if found.
     * @throws RuntimeException if room not found.
     */
    public ChatRoom getRoomEntityByName(String roomName) {
        return chatRoomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    /**
     * Convert ChatRoom entity to DTO.
     * 
     * @param room The ChatRoom entity.
     * @return ChatRoomDTO.
     */
    private ChatRoomDTO convertToDTO(ChatRoom room) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setCreatedAt(room.getCreatedAt());
        if (room.getCreatedBy() != null) {
            dto.setCreatedBy(room.getCreatedBy().getUsername());
        }
        return dto;
    }
}

