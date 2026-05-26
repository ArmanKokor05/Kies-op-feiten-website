package com.sop.backend.controllers;

import com.sop.backend.dto.Party2DTO;
import com.sop.backend.mapper.PartyDtoMapper;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.PartyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/party")
public class PartyController {

    private final PartyService partyService;
    private final PartyDtoMapper partyDtoMapper;

    public PartyController(PartyService partyService,
                                  PartyDtoMapper partyDtoMapper) {
        this.partyService = partyService;
        this.partyDtoMapper = partyDtoMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Party2DTO>>> getParties() {
        List<Party2DTO> parties = partyDtoMapper.toDtoList(partyService.getParties());

        return ResponseEntity.ok(
                new ApiResponse<>("Parties fetched successfully", parties)
        );
    }
}
