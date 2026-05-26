package com.sop.backend.responses;

public record ApiResponse<T>(String message, T data) {
}
