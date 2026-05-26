package com.sop.backend.mapper;

import com.sop.backend.dto.Party2DTO;
import com.sop.backend.models.Party2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PartyDtoMapper {

    public Party2DTO toDto(Party2 party) {
        return new Party2DTO(party.getId(), party.getName(), party.getAcronym());
    }

    public List<Party2DTO> toDtoList(List<Party2> parties) {
        return parties.stream()
                .map(this::toDto)
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .collect(Collectors.toList());
    }
}
