package com.NTG.QuizAppStudentTask.DTO;

import lombok.Builder;
import lombok.Data;


public class AuthResponse {
    private String token;
    public AuthResponse() {
        // needed for Jackson
    }
    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
