package com.sop.backend.filters;

import com.sop.backend.handlers.JwtAuthenticationEntryPoint;
import com.sop.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;
    private FilterChain filterChain;
    private Environment environment;

    @BeforeEach
    void setUp() {
        JwtAuthenticationEntryPoint authEntryPoint = mock(JwtAuthenticationEntryPoint.class);
        jwtUtil = mock(JwtUtil.class);
        filterChain = mock(FilterChain.class);
        environment = mock(Environment.class);

        when(environment.getActiveProfiles()).thenReturn(new String[]{});

        jwtFilter = new JwtFilter(jwtUtil, authEntryPoint, environment);
    }

    @Test
    void testDoFilterInternal_withValidToken_callsFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "valid.jwt.token";

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtil.validateToken("Bearer " + token)).thenReturn("user");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(jwtUtil).validateToken("Bearer " + token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_withInvalidToken_usesAuthenticationEntryPoint() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "invalid.jwt.token";

        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.validateToken("Bearer " + token)).thenThrow(new RuntimeException("Invalid token"));

        JwtAuthenticationEntryPoint entryPoint = mock(JwtAuthenticationEntryPoint.class);
        jwtFilter = new JwtFilter(jwtUtil, entryPoint, environment);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(entryPoint).commence(eq(request), eq(response), any());
    }
}