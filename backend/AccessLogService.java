package com.sparkwave.login.service;

import com.sparkwave.login.model.AccessLog;
import com.sparkwave.login.model.User;
import com.sparkwave.login.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço para gerenciar operações relacionadas a logs de acesso.
 */
@Service
public class AccessLogService {
    
    @Autowired
    private AccessLogRepository accessLogRepository;
    
    /**
     * Registra um acesso no sistema.
     *
     * @param user Usuário
     * @param action Ação realizada
     * @param status Status da ação
     * @param request Requisição HTTP
     * @return Log de acesso criado
     */
    @Transactional
    public AccessLog logAccess(User user, String action, String status, HttpServletRequest request) {
        AccessLog accessLog = new AccessLog();
        accessLog.setUser(user);
        accessLog.setAccessTime(LocalDateTime.now());
        accessLog.setIpAddress(getClientIp(request));
        accessLog.setUserAgent(request.getHeader("User-Agent"));
        accessLog.setAction(action);
        accessLog.setStatus(status);
        
        return accessLogRepository.save(accessLog);
    }
    
    /**
     * Busca logs de acesso por usuário.
     *
     * @param user Usuário
     * @return Lista de logs de acesso
     */
    public List<AccessLog> findByUser(User user) {
        return accessLogRepository.findByUser(user);
    }
    
    /**
     * Busca logs de acesso por usuário e período.
     *
     * @param user Usuário
     * @param startTime Data/hora inicial
     * @param endTime Data/hora final
     * @return Lista de logs de acesso
     */
    public List<AccessLog> findByUserAndPeriod(User user, LocalDateTime startTime, LocalDateTime endTime) {
        return accessLogRepository.findByUserAndAccessTimeBetween(user, startTime, endTime);
    }
    
    /**
     * Busca logs de acesso por período.
     *
     * @param startTime Data/hora inicial
     * @param endTime Data/hora final
     * @return Lista de logs de acesso
     */
    public List<AccessLog> findByPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        return accessLogRepository.findByAccessTimeBetween(startTime, endTime);
    }
    
    /**
     * Busca logs de acesso por status.
     *
     * @param status Status
     * @return Lista de logs de acesso
     */
    public List<AccessLog> findByStatus(String status) {
        return accessLogRepository.findByStatus(status);
    }
    
    /**
     * Obtém todos os logs de acesso.
     *
     * @return Lista de logs de acesso
     */
    public List<AccessLog> findAll() {
        return accessLogRepository.findAll();
    }
    
    /**
     * Obtém o endereço IP do cliente.
     *
     * @param request Requisição HTTP
     * @return Endereço IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

