package com.sop.backend.services;

import com.sop.backend.models.Conversation;
import com.sop.backend.models.ConversationUser;
import com.sop.backend.models.User;
import com.sop.backend.repositories.ConversationUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationUserService implements ServiceInterface<ConversationUser> {

    private final ConversationUserRepository repository;

    public ConversationUserService(ConversationUserRepository repository) {
        this.repository = repository;
    }

    public List<ConversationUser> findByUserIdNotAndConversationId(Long userId, Long conversationId) {
        return repository.findByUserIdNotAndConversationId(userId, conversationId);
    }

    public List<ConversationUser> findByUserIdAndConversation(Long userId, Conversation conversation) {
        return repository.findByUserIdAndConversation(userId, conversation);
    }

    public List<ConversationUser> getConversationsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    /**
     * The conversations that are retrieved are the conversation of the receiver side.
     * This is because for every conversation there are 2 rows added in the @Link{ConversationUser} table.
     * To display a conversation you want the display name of the conversation to be the name of receiving user.
     * This way you retrieve all the conversations of a user, but the name you can retrieve of the conversation is the name of the receiver.
     *
     * @param senderId The id of the logged-in user
     * @return The conversation of the receiver side
     */
    public List<ConversationUser> getReceiverConversationsBySenderId(Long senderId) {
        List<ConversationUser> userConversations = getConversationsByUserId(senderId);
        List<ConversationUser> receiverConversations = new ArrayList<>();

        for (ConversationUser conversationUser : userConversations) {
            receiverConversations.add(
                    repository.findByConversationAndUserIdNot(
                            conversationUser.getConversation(), senderId).get(0));
        }

        return receiverConversations;
    }

    public ConversationUser create(User user, Conversation conversation) {
        ConversationUser conversationUser = new ConversationUser();
        conversationUser.setUser(user);
        conversationUser.setConversation(conversation);
        return repository.save(conversationUser);
    }

    @Override
    public ConversationUser save(ConversationUser conversationUser) {
        return repository.save(conversationUser);
    }
}
