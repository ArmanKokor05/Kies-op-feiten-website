package com.sop.backend.config;

import com.sop.backend.util.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Controller
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final JwtHandshakeInterceptor2 jwtHandshakeInterceptor2;
    private final UserInterceptor userInterceptor;

    public WebSocketConfig(JwtUtil jwtUtil, JwtHandshakeInterceptor2 jwtHandshakeInterceptor2, UserInterceptor userInterceptor) {
        this.jwtUtil = jwtUtil;
        this.jwtHandshakeInterceptor2 = jwtHandshakeInterceptor2;
        this.userInterceptor = userInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
//                    .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
                .withSockJS();

        registry
                .addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("http://localhost:5173",
                        "https://kiesopfeiten.com",
                        "https://www.kiesopfeiten.com",
                        "https://api.kiesopfeiten.com")
                .addInterceptors(jwtHandshakeInterceptor2)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(userInterceptor);
    }
}
