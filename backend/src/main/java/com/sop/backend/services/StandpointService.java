package com.sop.backend.services;

import com.sop.backend.dto.StandpointInputDTO;
import com.sop.backend.models.Party;
import com.sop.backend.models.PartyStandpoint;
import com.sop.backend.models.Question;
import com.sop.backend.models.Stance;
import com.sop.backend.repositories.PartyRepository;
import com.sop.backend.repositories.PartyStandpointRepository;
import com.sop.backend.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandpointService {

    private final PartyRepository partyRepository;
    private final QuestionRepository questionRepository;
    private final PartyStandpointRepository standpointRepository;

    public StandpointService(PartyRepository partyRepository,
                             QuestionRepository questionRepository,
                             PartyStandpointRepository standpointRepository) {
        this.partyRepository = partyRepository;
        this.questionRepository = questionRepository;
        this.standpointRepository = standpointRepository;
    }

    public int saveBulkStandpoints(List<StandpointInputDTO> standpoints) {
        int saved = 0;

        for (StandpointInputDTO input : standpoints) {
            Party party = partyRepository.findById(input.getPartyId())
                    .orElseThrow(() -> new RuntimeException("Party not found: " + input.getPartyId()));

            Question question = questionRepository.findById(input.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + input.getQuestionId()));

            List<PartyStandpoint> existing = standpointRepository.findByQuestionId(question.getId());
            PartyStandpoint standpoint = existing.stream()
                    .filter(s -> s.getParty().getId().equals(party.getId()))
                    .findFirst()
                    .orElse(new PartyStandpoint());

            standpoint.setParty(party);
            standpoint.setQuestion(question);
            standpoint.setStance(Stance.valueOf(input.getStance().toUpperCase()));

            standpointRepository.save(standpoint);
            saved++;
        }

        return saved;
    }
}