package com.sparkwave.login.model;

import lombok.Data;

import java.util.Set;

/**
 * DTO para a requisição de registro de usuário.
 */
@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private Set<String> roles;
}

