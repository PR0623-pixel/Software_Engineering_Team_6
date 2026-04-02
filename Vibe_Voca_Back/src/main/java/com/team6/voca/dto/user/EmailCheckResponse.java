package com.team6.voca.dto.user;

public class EmailCheckResponse {

    private final boolean available;

    public EmailCheckResponse(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}

