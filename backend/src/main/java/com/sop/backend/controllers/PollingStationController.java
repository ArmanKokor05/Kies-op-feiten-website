package com.sop.backend.controllers;

import com.sop.backend.dto.CreatePollingStationsDTO;
import com.sop.backend.dto.PollingStationDTO;
import com.sop.backend.mapper.PollingStationDtoMapper;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.PollingStationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pollingstation")
public class PollingStationController {

    private final PollingStationService pollingStationService;
    private final PollingStationDtoMapper pollingStationDtoMapper;

    public PollingStationController(PollingStationService pollingStationService, PollingStationDtoMapper pollingStationDtoMapper) {
        this.pollingStationService = pollingStationService;
        this.pollingStationDtoMapper = pollingStationDtoMapper;
    }

    @PostMapping("/xml")
    public ResponseEntity<ApiResponse<CreatePollingStationsDTO>> createPollingStationsFromXml(
            @RequestParam MultipartFile[] pollingStationXml) throws IOException {
        CreatePollingStationsDTO response = this.pollingStationService.createPollingStations(pollingStationXml);
        return ResponseEntity.ok(new ApiResponse<>("PollingStations created successfully", response));
    }

    @GetMapping("/election")
    public ResponseEntity<ApiResponse<Page<PollingStationDTO>>> getPollingStationsByElection(
            @RequestParam long electionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PollingStationDTO> dtoPage = pollingStationService
                .findByElectionId(electionId, pageable)
                .map(pollingStationDtoMapper::toDto);

        return ResponseEntity.ok(
                new ApiResponse<>("Polling stations fetched successfully", dtoPage)
        );
    }

    @GetMapping("/municipality")
    public ResponseEntity<ApiResponse<Page<PollingStationDTO>>> getPollingStationsByMunicipalityAndElection(
            @RequestParam long electionId,
            @RequestParam long municipalityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PollingStationDTO> dtoPage = pollingStationService
                .findByElectionAndMunicipality(electionId, municipalityId, pageable)
                .map(pollingStationDtoMapper::toDto);

        return ResponseEntity.ok(
                new ApiResponse<>("Polling stations fetched successfully", dtoPage)
        );
    }
}
