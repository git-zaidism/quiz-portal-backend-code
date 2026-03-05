package com.exam.model;

public class JwtResponse {
    String token;
    Long expiresInMinutes;

    public JwtResponse(String token) {
        this.token = token;
    }

    public JwtResponse(String token, Long expiresInMinutes) {
        this.token = token;
        this.expiresInMinutes = expiresInMinutes;
    }

    public JwtResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(Long expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }
}
