package com.sop.backend.services;

import com.sop.backend.dto.MessageDto;
import com.sop.backend.exceptions.DirectMessageException;
import com.sop.backend.mapper.MessageDtoMapper;
import com.sop.backend.models.Conversation;
import com.sop.backend.models.Message;
import com.sop.backend.models.User;
import com.sop.backend.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService implements ServiceInterface<Message> {

    private final MessageRepository repository;
    private final MessageDtoMapper dtoMapper;

    public MessageService(MessageRepository repository, MessageDtoMapper dtoMapper) {
        this.repository = repository;
        this.dtoMapper = dtoMapper;
    }

        public Message create(User sender, User receiver, Conversation conversation, String content) {
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setConversation(conversation);
            message.setContent(content.trim());

            return save(message);
        }

    public Optional<MessageDto> getLatestMessageByConversationId(Long conversationId) {
        return repository
                .findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
                .map(dtoMapper::toDto);
    }

    public List<MessageDto> getMessagesByConversationId(Long userId, Long conversationId) {
        List<Message> messages = repository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        if (!messages.isEmpty()) {
            checkIfMessageBelongsToUser(messages.get(0), userId);
        }

        return dtoMapper.toDtoList(messages);
    }

    private void checkIfMessageBelongsToUser(Message message, Long userId) {
        if (!message.getSender().getId().equals(userId) && !message.getReceiver().getId().equals(userId)) {
            throw new DirectMessageException("This is not your conversation to enter");
        }
    }

    @Override
    public Message save(Message message) {
        return repository.save(message);
    }
}
