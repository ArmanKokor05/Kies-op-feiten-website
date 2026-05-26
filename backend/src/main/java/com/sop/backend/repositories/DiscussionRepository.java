package com.sop.backend.repositories;

import com.sop.backend.models.Discussion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    /**
     * Finds all discussions created by a specific user with pagination.
     * Uses EntityGraph to eagerly fetch the associated user entity,
     * preventing N+1 query problems when accessing user information.
     *
     * @param userId the unique identifier of the user
     * @param pageable the pagination information
     * @return a page of discussions created by the specified user
     *
     */
    @EntityGraph(attributePaths = {"user"})
    Page<Discussion> findByUserId(Long userId, Pageable pageable);

    /**
     * Searches discussions by title with case-insensitive partial matching.
     * Uses EntityGraph to eagerly fetch the associated user entity,
     * preventing N+1 query problems when accessing user information.
     *
     * @param title the search term to match against discussion titles
     * @param pageable the pagination information
     * @return a page of discussions with matching titles
     *
     */
    @EntityGraph(attributePaths = {"user"})
    Page<Discussion> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    void deleteByUserId(Long userId);
}
