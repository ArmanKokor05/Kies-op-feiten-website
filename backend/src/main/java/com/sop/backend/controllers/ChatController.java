package com.sop.backend.controllers;

import com.sop.backend.dto.ChatDTO;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/chats")
@Validated
@Tag(name = "Chat", description = "Chat management APIs")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Create a new chat/reply",
            description = "Creates a new chat/reply for a specific discussion"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Chat created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid chat data",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ChatDTO>> createChat(@RequestBody ChatDTO chatDTO,
                                                           Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        ChatDTO response = chatService.createChat(chatDTO, userId);
        return ResponseEntity.ok(new ApiResponse<>("Chat created successfully", response));
    }

    @Operation(
            summary = "Get chats from a specific user",
            description = "Retrieve paginated chats/replies for a specific user. " +
                    "Chats are sorted by creation date (newest first)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Chats retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters (e.g. negative userId)",
                    content = @Content
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ChatDTO>> getChatsByUser(
            @Parameter(description = "ID from the user", required = true, example = "1")
            @PathVariable
            @Positive(message = "User ID must be positive")
            Long userId,

            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Amount of items per page", example = "20")
            @RequestParam(defaultValue = "20")
            int size) {

        logger.info("Fetching chats for user: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatDTO> chats = chatService.getChatsByUser(userId, pageable);

        logger.debug("Found {} total chats for user {}", chats.getTotalElements(), userId);

        return ResponseEntity.ok(chats);
    }

    @Operation(
            summary = "Get chats for a specific discussion",
            description = "Retrieve paginated chats/replies for a specific discussion. " +
                    "Chats are sorted by creation date (newest first)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Chats retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content
            )
    })
    @GetMapping("/discussion/{discussionId}")
    public ResponseEntity<Page<ChatDTO>> getChatsByDiscussion(
            @Parameter(description = "ID of the discussion", required = true, example = "1")
            @PathVariable
            @Positive(message = "Discussion ID must be positive")
            Long discussionId,

            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Amount of items per page", example = "20")
            @RequestParam(defaultValue = "20")
            int size) {

        logger.info("Fetching chats for discussion: {}, page: {}, size: {}", discussionId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatDTO> chats = chatService.getChatsByDiscussion(discussionId, pageable);

        logger.debug("Found {} total chats for discussion {}", chats.getTotalElements(), discussionId);

        return ResponseEntity.ok(chats);
    }
}