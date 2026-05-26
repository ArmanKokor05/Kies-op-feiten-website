package com.sop.backend.controllers;

import com.nimbusds.jose.JOSEException;
import com.sop.backend.dto.EditUserDTO;
import com.sop.backend.dto.SearchUserDTO;
import com.sop.backend.dto.UserDTO;
import com.sop.backend.dto.UserInfoDTO;
import com.sop.backend.mapper.SearchUserDtoMapper;
import com.sop.backend.models.User;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.UserService;
import com.sop.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SearchUserDtoMapper dtoMapper;

    public UserController(UserService userService, JwtUtil jwtUtil, SearchUserDtoMapper dtoMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.dtoMapper = dtoMapper;
    }

    @Operation(
            summary = "Change username",
            description = "Updates the user's username and generates a new JWT token. " +
                    "The old token becomes invalid after this operation."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Username updated successfully, new JWT token returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID or username data",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PutMapping("/{userId}/username")
    public ResponseEntity<ApiResponse<String>> changeName(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable
            @Positive(message = "User ID must be positive")
            Long userId,

            @Parameter(description = "New username data", required = true)
            @Valid
            @RequestBody
            EditUserDTO editUserDTO) throws JOSEException {

        logger.info("Request to change username for user {}", userId);

        User user = userService.changeName(userId, editUserDTO.getName());
        logger.debug("Username changed successfully for user {}", userId);

        String token = jwtUtil.generateToken(user);
        logger.info("Generated new JWT token for user {}", userId);

        return ResponseEntity.ok(
                new ApiResponse<>("Username updated successfully", token)
        );
    }

    @Operation(
            summary = "Delete user account",
            description = "Permanently deletes a user account and all associated data from the database"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Account deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable
            @Positive(message = "User ID must be positive")
            Long userId) {

        logger.info("Request to delete account for user {}", userId);

        userService.deleteAccount(userId);
        logger.info("Account successfully deleted for user {}", userId);

        return ResponseEntity.ok(
                new ApiResponse<>("Account deleted successfully", null)
        );
    }

    @Operation(
            summary = "Get user information",
            description = "Retrieves basic user information including username and account creation date"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User information retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDTO> getUserInfo(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable
            @Positive(message = "User ID must be positive")
            Long userId) {

        logger.info("Request to get user info for user {}", userId);

        UserInfoDTO userInfo = userService.getUserInfo(userId);

        logger.debug("User info retrieved for user {}", userId);

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchUserDTO>>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsers(name);

        return ResponseEntity.ok(
                new ApiResponse<>("Users found", dtoMapper.toList(users))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Long>> getUserId (Authentication authentication) {
        return ResponseEntity.ok(
                new ApiResponse<>("User found", Long.valueOf(authentication.getName()))
        );
    }

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<SearchUserDTO>> findById (long id) {
        return ResponseEntity.ok(
                new ApiResponse<>("User found", dtoMapper.toDto(userService.findById(id)))
        );
    }
}
