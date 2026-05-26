package com.sop.backend;

import com.sop.backend.models.*;
import com.sop.backend.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StemwijzerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PartyStandpointRepository partyStandpointRepository;

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    private Long testUserId;
    private Long question1Id;
    private Long question2Id;

    @BeforeEach
    void setup() {
        surveyResultRepository.deleteAll();
        partyStandpointRepository.deleteAll();
        questionRepository.deleteAll();
        partyRepository.deleteAll();
        discussionRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId();

        Party vvd = new Party();
        vvd.setName("VVD");
        vvd.setColor("#FF6600");
        vvd = partyRepository.save(vvd);

        Party pvv = new Party();
        pvv.setName("PVV");
        pvv.setColor("#0000FF");
        pvv = partyRepository.save(pvv);

        Party d66 = new Party();
        d66.setName("D66");
        d66.setColor("#00FF00");
        d66 = partyRepository.save(d66);

        Question question1 = new Question();
        question1.setTitle("Klimaat");
        question1.setQuestion("Nederland moet meer doen tegen klimaatverandering");
        question1 = questionRepository.save(question1);
        question1Id = question1.getId();

        Question question2 = new Question();
        question2.setTitle("Zorg");
        question2.setQuestion("De zorg moet volledig gratis zijn");
        question2 = questionRepository.save(question2);
        question2Id = question2.getId();

        PartyStandpoint vvdQ1 = new PartyStandpoint();
        vvdQ1.setParty(vvd);
        vvdQ1.setQuestion(question1);
        vvdQ1.setStance(Stance.AGREE);
        partyStandpointRepository.save(vvdQ1);

        PartyStandpoint vvdQ2 = new PartyStandpoint();
        vvdQ2.setParty(vvd);
        vvdQ2.setQuestion(question2);
        vvdQ2.setStance(Stance.AGREE);
        partyStandpointRepository.save(vvdQ2);

        PartyStandpoint pvvQ1 = new PartyStandpoint();
        pvvQ1.setParty(pvv);
        pvvQ1.setQuestion(question1);
        pvvQ1.setStance(Stance.AGREE);
        partyStandpointRepository.save(pvvQ1);

        PartyStandpoint pvvQ2 = new PartyStandpoint();
        pvvQ2.setParty(pvv);
        pvvQ2.setQuestion(question2);
        pvvQ2.setStance(Stance.DISAGREE);
        partyStandpointRepository.save(pvvQ2);

        PartyStandpoint d66Q1 = new PartyStandpoint();
        d66Q1.setParty(d66);
        d66Q1.setQuestion(question1);
        d66Q1.setStance(Stance.DISAGREE);
        partyStandpointRepository.save(d66Q1);

        PartyStandpoint d66Q2 = new PartyStandpoint();
        d66Q2.setParty(d66);
        d66Q2.setQuestion(question2);
        d66Q2.setStance(Stance.DISAGREE);
        partyStandpointRepository.save(d66Q2);
    }

    @Test
    void api_CalculateMatch_RetourneertMatches() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "AGREE",
                    "%d": "AGREE"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].partyName", is("VVD")))
                .andExpect(jsonPath("$[0].partyColor", notNullValue()))
                .andExpect(jsonPath("$[0].matchPercentage", is(100.0)));
    }

    @Test
    void api_SaveSurveyResult_SlaatResultaatOp() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "AGREE",
                    "%d": "DISAGREE"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/save")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.topMatchParty", is("PVV")))
                .andExpect(jsonPath("$.topMatchPercentage", is(100.0)))
                .andExpect(jsonPath("$.completedAt", notNullValue()));
    }

    @Test
    void api_GetLatestResult_RetourneertLaatsteResultaat() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "AGREE",
                    "%d": "AGREE"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/save")
                .header("X-User-Id", testUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        mockMvc.perform(get("/api/stemwijzer/latest-result")
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.matches", hasSize(3)))
                .andExpect(jsonPath("$.matches[0].partyName", is("VVD")))
                .andExpect(jsonPath("$.matches[0].matchPercentage", is(100.0)));
    }

    @Test
    void api_GetLatestResult_Geeft404IndienGeenResultaat() throws Exception {
        mockMvc.perform(get("/api/stemwijzer/latest-result")
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void api_GetResultDetail_RetourneertSpecifiekResultaat() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "AGREE",
                    "%d": "DISAGREE"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/save")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Long resultId = surveyResultRepository.findFirstByUserIdOrderByCompletedAtDesc(testUserId)
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/api/stemwijzer/result/" + resultId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resultId.intValue())))
                .andExpect(jsonPath("$.matches", hasSize(3)))
                .andExpect(jsonPath("$.matches[0].partyColor", notNullValue()));
    }

    @Test
    void api_CalculateMatch_KleurenZijnCorrect() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "NEUTRAL",
                    "%d": "NEUTRAL"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].partyColor", notNullValue()))
                .andExpect(jsonPath("$[1].partyColor", notNullValue()))
                .andExpect(jsonPath("$[2].partyColor", notNullValue()))
                .andExpect(jsonPath("$[0].partyColor", matchesPattern("^#[0-9A-Fa-f]{6}$")))
                .andExpect(jsonPath("$[1].partyColor", matchesPattern("^#[0-9A-Fa-f]{6}$")))
                .andExpect(jsonPath("$[2].partyColor", matchesPattern("^#[0-9A-Fa-f]{6}$")));
    }

    @Test
    void api_SaveAndRetrieve_BehoudenKleurenEnPercentages() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "AGREE",
                    "%d": "AGREE"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/save")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/stemwijzer/latest-result")
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matches[0].partyColor", notNullValue()))
                .andExpect(jsonPath("$.matches[0].matchPercentage", is(100.0)))
                .andExpect(jsonPath("$.matches[1].matchPercentage", greaterThanOrEqualTo(0.0)))
                .andExpect(jsonPath("$.matches[2].matchPercentage", greaterThanOrEqualTo(0.0)));
    }

    @Test
    void api_CalculateMatch_RetourneertGesorteerdOpPercentage() throws Exception {
        String requestBody = String.format("""
            {
                "answers": {
                    "%d": "AGREE",
                    "%d": "DISAGREE"
                }
            }
            """, question1Id, question2Id);

        mockMvc.perform(post("/api/stemwijzer/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].partyName", is("PVV")))  // 100% match
                .andExpect(jsonPath("$[0].matchPercentage", is(100.0)))
                .andExpect(jsonPath("$[1].matchPercentage", lessThanOrEqualTo(100.0)))
                .andExpect(jsonPath("$[2].matchPercentage", lessThanOrEqualTo(100.0)));
    }
}