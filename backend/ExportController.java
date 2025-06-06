package com.sparkwave.login.controller;

import com.sparkwave.login.model.AccessLog;
import com.sparkwave.login.model.User;
import com.sparkwave.login.service.AccessLogService;
import com.sparkwave.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para exportação de dados.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/export")
@PreAuthorize("hasRole('ADMIN')")
public class ExportController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AccessLogService accessLogService;

    /**
     * Exporta a lista de usuários em formato CSV.
     *
     * @return Arquivo CSV
     */
    @GetMapping("/users/csv")
    public ResponseEntity<String> exportUsersAsCsv() {
        List<User> users = userService.findAll();
        
        // Cabeçalho do CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Username,Email,Nome Completo,Ativo,Perfis\n");
        
        // Dados dos usuários
        for (User user : users) {
            csv.append(user.getId()).append(",");
            csv.append(user.getUsername()).append(",");
            csv.append(user.getEmail()).append(",");
            csv.append(escapeCsvField(user.getFullName())).append(",");
            csv.append(user.isActive()).append(",");
            csv.append(escapeCsvField(String.join(", ", user.getRoles()))).append("\n");
        }
        
        // Configurar o cabeçalho da resposta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "usuarios_sparkwave.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }

    /**
     * Exporta o histórico de acessos em formato CSV.
     *
     * @param start Data/hora inicial (opcional)
     * @param end Data/hora final (opcional)
     * @return Arquivo CSV
     */
    @GetMapping("/access-logs/csv")
    public ResponseEntity<String> exportAccessLogsAsCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<AccessLog> logs;
        
        if (start != null && end != null) {
            logs = accessLogService.findByPeriod(start, end);
        } else {
            logs = accessLogService.findAll();
        }
        
        // Cabeçalho do CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Usuário,Data/Hora,Endereço IP,User Agent,Ação,Status\n");
        
        // Formatador de data/hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Dados dos logs
        for (AccessLog log : logs) {
            csv.append(log.getId()).append(",");
            csv.append(log.getUser().getUsername()).append(",");
            csv.append(log.getAccessTime().format(formatter)).append(",");
            csv.append(escapeCsvField(log.getIpAddress())).append(",");
            csv.append(escapeCsvField(log.getUserAgent())).append(",");
            csv.append(escapeCsvField(log.getAction())).append(",");
            csv.append(escapeCsvField(log.getStatus())).append("\n");
        }
        
        // Configurar o cabeçalho da resposta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "historico_acessos_sparkwave.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }
    
    /**
     * Escapa campos para o formato CSV.
     *
     * @param field Campo a ser escapado
     * @return Campo escapado
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // Se o campo contém vírgula, aspas ou quebra de linha, envolve em aspas
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Substituir aspas por aspas duplas
            field = field.replace("\"", "\"\"");
            // Envolver em aspas
            return "\"" + field + "\"";
        }
        
        return field;
    }
}

