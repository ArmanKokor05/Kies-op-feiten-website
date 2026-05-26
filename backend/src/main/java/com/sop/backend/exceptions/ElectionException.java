package com.sop.backend.exceptions;

public class ElectionException extends RuntimeException {
    public ElectionException(String message) {
        super("ElectionMapper: " + message);
    }
}
