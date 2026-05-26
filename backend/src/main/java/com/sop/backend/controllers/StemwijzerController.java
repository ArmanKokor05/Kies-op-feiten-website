package com.sop.backend.controllers;

import com.sop.backend.dto.*;
import com.sop.backend.models.Party;
import com.sop.backend.services.StemwijzerService;
import com.sop.backend.services.SurveyResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stemwijzer")
@CrossOrigin(origins = "*")
public class StemwijzerController {

    private final StemwijzerService stemwijzerService;
    private final SurveyResultService surveyResultService;

    public StemwijzerController(StemwijzerService stemwijzerService,
                                SurveyResultService surveyResultService) {
        this.stemwijzerService = stemwijzerService;
        this.surveyResultService = surveyResultService;
    }

    @GetMapping("/parties")
    public ResponseEntity<List<Party>> getAllParties() {
        return ResponseEntity.ok(stemwijzerService.getAllParties());
    }

    @PostMapping("/calculate")
    public ResponseEntity<List<MatchResultDTO>> calculateMatch(
            @RequestBody UserAnswerDTO userAnswers) {
        List<MatchResultDTO> results =
                stemwijzerService.calculateMatch(userAnswers.getAnswers());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/latest-result")
    public ResponseEntity<SurveyResultDetailDTO> getLatestResult(
            Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = Long.parseLong(authentication.getName());
        Optional<SurveyResultDetailDTO> latestResult =
                surveyResultService.getLatestResult(userId);

        return latestResult
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<SurveyResultDetailDTO> getResultDetail(
            Authentication authentication,
            @PathVariable Long id) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = Long.parseLong(authentication.getName());

        try {
            SurveyResultDetailDTO result = surveyResultService.getResultDetail(userId, id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/save")
    public ResponseEntity<SurveyResultResponseDTO> saveSurveyResult(
            Authentication authentication,
            @RequestBody SaveSurveyResultDTO dto) {

        Long userId = Long.parseLong(authentication.getName());
        SurveyResultResponseDTO result = surveyResultService.saveSurveyResult(userId, dto);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/my-results")
    public ResponseEntity<List<SurveyResultResponseDTO>> getMyResults(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        List<SurveyResultResponseDTO> results = surveyResultService.getUserResults(userId);

        return ResponseEntity.ok(results);
    }
}