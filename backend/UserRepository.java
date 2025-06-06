package com.sparkwave.login.repository;

import com.sparkwave.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados relacionadas a usuários.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca um usuário pelo nome de usuário.
     * 
     * @param username Nome de usuário
     * @return Optional contendo o usuário, se encontrado
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Verifica se existe um usuário com o nome de usuário fornecido.
     * 
     * @param username Nome de usuário
     * @return true se o usuário existir, false caso contrário
     */
    Boolean existsByUsername(String username);
    
    /**
     * Verifica se existe um usuário com o email fornecido.
     * 
     * @param email Email
     * @return true se o email existir, false caso contrário
     */
    Boolean existsByEmail(String email);
}

