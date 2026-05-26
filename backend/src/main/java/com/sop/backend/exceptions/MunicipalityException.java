package com.sop.backend.exceptions;

public class MunicipalityException extends RuntimeException {
    public MunicipalityException(String message) {
        super("Municipality:" + message);
    }
}
