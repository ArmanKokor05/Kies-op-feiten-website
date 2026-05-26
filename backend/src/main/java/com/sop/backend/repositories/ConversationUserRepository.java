package com.sop.backend.repositories;

import com.sop.backend.models.Conversation;
import com.sop.backend.models.ConversationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationUserRepository extends JpaRepository<ConversationUser, Long> {
    List<ConversationUser> findByUserId(Long userId);
    List<ConversationUser> findByUserIdNotAndConversationId(Long userId, Long conversationId);
    List<ConversationUser> findByUserIdAndConversation(Long userId, Conversation conversation);
    List<ConversationUser> findByConversationAndUserIdNot(Conversation conversation, Long userId);
}
