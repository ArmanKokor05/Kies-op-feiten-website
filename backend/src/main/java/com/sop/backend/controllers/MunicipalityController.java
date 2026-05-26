package com.sop.backend.controllers;

import com.sop.backend.dto.MunicipalityDTO;
import com.sop.backend.mapper.MunicipalityDtoMapper;
import com.sop.backend.responses.ApiResponse;
import com.sop.backend.services.MunicipalityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/municipality")
public class MunicipalityController {

    private final MunicipalityService municipalityService;
    private final MunicipalityDtoMapper municipalityDtoMapper;

    public MunicipalityController(MunicipalityService municipalityService,
                                  MunicipalityDtoMapper municipalityDtoMapper) {
        this.municipalityService = municipalityService;
        this.municipalityDtoMapper = municipalityDtoMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MunicipalityDTO>>> getMunicipalities() {
        List<MunicipalityDTO> municipalities = municipalityDtoMapper.toDtoList(municipalityService.getMunicipalities());

        return ResponseEntity.ok(
                new ApiResponse<>("Municipalities fetched successfully", municipalities)
        );
    }
}
