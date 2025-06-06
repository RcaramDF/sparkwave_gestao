package com.sparkwave.login.controller;

import com.sparkwave.login.model.AccessLog;
import com.sparkwave.login.model.User;
import com.sparkwave.login.repository.AccessLogRepository;
import com.sparkwave.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para o dashboard administrativo.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccessLogRepository accessLogRepository;

    /**
     * Obtém estatísticas gerais para o dashboard.
     *
     * @return Estatísticas
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total de usuários
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // Usuários ativos
        List<User> users = userRepository.findAll();
        long activeUsers = users.stream().filter(User::isActive).count();
        stats.put("activeUsers", activeUsers);
        
        // Usuários por perfil
        Map<String, Long> usersByRole = users.stream()
                .flatMap(user -> user.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));
        stats.put("usersByRole", usersByRole);
        
        // Acessos recentes
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusWeeks(1);
        
        List<AccessLog> recentLogs = accessLogRepository.findByAccessTimeBetween(lastWeek, now);
        stats.put("totalLogins", recentLogs.stream().filter(log -> "LOGIN".equals(log.getAction())).count());
        stats.put("successfulLogins", recentLogs.stream().filter(log -> "LOGIN".equals(log.getAction()) && "SUCCESS".equals(log.getStatus())).count());
        stats.put("failedLogins", recentLogs.stream().filter(log -> "LOGIN".equals(log.getAction()) && "FAILED".equals(log.getStatus())).count());
        
        // Acessos por dia
        Map<String, Long> loginsByDay = recentLogs.stream()
                .filter(log -> "LOGIN".equals(log.getAction()) && "SUCCESS".equals(log.getStatus()))
                .collect(Collectors.groupingBy(
                        log -> log.getAccessTime().toLocalDate().toString(),
                        Collectors.counting()
                ));
        stats.put("loginsByDay", loginsByDay);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtém estatísticas de acesso para um usuário específico.
     *
     * @param userId ID do usuário
     * @return Estatísticas
     */
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    // Informações do usuário
                    stats.put("username", user.getUsername());
                    stats.put("email", user.getEmail());
                    stats.put("fullName", user.getFullName());
                    stats.put("active", user.isActive());
                    stats.put("roles", user.getRoles());
                    
                    // Acessos recentes
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime lastMonth = now.minusMonths(1);
                    
                    List<AccessLog> userLogs = accessLogRepository.findByUserAndAccessTimeBetween(user, lastMonth, now);
                    stats.put("totalLogins", userLogs.stream().filter(log -> "LOGIN".equals(log.getAction())).count());
                    stats.put("successfulLogins", userLogs.stream().filter(log -> "LOGIN".equals(log.getAction()) && "SUCCESS".equals(log.getStatus())).count());
                    stats.put("failedLogins", userLogs.stream().filter(log -> "LOGIN".equals(log.getAction()) && "FAILED".equals(log.getStatus())).count());
                    
                    // Último acesso
                    userLogs.stream()
                            .filter(log -> "LOGIN".equals(log.getAction()) && "SUCCESS".equals(log.getStatus()))
                            .max((a, b) -> a.getAccessTime().compareTo(b.getAccessTime()))
                            .ifPresent(log -> {
                                stats.put("lastLogin", log.getAccessTime());
                                stats.put("lastIp", log.getIpAddress());
                                stats.put("lastUserAgent", log.getUserAgent());
                            });
                    
                    return ResponseEntity.ok(stats);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

