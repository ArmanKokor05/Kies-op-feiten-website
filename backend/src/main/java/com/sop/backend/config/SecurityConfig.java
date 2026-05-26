package com.sop.backend.config;

import com.sop.backend.filters.JwtFilter;
import com.sop.backend.handlers.JwtAuthenticationEntryPoint;
import com.sop.backend.handlers.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtFilter jwtFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtFilter = jwtFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/xml/**").permitAll()
                        .requestMatchers("/api/party-results/**").permitAll()
                        .requestMatchers("/questions/**").permitAll()
                        .requestMatchers("/candidates/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/stemwijzer/calculate").permitAll()
                        .requestMatchers("/api/stemwijzer/parties").permitAll()
                        .requestMatchers("/api/election").permitAll()
                        .requestMatchers("/api/discussions/AllDiscussions/**").permitAll()
                        .requestMatchers("/api/discussions/search/**").permitAll()
                        .requestMatchers("/api/discussions/search").permitAll()
                        .requestMatchers("/api/pollingstation/election").permitAll()
                        .requestMatchers("/api/pollingstation/municipality").permitAll()
                        .requestMatchers("/api/municipality").permitAll()
                        .requestMatchers("/api/party/**").permitAll()
                        .requestMatchers("/ws/*").permitAll()
                        .requestMatchers("/api/stemwijzer/save").authenticated()
                        .requestMatchers("/api/stemwijzer/my-results").authenticated()
                        .requestMatchers("/api/stemwijzer/result/**").authenticated()
                        .requestMatchers("/api/pollingstation/xml").authenticated()
                        .requestMatchers("/api/chats/discussion/**").permitAll()
                        .requestMatchers("/api/discussions/discussions/**").permitAll()
                        .requestMatchers("/api/**").authenticated()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/favicon.ico/**").permitAll()

                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://192.168.178.10:5173",
                "http://77.250.123.23:5173",
                "https://kiesopfeiten.com",
                "https://api.kiesopfeiten.com",
                "http://159.223.237.65:5173"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}