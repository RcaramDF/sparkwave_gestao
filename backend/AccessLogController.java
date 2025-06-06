package com.sparkwave.login.controller;

import com.sparkwave.login.model.AccessLog;
import com.sparkwave.login.model.User;
import com.sparkwave.login.service.AccessLogService;
import com.sparkwave.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para gerenciamento de logs de acesso na área de administração.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/access-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;
    
    @Autowired
    private UserService userService;

    /**
     * Obtém todos os logs de acesso.
     *
     * @return Lista de logs de acesso
     */
    @GetMapping
    public ResponseEntity<List<AccessLog>> getAllAccessLogs() {
        List<AccessLog> logs = accessLogService.findAll();
        return ResponseEntity.ok(logs);
    }

    /**
     * Obtém logs de acesso por usuário.
     *
     * @param userId ID do usuário
     * @return Lista de logs de acesso
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccessLog>> getAccessLogsByUser(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(accessLogService.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtém logs de acesso por período.
     *
     * @param start Data/hora inicial
     * @param end Data/hora final
     * @return Lista de logs de acesso
     */
    @GetMapping("/period")
    public ResponseEntity<List<AccessLog>> getAccessLogsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AccessLog> logs = accessLogService.findByPeriod(start, end);
        return ResponseEntity.ok(logs);
    }

    /**
     * Obtém logs de acesso por usuário e período.
     *
     * @param userId ID do usuário
     * @param start Data/hora inicial
     * @param end Data/hora final
     * @return Lista de logs de acesso
     */
    @GetMapping("/user/{userId}/period")
    public ResponseEntity<List<AccessLog>> getAccessLogsByUserAndPeriod(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(accessLogService.findByUserAndPeriod(user, start, end)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtém logs de acesso por status.
     *
     * @param status Status
     * @return Lista de logs de acesso
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccessLog>> getAccessLogsByStatus(@PathVariable String status) {
        List<AccessLog> logs = accessLogService.findByStatus(status);
        return ResponseEntity.ok(logs);
    }
}

