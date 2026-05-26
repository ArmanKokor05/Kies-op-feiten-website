package com.sop.backend.dto;

import java.util.List;

public class BulkStandpointDTO {
    private List<StandpointInputDTO> standpoints;

    public BulkStandpointDTO() {
    }

    public List<StandpointInputDTO> getStandpoints() {
        return standpoints;
    }

    public void setStandpoints(List<StandpointInputDTO> standpoints) {
        this.standpoints = standpoints;
    }
}