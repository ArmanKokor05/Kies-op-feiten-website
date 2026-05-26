package com.sop.backend.services;

import com.sop.backend.dto.DiscussionDTO;
import com.sop.backend.exceptions.DiscussionException;
import com.sop.backend.mapper.DiscussionMapper;
import com.sop.backend.models.Discussion;
import com.sop.backend.models.User;
import com.sop.backend.repositories.DiscussionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final DiscussionMapper discussionMapper;
    private final UserService userService;

    public DiscussionService(DiscussionRepository discussionRepository,
                             DiscussionMapper discussionMapper,
                             UserService userService) {
        this.discussionRepository = discussionRepository;
        this.discussionMapper = discussionMapper;
        this.userService = userService;
    }

    public DiscussionDTO createDiscussion(DiscussionDTO discussionDTO, Long userId) {
        String trimmedTitle = discussionDTO.title.trim();
        String trimmedContent = discussionDTO.content.trim();
        User user = userService.findById(userId);

        if (trimmedTitle.isEmpty()) {
            throw new DiscussionException("Title is empty");
        }

        if (trimmedContent.isEmpty()) {
            throw new DiscussionException("Content is empty");
        }

        Discussion discussion = new Discussion();
        discussion.setTitle(discussionDTO.title.trim());
        discussion.setContent(discussionDTO.content.trim());
        discussion.setUser(user);

        discussion = this.discussionRepository.save(discussion);

        discussionDTO.setId(discussion.getId());
        discussionDTO.setCreatedAt(discussion.getCreatedAt());

        return discussionDTO;
    }

    /**
     * Retrieves paginated discussions created by a specific user.
     *
     * @param userId the unique identifier of the user
     * @param pageable the pagination information (page number, size, sort)
     * @return a page of discussion DTOs created by the user
     *
     * @see DiscussionRepository#findByUserId(Long, Pageable)
     */
    public Page<DiscussionDTO> getDiscussionsByUser(Long userId, Pageable pageable) {
        return discussionRepository.findByUserId(userId, pageable)
                .map(discussionMapper::toDTO);
    }

    public DiscussionDTO getDiscussionById(Long id) {
        return discussionRepository.findById(id)
                .map(discussionMapper::toDTO)
                .orElseThrow(() -> new DiscussionException("Discussion not found"));
    }

    public Page<DiscussionDTO> getAllDiscussions(Pageable pageable) {
        return discussionRepository.findAll(pageable)
                .map(discussionMapper::toDTO);
    }

    /**
     * Searches for discussions by title with pagination support.
     * Performs a case-insensitive partial match search on discussion titles.
     *
     * @param title the search term to match against discussion titles (trimmed automatically)
     * @param pageable the pagination information (page number, size, sort)
     * @return a page of discussion DTOs matching the search criteria
     *
     * @see DiscussionRepository#findByTitleContainingIgnoreCase(String, Pageable)
     */
    public Page<DiscussionDTO> searchDiscussions(String title, Pageable pageable) {
        return discussionRepository.findByTitleContainingIgnoreCase(title.trim(), pageable)
                .map(discussionMapper::toDTO);
    }
}
