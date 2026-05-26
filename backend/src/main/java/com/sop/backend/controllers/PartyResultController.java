package com.sop.backend.controllers;
import com.sop.backend.dto.PartyResultDTO;
import com.sop.backend.services.PartyResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/party-results")
@CrossOrigin(origins = "*")
public class PartyResultController {

    private final PartyResultService partyResultService;

    public PartyResultController(PartyResultService partyResultService) {
        this.partyResultService = partyResultService;
    }

    @GetMapping("/seats/{year}")
    public List<PartyResultDTO> getSeatsByYear(@PathVariable int year) {
        return partyResultService.getSeatsByYearAsDTO(year);
    }


    @GetMapping("/seats/{year}/filter")
    public List<PartyResultDTO> getSeatsByYearAndRegion(
            @PathVariable int year,
            @RequestParam(required = false) String region
    ) {
        return partyResultService.getSeatsByYearAndRegion(year, region);
    }


    @GetMapping("/years")
    public List<Integer> getAvailableYears() {
        return partyResultService.getAvailableYears();
    }


    @GetMapping("/provinces")
    public List<String> getAvailableProvinces() {
        return partyResultService.getAvailableProvinces();
    }


    @GetMapping("/kieskringen")
    public List<String> getAvailableKieskringen() {
        return partyResultService.getAvailableKieskringen();
    }


    @Deprecated
    @GetMapping("/regions")
    public List<String> getAvailableRegions() {
        return partyResultService.getAvailableProvinces();
    }

    @GetMapping("/municipalities")
    public List<String> getAvailableMunicipalities() {
        return partyResultService.getAvailableMunicipalities();
    }

    @GetMapping("/seats/{year}/municipalities")
    public List<PartyResultDTO> getSeatsByYearAndMunicipality(
            @PathVariable int year,
            @RequestParam(required = false) String municipality
    ) {
        return partyResultService.getSeatsByYearAndMunicipality(year, municipality);
    }
}