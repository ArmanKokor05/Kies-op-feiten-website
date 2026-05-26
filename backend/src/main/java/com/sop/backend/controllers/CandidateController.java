package com.sop.backend.controllers;

import com.sop.backend.dto.CandidateDTO;
import com.sop.backend.dto.CreateCandidatesDTO;
import com.sop.backend.mapper.CandidateDtoMapper;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.CandidateListEntryService;
import com.sop.backend.services.CandidateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateListEntryService candidateListEntryService;
    private final CandidateService candidateService;
    private final CandidateDtoMapper candidateDtoMapper;

    public CandidateController(CandidateService candidateService,
                               CandidateDtoMapper candidateDtoMapper,
                               CandidateListEntryService candidateListEntryService) {
        this.candidateListEntryService = candidateListEntryService;
        this.candidateDtoMapper = candidateDtoMapper;
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateCandidatesDTO>> createCandidatesFromXml(@RequestParam MultipartFile[] candidateXml) throws IOException {
        CreateCandidatesDTO response = this.candidateService.createCandidate(candidateXml);
        return ResponseEntity.ok(new ApiResponse<>("candidates created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CandidateDTO>>> getCandidatesByElectionAndParty(
            @RequestParam long electionId,
            @RequestParam long partyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<CandidateDTO> dtoPage = candidateListEntryService
                .findByElectionAndParty(electionId, partyId, pageable)
                .map(entry -> candidateDtoMapper.toDto(entry.getCandidate()));

        return ResponseEntity.ok(
                new ApiResponse<>("Candidates fetched successfully", dtoPage)
        );
    }
}