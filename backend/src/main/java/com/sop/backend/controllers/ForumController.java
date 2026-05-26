package com.sop.backend.controllers;

import com.sop.backend.dto.ForumPostDTO;
import com.sop.backend.services.ForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @PostMapping
    public ResponseEntity<ForumPostDTO> createPost(@RequestBody ForumPostDTO request) {
        ForumPostDTO response = forumService.createPost(request);
        return ResponseEntity.ok(response);
    }
}