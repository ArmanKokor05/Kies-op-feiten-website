package com.sop.backend.services;

import com.sop.backend.dto.QuestionResponseDTO;
import com.sop.backend.models.Question;
import com.sop.backend.repositories.QuestionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionResponseDTO> getAllQuestions() {
        List<Question> allQuestions = questionRepository.findAll();
        return allQuestions.stream()
                .map(q -> new QuestionResponseDTO(
                        q.getTitle(),
                        q.getQuestion(),
                        q.getSourceUrl(),
                        q.getSourceName(),
                        q.getId()
                ))
                .collect(Collectors.toList());
    }
}