package com.sop.backend.mapper;

import com.sop.backend.dto.PollingStationDTO;
import com.sop.backend.models.PollingStation;
import org.springframework.stereotype.Component;

@Component
public class PollingStationDtoMapper {

    public PollingStationDTO toDto(PollingStation pollingStation) {
        return new PollingStationDTO(
                pollingStation.getId(),
                pollingStation.getName(),
                pollingStation.getZipcode(),
                pollingStation.getMunicipality().getName()
        );
    }
}

