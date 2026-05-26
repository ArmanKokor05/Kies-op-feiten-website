package com.sop.backend.exceptions;

public class CandidateMapperException extends RuntimeException {
    public CandidateMapperException(String message) {
        super("CandidateMapper: " + message);
    }
}
