package com.sop.backend.repositories;

import com.sop.backend.models.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Finds all chats/responses created by a specific user with pagination.
     * Uses EntityGraph to eagerly fetch the associated discussion entity,
     * preventing N+1 query problems when accessing discussion information.
     *
     * @param userId the unique identifier of the user
     * @param pageable the pagination information
     * @return a page of chats/responses created by the specified user
     *
     */
    @EntityGraph(attributePaths = {"discussion"})
    Page<Chat> findByUserId(Long userId, Pageable pageable);


    @EntityGraph(attributePaths = {"user"})
    Page<Chat> findByDiscussionId(Long discussionId, Pageable pageable);

    void deleteByUserId(Long userId);
}