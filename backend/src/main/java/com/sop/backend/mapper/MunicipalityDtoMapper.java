package com.sop.backend.mapper;

import com.sop.backend.dto.MunicipalityDTO;
import com.sop.backend.models.Municipality;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MunicipalityDtoMapper {

    public MunicipalityDTO toDto(Municipality municipality) {
        return new MunicipalityDTO(municipality.getId(), municipality.getName());
    }

    public List<MunicipalityDTO> toDtoList(List<Municipality> municipalities) {
        return municipalities.stream()
                .map(this::toDto)
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .collect(Collectors.toList());
    }
}
