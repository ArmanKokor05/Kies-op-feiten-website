package com.sop.backend.controllers;

import com.sop.backend.dto.SurveyDTO;
import com.sop.backend.services.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/survey")
    public ResponseEntity<SurveyDTO> createSurvey(@RequestBody SurveyDTO surveyDTO) {
        SurveyDTO response = surveyService.createSurvey(surveyDTO);
        return ResponseEntity.ok(response);
    }
}
