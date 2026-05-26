package com.sop.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sop.backend.dto.MatchResultDTO;
import com.sop.backend.dto.SaveSurveyResultDTO;
import com.sop.backend.dto.SurveyResultDetailDTO;
import com.sop.backend.dto.SurveyResultResponseDTO;
import com.sop.backend.models.SurveyResult;
import com.sop.backend.models.User;
import com.sop.backend.repositories.SurveyResultRepository;
import com.sop.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyUnitTest {

    @Mock
    private SurveyResultRepository surveyResultRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StemwijzerService stemwijzerService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SurveyResultService surveyResultService;

    private User testUser;
    private User differentUser;
    private SurveyResult testSurveyResult;
    private Map<Long, String> testAnswers;
    private List<MatchResultDTO> testMatches;

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        testUser.setName("Test User");
        testUser.setEmail("test@test.com");

        differentUser = new User();
        ReflectionTestUtils.setField(differentUser, "id", 999L);
        differentUser.setName("Different User");
        differentUser.setEmail("different@test.com");

        testAnswers = new HashMap<>();
        testAnswers.put(1L, "AGREE");
        testAnswers.put(2L, "DISAGREE");
        testAnswers.put(3L, "NEUTRAL");

        testMatches = Arrays.asList(
                new MatchResultDTO("VVD", "#FF6600", 75.5),
                new MatchResultDTO("PVV", "#0000FF", 65.0),
                new MatchResultDTO("D66", "#00FF00", 55.5)
        );

        testSurveyResult = new SurveyResult();
        ReflectionTestUtils.setField(testSurveyResult, "id", 1L);
        testSurveyResult.setUser(testUser);
        testSurveyResult.setCompletedAt(LocalDateTime.now());
        testSurveyResult.setTopMatchParty("VVD");
        testSurveyResult.setTopMatchPercentage(75.5);
        testSurveyResult.setSecondMatchParty("PVV");
        testSurveyResult.setSecondMatchPercentage(65.0);
        testSurveyResult.setThirdMatchParty("D66");
        testSurveyResult.setThirdMatchPercentage(55.5);
    }

    @Test
    void saveSurveyResult_ShouldSaveWithTop3Parties() throws Exception {
        SaveSurveyResultDTO dto = new SaveSurveyResultDTO();
        dto.setAnswers(testAnswers);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(stemwijzerService.calculateMatch(testAnswers)).thenReturn(testMatches);
        when(objectMapper.writeValueAsString(testAnswers)).thenReturn("{\"1\":\"AGREE\"}");
        when(surveyResultRepository.save(any(SurveyResult.class))).thenReturn(testSurveyResult);

        SurveyResultResponseDTO result = surveyResultService.saveSurveyResult(1L, dto);

        assertNotNull(result);
        assertEquals("VVD", result.getTopMatchParty());
        assertEquals(75.5, result.getTopMatchPercentage());
        assertNotNull(result.getCompletedAt());

        verify(surveyResultRepository, times(1)).save(any(SurveyResult.class));
    }

    @Test
    void saveSurveyResult_ShouldThrowException_WhenUserNotFound() {
        SaveSurveyResultDTO dto = new SaveSurveyResultDTO();
        dto.setAnswers(testAnswers);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            surveyResultService.saveSurveyResult(999L, dto);
        });

        assertEquals("User not found", exception.getMessage());
        verify(surveyResultRepository, never()).save(any());
    }

    @Test
    void getLatestResult_ShouldReturnDetailDTO_WhenResultExists() throws Exception {
        String answersJson = "{\"1\":\"AGREE\",\"2\":\"DISAGREE\"}";
        testSurveyResult.setAnswers(answersJson);

        when(surveyResultRepository.findFirstByUserIdOrderByCompletedAtDesc(1L))
                .thenReturn(Optional.of(testSurveyResult));
        when(objectMapper.readValue(eq(answersJson), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(testAnswers);
        when(stemwijzerService.calculateMatch(testAnswers)).thenReturn(testMatches);

        Optional<SurveyResultDetailDTO> result = surveyResultService.getLatestResult(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertNotNull(result.get().getCompletedAt());
        assertEquals(3, result.get().getMatches().size());
        assertEquals("VVD", result.get().getMatches().get(0).getPartyName());
        assertEquals(75.5, result.get().getMatches().get(0).getMatchPercentage());
    }

    @Test
    void getLatestResult_ShouldReturnEmpty_WhenNoResultExists() {
        when(surveyResultRepository.findFirstByUserIdOrderByCompletedAtDesc(999L))
                .thenReturn(Optional.empty());

        Optional<SurveyResultDetailDTO> result = surveyResultService.getLatestResult(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getResultDetail_ShouldReturnDetailDTO_WhenResultExists() throws Exception {
        String answersJson = "{\"1\":\"AGREE\",\"2\":\"DISAGREE\"}";
        testSurveyResult.setAnswers(answersJson);

        when(surveyResultRepository.findById(1L)).thenReturn(Optional.of(testSurveyResult));
        when(objectMapper.readValue(eq(answersJson), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(testAnswers);
        when(stemwijzerService.calculateMatch(testAnswers)).thenReturn(testMatches);

        SurveyResultDetailDTO result = surveyResultService.getResultDetail(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getCompletedAt());
        assertEquals(3, result.getMatches().size());
    }

    @Test
    void getResultDetail_ShouldThrowException_WhenUnauthorizedAccess() {
        testSurveyResult.setUser(differentUser);

        when(surveyResultRepository.findById(1L)).thenReturn(Optional.of(testSurveyResult));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            surveyResultService.getResultDetail(1L, 1L);
        });

        assertEquals("Unauthorized access to result", exception.getMessage());
    }

    @Test
    void getUserResults_ShouldReturnListOfResults() {
        List<SurveyResult> surveyResults = Arrays.asList(testSurveyResult);
        when(surveyResultRepository.findByUserIdOrderByCompletedAtDesc(1L))
                .thenReturn(surveyResults);

        List<SurveyResultResponseDTO> results = surveyResultService.getUserResults(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("VVD", results.get(0).getTopMatchParty());
        assertEquals(75.5, results.get(0).getTopMatchPercentage());
    }

    @Test
    void saveSurveyResult_ShouldHandleEmptyMatches() throws Exception {
        SaveSurveyResultDTO dto = new SaveSurveyResultDTO();
        dto.setAnswers(testAnswers);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(stemwijzerService.calculateMatch(testAnswers)).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(testAnswers)).thenReturn("{}");

        SurveyResult emptySurveyResult = new SurveyResult();
        ReflectionTestUtils.setField(emptySurveyResult, "id", 1L);
        emptySurveyResult.setUser(testUser);
        emptySurveyResult.setCompletedAt(LocalDateTime.now());

        when(surveyResultRepository.save(any(SurveyResult.class))).thenReturn(emptySurveyResult);

        SurveyResultResponseDTO result = surveyResultService.saveSurveyResult(1L, dto);

        assertNotNull(result);
        assertNull(result.getTopMatchParty());
        assertNull(result.getTopMatchPercentage());
    }

    @Test
    void saveSurveyResult_ShouldOnlySaveTop3_WhenMoreMatchesExist() throws Exception {
        List<MatchResultDTO> manyMatches = Arrays.asList(
                new MatchResultDTO("Party1", "#FF0000", 90.0),
                new MatchResultDTO("Party2", "#00FF00", 80.0),
                new MatchResultDTO("Party3", "#0000FF", 70.0),
                new MatchResultDTO("Party4", "#FFFF00", 60.0),
                new MatchResultDTO("Party5", "#FF00FF", 50.0)
        );

        SaveSurveyResultDTO dto = new SaveSurveyResultDTO();
        dto.setAnswers(testAnswers);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(stemwijzerService.calculateMatch(testAnswers)).thenReturn(manyMatches);
        when(objectMapper.writeValueAsString(testAnswers)).thenReturn("{}");
        when(surveyResultRepository.save(any(SurveyResult.class))).thenAnswer(invocation -> {
            SurveyResult saved = invocation.getArgument(0);
            assertEquals("Party1", saved.getTopMatchParty());
            assertEquals("Party2", saved.getSecondMatchParty());
            assertEquals("Party3", saved.getThirdMatchParty());
            return saved;
        });

        surveyResultService.saveSurveyResult(1L, dto);

        verify(surveyResultRepository, times(1)).save(any(SurveyResult.class));
    }
}