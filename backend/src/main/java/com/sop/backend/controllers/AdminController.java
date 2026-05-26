package com.sop.backend.controllers;

import com.sop.backend.dto.BulkStandpointDTO;
import com.sop.backend.models.Party;
import com.sop.backend.models.PartyStandpoint;
import com.sop.backend.models.Question;
import com.sop.backend.repositories.PartyRepository;
import com.sop.backend.repositories.PartyStandpointRepository;
import com.sop.backend.repositories.QuestionRepository;
import com.sop.backend.services.StandpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final PartyRepository partyRepository;
    private final QuestionRepository questionRepository;
    private final PartyStandpointRepository standpointRepository;
    private final StandpointService standpointService;

    public AdminController(PartyRepository partyRepository,
                           QuestionRepository questionRepository,
                           PartyStandpointRepository standpointRepository,
                           StandpointService standpointService) {
        this.partyRepository = partyRepository;
        this.questionRepository = questionRepository;
        this.standpointRepository = standpointRepository;
        this.standpointService = standpointService;
    }

    @GetMapping("/parties")
    public ResponseEntity<List<Party>> getAllParties() {
        return ResponseEntity.ok(partyRepository.findAll());
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @GetMapping("/standpoints")
    public ResponseEntity<List<PartyStandpoint>> getAllStandpoints() {
        return ResponseEntity.ok(standpointRepository.findAll());
    }

    @GetMapping("/standpoints/question/{questionId}")
    public ResponseEntity<List<PartyStandpoint>> getStandpointsByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(standpointRepository.findByQuestionId(questionId));
    }

    @DeleteMapping("/standpoints/{id}")
    public ResponseEntity<String> deleteStandpoint(@PathVariable Long id) {
        standpointRepository.deleteById(id);
        return ResponseEntity.ok("Standpoint deleted!");
    }

    @PostMapping("/standpoints/bulk")
    public ResponseEntity<String> saveBulkStandpoints(@RequestBody BulkStandpointDTO dto) {
        int saved = standpointService.saveBulkStandpoints(dto.getStandpoints());
        return ResponseEntity.ok(saved + " standpoints saved!");
    }
}