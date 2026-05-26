package com.sop.backend.exceptions;

public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super("NotificationService" + message);
    }
}
