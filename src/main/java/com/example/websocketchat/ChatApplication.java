package com.example.websocketchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class.
 * 
 * This is a WebSocket-based chat application with:
 * - JWT authentication
 * - Multiple chat rooms
 * - Private messaging
 * - Read receipts
 * - Message persistence with MySQL
 */
@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
