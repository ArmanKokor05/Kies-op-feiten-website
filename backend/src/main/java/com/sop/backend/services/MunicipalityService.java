package com.sop.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.models.Municipality;
import com.sop.backend.repositories.MunicipalityRepository;
import com.sop.backend.xml.mappers.MunicipalityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipalityService {

    private final MunicipalityRepository municipalityRepository;
    private final MunicipalityMapper municipalityMapper;

    public MunicipalityService(MunicipalityRepository municipalityRepository, MunicipalityMapper municipalityMapper) {
        this.municipalityRepository = municipalityRepository;
        this.municipalityMapper = municipalityMapper;
    }

    public Municipality parseFromJson(JsonNode jsonNode) {
        List<Municipality> municipalities = municipalityMapper.assembleModel(jsonNode);

        if (municipalities.size() != 1) throw new RuntimeException("Expected exactly one municipality");

        return municipalities.get(0);
    }

    public List<Municipality> getMunicipalities() {
        return municipalityRepository.findAll();
    }

    public Municipality save(Municipality municipality) {
        return municipalityRepository.findByName(municipality.getName())
                .orElseGet(() -> municipalityRepository.save(municipality));
    }
}
