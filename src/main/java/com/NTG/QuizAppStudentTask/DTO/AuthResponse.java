package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    public AuthResponse() {

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

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
}
