package com.sop.backend.services;

import com.sop.backend.dto.MatchResultDTO;
import com.sop.backend.models.Party;
import com.sop.backend.models.PartyStandpoint;
import com.sop.backend.models.Stance;
import com.sop.backend.repositories.PartyRepository;
import com.sop.backend.repositories.PartyStandpointRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StemwijzerService {

    private final PartyRepository partyRepository;
    private final PartyStandpointRepository partyStandpointRepository;
    private final PartyColorService partyColorService;

    public StemwijzerService(PartyRepository partyRepository,
                             PartyStandpointRepository partyStandpointRepository,
                             PartyColorService partyColorService) {
        this.partyRepository = partyRepository;
        this.partyStandpointRepository = partyStandpointRepository;
        this.partyColorService = partyColorService;
    }

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public List<PartyStandpoint> getAllStandpoints() {
        return partyStandpointRepository.findAll();
    }

    public List<MatchResultDTO> calculateMatch(Map<Long, String> userAnswers) {
        List<Party> parties = partyRepository.findAll();
        List<MatchResultDTO> results = new ArrayList<>();

        for (Party party : parties) {
            List<PartyStandpoint> standpoints = partyStandpointRepository.findByPartyId(party.getId());

            int totalQuestions = userAnswers.size();
            int matchingAnswers = 0;

            for (PartyStandpoint standpoint : standpoints) {
                Long questionId = standpoint.getQuestion().getId();
                String userAnswer = userAnswers.get(questionId);

                if (userAnswer != null) {
                    Stance userStance = Stance.valueOf(userAnswer.toUpperCase());
                    if (userStance == standpoint.getStance()) {
                        matchingAnswers++;
                    }
                }
            }

            double matchPercentage = totalQuestions > 0
                    ? (double) matchingAnswers / totalQuestions * 100
                    : 0;


            String color = partyColorService.getPartyColor(party.getName());

            results.add(new MatchResultDTO(
                    party.getName(),
                    color,
                    Math.round(matchPercentage * 10.0) / 10.0
            ));
        }

        return results.stream()
                .sorted((a, b) -> Double.compare(b.getMatchPercentage(), a.getMatchPercentage()))
                .collect(Collectors.toList());
    }
}