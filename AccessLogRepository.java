package com.sparkwave.login.repository;

import com.sparkwave.login.model.AccessLog;
import com.sparkwave.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados relacionadas a logs de acesso.
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    
    /**
     * Busca logs de acesso por usuário.
     * 
     * @param user Usuário
     * @return Lista de logs de acesso
     */
    List<AccessLog> findByUser(User user);
    
    /**
     * Busca logs de acesso por usuário e período.
     * 
     * @param user Usuário
     * @param startTime Data/hora inicial
     * @param endTime Data/hora final
     * @return Lista de logs de acesso
     */
    List<AccessLog> findByUserAndAccessTimeBetween(User user, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Busca logs de acesso por período.
     * 
     * @param startTime Data/hora inicial
     * @param endTime Data/hora final
     * @return Lista de logs de acesso
     */
    List<AccessLog> findByAccessTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Busca logs de acesso por status.
     * 
     * @param status Status
     * @return Lista de logs de acesso
     */
    List<AccessLog> findByStatus(String status);
}

