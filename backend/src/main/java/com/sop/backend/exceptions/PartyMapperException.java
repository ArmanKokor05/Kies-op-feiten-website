package com.sop.backend.exceptions;

public class PartyMapperException extends RuntimeException {
    public PartyMapperException(String message) {
        super("PartyMapperException: " + message);
    }
}
