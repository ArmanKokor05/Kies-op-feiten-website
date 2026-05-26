package com.sop.backend.services;

import com.sop.backend.models.ForumPost;
import com.sop.backend.dto.ForumPostDTO;
import com.sop.backend.repositories.ForumRepository;
import org.springframework.stereotype.Service;

@Service
public class ForumService {

    private final ForumRepository forumRepository;

    public ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
    }

    public ForumPostDTO createPost(ForumPostDTO request) {
        ForumPost post = new ForumPost(request.getTitle(), request.getContent(), request.getAuthor());
        ForumPost savedPost = forumRepository.save(post);

        return new ForumPostDTO(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getAuthor(),
                savedPost.getCreatedAt()
        );
    }
}