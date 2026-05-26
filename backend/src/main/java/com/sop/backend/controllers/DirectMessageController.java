package com.sop.backend.controllers;

import com.sop.backend.dto.ConversationDTO;
import com.sop.backend.dto.ConversationDisplayDTO;
import com.sop.backend.dto.MessageDto;
import com.sop.backend.mapper.MessageDtoMapper;
import com.sop.backend.models.Conversation;
import com.sop.backend.models.Message;
import com.sop.backend.models.User;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.ConversationService;
import com.sop.backend.services.ConversationUserService;
import com.sop.backend.services.MessageService;
import com.sop.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directmessage")
public class DirectMessageController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;


    public DirectMessageController(ConversationUserService conversationUserService,
                                   ConversationService conversationService,
                                   MessageService messageService,
                                   UserService userService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageDto>> createDirectMessage(Authentication authentication,
                                                                       @RequestParam Long receiverId,
                                                                       @RequestParam String content) {
        Long senderId = Long.valueOf(authentication.getName());

        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);
        Conversation conversation = conversationService.createDirectMessage(sender, receiver);
        Message message = messageService.create(sender, receiver, conversation, content);

        MessageDtoMapper mapper = new MessageDtoMapper();

        MessageDto messageDto = mapper.toDto(message);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversation.getId(),
                messageDto
        );

        return ResponseEntity.ok(
                new ApiResponse<>("Message sent successfully", messageDto)
        );
    }

    @GetMapping("/all/conversations")
    public ResponseEntity<ApiResponse<List<ConversationDisplayDTO>>> getConversations(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                new ApiResponse<>("Conversations retrieved successfully",
                        conversationService.getAllConversationsByUserId(userId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ConversationDTO>> getConversationId(@RequestParam long conversationId,
                                                                          Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());

        return ResponseEntity.ok(
                new ApiResponse<>("Conversation retrieved successfully",
                        conversationService.getConversationById(conversationId, userId))
        );
    }

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessages(@RequestParam Long conversationId,
                                                                     Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                new ApiResponse<>("Messages retrieved successfully",
                        messageService.getMessagesByConversationId(userId, conversationId))
        );
    }
}
