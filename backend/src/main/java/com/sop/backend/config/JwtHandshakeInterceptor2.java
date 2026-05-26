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
 * Intercepts WebSocket handshake requests to extract and validate JWT from query parameters.
 * Stores the userId in session attributes for user-specific WebSocket routing.
 */
@Component
public class JwtHandshakeInterceptor2 implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor2(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {

        System.out.println("=== WebSocket Handshake Started ===");

        if (!(request instanceof ServletServerHttpRequest)) {
            System.out.println("Not a servlet request, allowing handshake");
            return true;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        String token = servletRequest.getParameter("token");

        System.out.println("Token from query: " + (token != null ? "present" : "null"));

        if (token == null || token.isEmpty()) {
            System.out.println("No token provided, rejecting handshake");
            return false;
        }

        String userId;
        try {
            userId = jwtUtil.validateToken("Bearer " + token);
            System.out.println("Token validated successfully for user: " + userId);
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }

        attributes.put("userId", userId);
        System.out.println("UserId stored in session attributes: " + userId);

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        System.out.println("=== WebSocket Handshake Completed ===");
    }
}