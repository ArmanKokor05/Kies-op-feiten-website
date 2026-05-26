package com.sop.backend.mapper;

import com.sop.backend.dto.CandidateDTO;
import com.sop.backend.models.Candidate;
import org.springframework.stereotype.Component;

@Component
public class CandidateDtoMapper {
    public CandidateDTO toDto(Candidate candidate) {
        return new CandidateDTO(
                candidate.getId(),
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getInitials(),
                candidate.getGender(),
                candidate.getQualifyingAddress()
        );
    }
}
