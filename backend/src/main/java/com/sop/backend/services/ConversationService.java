package com.sop.backend.services;

import com.sop.backend.dto.ConversationDTO;
import com.sop.backend.dto.ConversationDisplayDTO;
import com.sop.backend.dto.MessageDto;
import com.sop.backend.mapper.ConversationDisplayDtoMapper;
import com.sop.backend.mapper.ConversationDtoMapper;
import com.sop.backend.models.Conversation;
import com.sop.backend.models.ConversationUser;
import com.sop.backend.models.User;
import com.sop.backend.repositories.ConservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ConversationService implements ServiceInterface<Conversation> {

    private final ConversationUserService conversationUserService;
    private final ConservationRepository repository;
    private final MessageService messageService;
    private final ConversationDisplayDtoMapper conversationDisplayDtoMapper;
    private final ConversationDtoMapper conversationDtoMapper;

    public ConversationService(ConversationUserService conversationUserService,
                               ConservationRepository repository,
                               MessageService messageService,
                               ConversationDisplayDtoMapper conversationDisplayDtoMapper,
                               ConversationDtoMapper conversationDtoMapper) {
        this.conversationUserService = conversationUserService;
        this.repository = repository;
        this.messageService = messageService;
        this.conversationDisplayDtoMapper = conversationDisplayDtoMapper;
        this.conversationDtoMapper = conversationDtoMapper;
    }

    @Transactional
    public Conversation createDirectMessage(User sender, User receiver) {
        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("Sender and Receiver are the same");
        }

        List<ConversationUser> senderConversations = conversationUserService.getConversationsByUserId(sender.getId());

        for (ConversationUser senderCu : senderConversations) {
            List<ConversationUser> receiverConversations =
                    conversationUserService.findByUserIdAndConversation(receiver.getId(), senderCu.getConversation());

            if (!receiverConversations.isEmpty()) {
                return receiverConversations.get(0).getConversation();
            }
        }

        Conversation conversation = new Conversation();
        repository.save(conversation);

        conversationUserService.create(sender, conversation);
        conversationUserService.create(receiver, conversation);

        return conversation;
    }

    public List<ConversationDisplayDTO> getAllConversationsByUserId(Long userId) {
        List<ConversationUser> userConversations = conversationUserService.getReceiverConversationsBySenderId(userId);
        List<ConversationDisplayDTO> conversationDTOs = new ArrayList<>();

        for (ConversationUser conversation : userConversations) {
            MessageDto message = messageService
                    .getLatestMessageByConversationId(conversation.getConversation().getId())
                    .orElse(null);

            ConversationDisplayDTO dto = conversationDisplayDtoMapper.toDto(conversation, message);
            conversationDTOs.add(dto);
        }

        conversationDTOs.sort(
                Comparator.comparing(ConversationDisplayDTO::updatedAt).reversed()
        );

        return conversationDTOs;
    }

    @Transactional
    public ConversationDTO getConversationById(long conversationId, long receiverId) {
        List<ConversationUser> conversation = conversationUserService
                .findByUserIdNotAndConversationId(receiverId, conversationId);

        if (conversation.isEmpty()) {
            throw new IllegalArgumentException("Cannot enter conversation that is not yours");
        }

        conversation.get(0).setLastReadAt(LocalDateTime.now());

        User user = conversation.get(0).getUser();

        return conversationDtoMapper.toDto(conversationId, user);
    }

    @Override
    public Conversation save(Conversation conversation) {
        return repository.save(conversation);
    }
}
