package com.sop.backend.controllers;

import com.sop.backend.dto.CreateElectionsDTO;
import com.sop.backend.dto.ElectionDTO;
import com.sop.backend.mapper.ElectionDtoMapper;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/election")
public class ElectionController {

    private final ElectionService electionService;
    private final ElectionDtoMapper electionMapper;

    public ElectionController(ElectionService electionService, ElectionDtoMapper electionMapper) {
        this.electionService = electionService;
        this.electionMapper = electionMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateElectionsDTO>> createElectionsFromXml(
            @RequestParam MultipartFile[] electionXml
    ) throws IOException {
        CreateElectionsDTO response = electionService.createElection(electionXml);
        return ResponseEntity.ok(
                new ApiResponse<>("elections created successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElectionDTO>>> getAllElections() {
        List<ElectionDTO> elections =
                electionMapper.electionsToDtoList(electionService.getAllElections());

        return ResponseEntity.ok(
                new ApiResponse<>("elections retrieved successfully", elections)
        );
    }
}
