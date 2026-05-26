package com.sop.backend.repositories;

import com.sop.backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(Long conversationId);
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
