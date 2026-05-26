package com.sop.backend.exceptions;

public class DirectMessageException extends RuntimeException {
    public DirectMessageException(String message) {
        super("DirectMessage: " + message);
    }
}
