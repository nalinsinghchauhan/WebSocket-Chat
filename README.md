# WebSocket Chat Application

A real-time chat application built with Spring Boot, WebSockets (STOMP), JWT authentication, and MySQL database.

## Features

- 🔐 **JWT Authentication** - Secure user registration and login
- 💬 **Multiple Chat Rooms** - Create and join different chat rooms
- 📨 **Private Messaging** - Direct messages between users
- ✅ **Read Receipts** - Track when messages are read
- 💾 **Message Persistence** - All messages stored in MySQL database
- ⏰ **Timestamps** - Message timestamps and delivery tracking

## Tech Stack

- **Backend**: Spring Boot 3.2.5, Spring Security, Spring WebSocket (STOMP)
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Frontend**: HTML, JavaScript, Tailwind CSS, SockJS, STOMP.js

## Local Development

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (or use Docker)

### Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd websocket-chat
   ```

2. **Configure MySQL Database**
   
   Create a MySQL database:
   ```sql
   CREATE DATABASE websocket_chat;
   ```

   Or use Docker:
   ```bash
   docker run --name mysql-chat -e MYSQL_ROOT_PASSWORD=yourpassword -e MYSQL_DATABASE=websocket_chat -p 3306:3306 -d mysql:8.0
   ```

3. **Update Application Properties**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.password=yourpassword
   jwt.secret=your-secret-key-change-this
   ```

4. **Build and Run**
   ```bash
   mvn clean package
   java -jar target/websocket-chat-0.0.1-SNAPSHOT.jar
   ```

   Or use Maven directly:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the Application**
   
   Open your browser: http://localhost:8080

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### Rooms
- `GET /api/rooms` - Get all chat rooms
- `GET /api/rooms/{roomName}` - Get a specific room
- `POST /api/rooms` - Create a new room (requires authentication)

### WebSocket Endpoints

#### Client → Server:
- `/app/chat/send` - Send a message to a room
- `/app/chat/join` - Join a chat room
- `/app/chat/leave` - Leave a chat room
- `/app/dm/send` - Send a private message
- `/app/dm/read` - Mark a message as read

#### Server → Client:
- `/topic/room.{roomName}` - Receive room messages
- `/user/{username}/queue/messages` - Receive private messages
- `/user/{username}/queue/read-receipts` - Receive read receipts

## WebSocket Message Format

### Room Message
```json
{
  "type": "CHAT",
  "content": "Hello everyone!",
  "sender": "username",
  "roomName": "general",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Private Message
```json
{
  "type": "DM",
  "content": "Private message",
  "sender": "username1",
  "recipient": "username2",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Read Receipt
```json
{
  "type": "READ_RECEIPT",
  "messageId": 123,
  "sender": "reader",
  "readAt": "2024-01-15T10:31:00"
}
```

## Authentication

All API requests (except `/api/auth/register` and `/api/auth/login`) require JWT authentication:

```
Authorization: Bearer <your-jwt-token>
```

For WebSocket connections, include the token in the connection URL:
```
ws://localhost:8080/ws?token=<your-jwt-token>
```

## Database Schema

The application uses JPA/Hibernate with automatic schema generation. Tables:
- `users` - User accounts
- `chat_rooms` - Chat rooms
- `messages` - All messages (room and private)
- `read_receipts` - Read receipt tracking

## Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed deployment instructions to Render.com or other platforms.

## Project Structure

```
src/main/java/com/example/websocketchat/
├── config/          # Configuration classes
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── WebSocketHandshakeInterceptor.java
├── controller/      # REST and WebSocket controllers
│   ├── AuthController.java
│   ├── ChatWebSocketController.java
│   ├── PrivateMessageController.java
│   └── RoomController.java
├── dto/             # Data Transfer Objects
├── event/           # Event listeners
├── model/           # JPA entities
├── repository/      # Data access layer
├── security/        # Security components
├── service/          # Business logic
└── util/            # Utility classes
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is open source and available under the MIT License.

## Support

For issues and questions, please open an issue on GitHub.

