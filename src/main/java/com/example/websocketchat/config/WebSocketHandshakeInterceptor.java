package com.example.websocketchat.config;

import com.example.websocketchat.util.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * Custom WebSocket handshake interceptor to validate JWT token during WebSocket connection.
 * Token can be passed as a query parameter: ws://localhost:8080/ws?token=...
 */
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                  WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        // Extract token from query parameters
        URI uri = request.getURI();
        String query = uri.getQuery();
        
        if (query != null && query.contains("token=")) {
            String token = query.substring(query.indexOf("token=") + 6);
            // Handle multiple query parameters
            if (token.contains("&")) {
                token = token.substring(0, token.indexOf("&"));
            }
            
            // Validate token
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                attributes.put("username", username);
                attributes.put("token", token);
                return true;
            }
        }
        
        // Also check Authorization header if present
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                attributes.put("username", username);
                attributes.put("token", token);
                return true;
            }
        }
        
        // If no valid token, reject the handshake
        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No action needed after handshake
    }
}

