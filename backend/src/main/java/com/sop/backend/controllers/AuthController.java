package com.sop.backend.controllers;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.dto.UserDTO;
import com.sop.backend.models.User;
import com.sop.backend.services.AuthService;
import com.sop.backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            User user = authService.registerUser(
                    userDTO.getName(),
                    userDTO.getEmail(),
                    userDTO.getPassword()
            );
            return ResponseEntity.ok(jwtUtil.generateToken(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (JOSEException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email ,
                                       @RequestParam String password ) {

        try {
            User user = authService.loginUser(email, password);
            return ResponseEntity.ok(jwtUtil.generateToken(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}