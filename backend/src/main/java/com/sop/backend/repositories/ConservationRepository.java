package com.sop.backend.repositories;

import com.sop.backend.models.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConservationRepository extends JpaRepository<Conversation, Long> {
}
