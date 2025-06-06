package com.sparkwave.login.model;

import lombok.Data;

import java.util.List;

/**
 * DTO para a resposta de login com o token JWT.
 */
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String redirectUrl;

    public JwtResponse(String token, Long id, String username, String email, List<String> roles, String redirectUrl) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.redirectUrl = redirectUrl;
    }
}

