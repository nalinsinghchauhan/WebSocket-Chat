package com.example.websocketchat.controller;

import com.example.websocketchat.dto.ChatRoomDTO;
import com.example.websocketchat.service.ChatRoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for chat room operations.
 * 
 * Endpoints:
 * - GET /api/rooms - Get all chat rooms
 * - GET /api/rooms/{roomName} - Get a specific room by name
 * - POST /api/rooms - Create a new chat room
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final ChatRoomService chatRoomService;

    public RoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    /**
     * Get all chat rooms.
     * 
     * @return List of all chat rooms.
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllRooms() {
        List<ChatRoomDTO> rooms = chatRoomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Get a specific room by name.
     * 
     * @param roomName The name of the room.
     * @return ChatRoomDTO if found.
     */
    @GetMapping("/{roomName}")
    public ResponseEntity<?> getRoom(@PathVariable String roomName) {
        try {
            ChatRoomDTO room = chatRoomService.getRoomByName(roomName);
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Room not found: " + e.getMessage());
        }
    }

    /**
     * Create a new chat room.
     * Requires authentication.
     * 
     * @param roomDTO Room information.
     * @param authentication Current user authentication.
     * @return Created ChatRoomDTO.
     */
    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody ChatRoomDTO roomDTO,
                                         Authentication authentication) {
        try {
            String username = authentication.getName();
            ChatRoomDTO createdRoom = chatRoomService.createRoom(roomDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating room: " + e.getMessage());
        }
    }
}

