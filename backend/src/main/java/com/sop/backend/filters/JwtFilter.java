package com.sop.backend.filters;

import com.sop.backend.handlers.JwtAuthenticationEntryPoint;
import com.sop.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final Environment environment;

    public JwtFilter(JwtUtil jwtUtil, JwtAuthenticationEntryPoint authEntryPoint, Environment environment) {
        this.jwtUtil = jwtUtil;
        this.authEntryPoint = authEntryPoint;
        this.environment = environment;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();


        return path.startsWith("/auth/")
                || path.startsWith("/api/xml")
                || path.startsWith("/api/party-results")
                || path.startsWith("/api/admin")
                || path.equals("/api/stemwijzer/calculate")
                || path.equals("/api/stemwijzer/parties")
                || path.startsWith("/questions")
                || path.startsWith("/candidates")
                || path.startsWith("/api/discussions/AllDiscussions")
                || path.startsWith("/api/discussions/search")
                || path.startsWith("/api/election")
                || path.equals("/api/pollingstation/election")
                || path.equals("/api/pollingstation/municipality")
                || path.startsWith("/ws")
                || path.startsWith("/ws-notifications")
                || path.equals("/api/municipality")
                || path.equals("/api/party")
                || path.startsWith("/ws/")
                || path.startsWith("/api/chats/discussion")
                || path.startsWith("/api/discussions/discussions")

                // Swagger/OpenAPI endpoints
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")
                || path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");

        try {
            String userId = jwtUtil.validateToken(authHeader);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            AuthenticationException authEx =
                    new BadCredentialsException("Invalid authentication: " + ex.getMessage(), ex);
            authEntryPoint.commence(request, response, authEx);
            return;
        }

        filterChain.doFilter(request, response);
    }
}