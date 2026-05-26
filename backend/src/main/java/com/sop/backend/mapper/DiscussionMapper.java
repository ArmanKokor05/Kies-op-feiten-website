package com.sop.backend.mapper;

import com.sop.backend.dto.DiscussionDTO;
import com.sop.backend.models.Discussion;
import org.springframework.stereotype.Component;

@Component
public class DiscussionMapper {

    /**
     * Converts a Discussion entity to a DiscussionDTO.
     * Maps all discussion fields including ID, title, content, vote counts,
     * and creation timestamp. Also includes the username if a user is
     * associated with the discussion.
     *
     * @param discussion the Discussion entity to convert
     * @return a DiscussionDTO containing the discussion data
     *
     */
    public DiscussionDTO toDTO(Discussion discussion) {
        DiscussionDTO dto = new DiscussionDTO();
        dto.setId(discussion.getId());
        dto.setTitle(discussion.getTitle());
        dto.setContent(discussion.getContent());
        dto.setUpvotes(discussion.getUpvotes());
        dto.setDownvotes(discussion.getDownvotes());
        dto.setCreatedAt(discussion.getCreatedAt());

        if (discussion.getUser() != null) {
            dto.setUserName(discussion.getUser().getName());
        }

        return dto;
    }
}