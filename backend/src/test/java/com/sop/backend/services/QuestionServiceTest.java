package com.sop.backend.services;

import com.sop.backend.dto.QuestionResponseDTO;
import com.sop.backend.models.Question;
import com.sop.backend.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllQuestions() {
        Question question1 = new Question();
        question1.setTitle("Title 1");
        question1.setQuestion("Question 1");

        Question question2 = new Question();
        question2.setTitle("Title 2");
        question2.setQuestion("Question 2");

        List<Question> mockQuestions = Arrays.asList(question1, question2);

        when(questionRepository.findAll()).thenReturn(mockQuestions);

        List<QuestionResponseDTO> result = questionService.getAllQuestions();

        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Question 1", result.get(0).question());
        assertEquals("Title 2", result.get(1).title());
        assertEquals("Question 2", result.get(1).question());

        verify(questionRepository, times(1)).findAll();
    }
}
