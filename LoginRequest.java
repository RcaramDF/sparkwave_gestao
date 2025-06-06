package com.sparkwave.login.model;

import lombok.Data;

/**
 * DTO para a requisição de login.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}

