package com.sop.backend.repositories;

import com.sop.backend.models.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends JpaRepository<ForumPost, Long> {
    // Geen custom methods - alleen standaard JPA functionaliteit
    // Gebruik gewoon: findAll(), findById(), save(), delete(), etc.
}