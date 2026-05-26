package com.sop.backend.config;

import com.sop.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Intercepts WebSocket handshake requests to extract and validate JWT.
 * Stores the userId in session attributes for WebSocket use.
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {

        // Ensure it's an HTTP servlet request
        if (!(request instanceof ServletServerHttpRequest)) {
            return true;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        // Read Authorization header
        String authHeader = servletRequest.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            return false; // reject handshake if no token
        }

        String userId;
        try {
            userId = jwtUtil.validateToken(authHeader); // returns subject (user id)
        } catch (Exception e) {
            return false; // reject handshake if invalid
        }

        attributes.put("userId", userId);

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {

    }
}
