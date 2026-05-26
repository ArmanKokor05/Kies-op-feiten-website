package com.sop.backend.controllers;

import com.sop.backend.dto.DiscussionDTO;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/discussions")
@Validated
@Tag(name = "Discussion", description = "Discussion management APIs")
public class DiscussionController {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionController.class);
    private final DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) { this.discussionService = discussionService; }

    @Operation(
            summary = "Create a new discussion",
            description = "Creates a new discussion with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Discussion created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid discussion data",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DiscussionDTO>> createDiscussion(@RequestBody DiscussionDTO discussionDTO,
                                                                       Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        DiscussionDTO response = discussionService.createDiscussion(discussionDTO, userId);
        return ResponseEntity.ok(new ApiResponse<>("Discussion created successfully", response));
    }

    @Operation(
            summary = "Get discussions from a specific user",
            description = "Retrieve paginated discussions for a specific user. " +
                    "Discussions are sorted by creation date (newest first)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Discussions retrieved successfully",
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
    public ResponseEntity<Page<DiscussionDTO>> getDiscussionsByUser(
            @Parameter(description = "ID of the user", required = true, example = "1")
            @PathVariable
            @Positive(message = "User ID must be positive")
            Long userId,

            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Amount of items per page", example = "20")
            @RequestParam(defaultValue = "20")
            int size) {

        logger.info("Fetching discussions for user: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DiscussionDTO> discussions = discussionService.getDiscussionsByUser(userId, pageable);

        logger.debug("Found {} total discussions for user {}", discussions.getTotalElements(), userId);

        return ResponseEntity.ok(discussions);
    }

    @Operation(
            summary = "Get all discussions",
            description = "Retrieve paginated discussions sorted by creation date (newest first)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Discussions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping("/AllDiscussions")
    public ResponseEntity<Page<DiscussionDTO>> getAllDiscussions(
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Amount of items per page", example = "20")
            @RequestParam(defaultValue = "20")
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DiscussionDTO> discussions = discussionService.getAllDiscussions(pageable);

        return ResponseEntity.ok(discussions);
    }

    @Operation(
            summary = "Search discussions by title",
            description = "Search discussions using case-insensitive partial matching on titles. " +
                    "Results are sorted by creation date (newest first) with pagination support."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping("/search")
    public ResponseEntity<Page<DiscussionDTO>> searchDiscussions(
            @Parameter(description = "Search term to match against discussion titles", example = "Java")
            @RequestParam(required = false, defaultValue = "")
            String title,

            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Amount of items per page", example = "20")
            @RequestParam(defaultValue = "20")
            int size) {

        logger.info("Searching discussions with title containing: '{}', page: {}, size: {}", title, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DiscussionDTO> discussions = discussionService.searchDiscussions(title, pageable);

        logger.debug("Found {} total discussions matching '{}'", discussions.getTotalElements(), title);

        return ResponseEntity.ok(discussions);
    }

    @GetMapping("/discussions/{id}")
    public ResponseEntity<ApiResponse<DiscussionDTO>> getDiscussionById(@PathVariable Long id) {
        DiscussionDTO response = discussionService.getDiscussionById(id);
        return ResponseEntity.ok(new ApiResponse<>("Discussion found", response));
    }
}
