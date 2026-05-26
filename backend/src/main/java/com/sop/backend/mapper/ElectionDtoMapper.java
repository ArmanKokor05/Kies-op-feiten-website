package com.sop.backend.mapper;

import com.sop.backend.dto.ElectionDTO;
import com.sop.backend.models.Election2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElectionDtoMapper {

    public List<ElectionDTO> electionsToDtoList(List<Election2> elections) {
        return elections.stream()
                .map(election -> new ElectionDTO(
                        election.getId(),
                        election.getDate().getYear()
                ))
                .toList();
    }
}
