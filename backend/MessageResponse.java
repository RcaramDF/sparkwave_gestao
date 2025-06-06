package com.sparkwave.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para mensagens de resposta genéricas.
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}

