package com.sop.backend.services;

import com.sop.backend.dto.MatchResultDTO;
import com.sop.backend.dto.SaveSurveyResultDTO;
import com.sop.backend.dto.SurveyResultDetailDTO;
import com.sop.backend.dto.SurveyResultResponseDTO;
import com.sop.backend.models.SurveyResult;
import com.sop.backend.models.User;
import com.sop.backend.repositories.SurveyResultRepository;
import com.sop.backend.repositories.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyResultService {

    private final SurveyResultRepository surveyResultRepository;
    private final UserRepository userRepository;
    private final StemwijzerService stemwijzerService;
    private final ObjectMapper objectMapper;

    public SurveyResultService(SurveyResultRepository surveyResultRepository,
                               UserRepository userRepository,
                               StemwijzerService stemwijzerService,
                               ObjectMapper objectMapper) {
        this.surveyResultRepository = surveyResultRepository;
        this.userRepository = userRepository;
        this.stemwijzerService = stemwijzerService;
        this.objectMapper = objectMapper;
    }

    public SurveyResultResponseDTO saveSurveyResult(Long userId, SaveSurveyResultDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MatchResultDTO> matchResults = stemwijzerService.calculateMatch(dto.getAnswers());

        SurveyResult surveyResult = new SurveyResult();
        surveyResult.setUser(user);
        surveyResult.setCompletedAt(LocalDateTime.now(ZoneId.of("Europe/Amsterdam")));

        try {
            surveyResult.setAnswers(objectMapper.writeValueAsString(dto.getAnswers()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save answers");
        }

        if (!matchResults.isEmpty()) {
            MatchResultDTO first = matchResults.get(0);
            surveyResult.setTopMatchParty(first.getPartyName());
            surveyResult.setTopMatchPercentage(first.getMatchPercentage());

            if (matchResults.size() > 1) {
                MatchResultDTO second = matchResults.get(1);
                surveyResult.setSecondMatchParty(second.getPartyName());
                surveyResult.setSecondMatchPercentage(second.getMatchPercentage());
            }

            if (matchResults.size() > 2) {
                MatchResultDTO third = matchResults.get(2);
                surveyResult.setThirdMatchParty(third.getPartyName());
                surveyResult.setThirdMatchPercentage(third.getMatchPercentage());
            }
        }

        SurveyResult saved = surveyResultRepository.save(surveyResult);

        return new SurveyResultResponseDTO(
                saved.getId(),
                saved.getTopMatchParty(),
                saved.getTopMatchPercentage(),
                saved.getCompletedAt()
        );
    }

    public List<SurveyResultResponseDTO> getUserResults(Long userId) {
        List<SurveyResult> results = surveyResultRepository
                .findByUserIdOrderByCompletedAtDesc(userId);

        return results.stream()
                .map(r -> new SurveyResultResponseDTO(
                        r.getId(),
                        r.getTopMatchParty(),
                        r.getTopMatchPercentage(),
                        r.getCompletedAt()
                ))
                .collect(Collectors.toList());
    }

    public Optional<SurveyResultDetailDTO> getLatestResult(Long userId) {
        Optional<SurveyResult> latestResult = surveyResultRepository
                .findFirstByUserIdOrderByCompletedAtDesc(userId);

        return latestResult.map(this::convertToDetailDTO);
    }

    public SurveyResultDetailDTO getResultDetail(Long userId, Long resultId) {
        SurveyResult result = surveyResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        if (!result.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to result");
        }

        return convertToDetailDTO(result);
    }

    private SurveyResultDetailDTO convertToDetailDTO(SurveyResult result) {
        try {
            Map<Long, String> answers = objectMapper.readValue(
                    result.getAnswers(),
                    new TypeReference<Map<Long, String>>() {}
            );

            List<MatchResultDTO> allMatches = stemwijzerService.calculateMatch(answers);

            return new SurveyResultDetailDTO(
                    result.getId(),
                    result.getCompletedAt(),
                    allMatches
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse stored answers", e);
        }
    }
}
