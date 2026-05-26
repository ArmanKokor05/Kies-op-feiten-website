package com.sop.backend.mapper;

import com.sop.backend.dto.MessageDto;
import com.sop.backend.models.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageDtoMapper {

    public List<MessageDto> toDtoList(List<Message> messages) {
        final List<MessageDto> dtoList = new ArrayList<>();

        for (Message message : messages) {
            dtoList.add(toDto(message));
        }

        return dtoList;
    }

    public MessageDto toDto(Message message) {
        return new MessageDto(message.getId(),
                              message.getConversation().getId(),
                              message.getSender().getId(),
                              message.getReceiver().getId(),
                              message.getContent(),
                              message.getCreatedAt());
    }
}
