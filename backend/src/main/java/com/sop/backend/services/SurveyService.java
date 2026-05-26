package com.sop.backend.services;

import com.sop.backend.models.Survey;
import com.sop.backend.dto.SurveyDTO;
import com.sop.backend.repositories.SurveyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    public SurveyDTO createSurvey(SurveyDTO surveyDTO) {
        Survey survey = new Survey();
        survey.setTitle(surveyDTO.getTitle());
        survey.setDescription(surveyDTO.getDescription());
        survey.setCreatedDate(LocalDate.now());

        // Save to database
        Survey savedSurvey = surveyRepository.save(survey);

        SurveyDTO response = new SurveyDTO();
        response.setId(savedSurvey.getId());
        response.setTitle(savedSurvey.getTitle());
        response.setDescription(savedSurvey.getDescription());
        response.setCreatedDate(savedSurvey.getCreatedDate());

        return response;
    }
}
