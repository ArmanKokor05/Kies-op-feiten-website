package com.sop.backend.exceptions;

public class PollingStationMapperException extends RuntimeException {
    public PollingStationMapperException(String message) {
        super("PollingStationMapper: " + message);
    }
}
