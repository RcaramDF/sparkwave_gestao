package com.sparkwave.login.service;

import com.sparkwave.login.model.SignupRequest;
import com.sparkwave.login.model.User;
import com.sparkwave.login.model.UserDTO;
import com.sparkwave.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Serviço para gerenciar operações relacionadas a usuários.
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Registra um novo usuário.
     *
     * @param signupRequest Dados do usuário
     * @return Usuário criado
     */
    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFullName(signupRequest.getFullName());
        
        Set<String> roles = signupRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("USER");
        }
        
        user.setRoles(roles);
        user.setActive(true);
        
        user = userRepository.save(user);
        
        // Enviar e-mail de boas-vindas
        emailService.sendWelcomeEmail(user, null);
        
        return user;
    }
    
    /**
     * Cria um novo usuário a partir de um DTO.
     *
     * @param userDTO Dados do usuário
     * @return Usuário criado
     */
    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        
        // Armazenar a senha em texto plano para enviar por e-mail
        String plainPassword = userDTO.getPassword();
        user.setPassword(passwordEncoder.encode(plainPassword));
        
        user.setFullName(userDTO.getFullName());
        
        Set<String> roles = userDTO.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("USER");
        }
        
        user.setRoles(roles);
        user.setActive(userDTO.isActive());
        
        user = userRepository.save(user);
        
        // Enviar e-mail de boas-vindas com a senha
        emailService.sendWelcomeEmail(user, plainPassword);
        
        return user;
    }
    
    /**
     * Atualiza um usuário existente.
     *
     * @param id ID do usuário
     * @param userDTO Dados do usuário
     * @return Usuário atualizado
     */
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow();
        
        // Atualizar apenas os campos não nulos
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        if (userDTO.getFullName() != null) {
            user.setFullName(userDTO.getFullName());
        }
        
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            user.setRoles(userDTO.getRoles());
        }
        
        // Verificar se o status de ativação foi alterado
        if (user.isActive() != userDTO.isActive()) {
            user.setActive(userDTO.isActive());
            
            // Enviar e-mail de notificação de alteração de status
            emailService.sendAccountStatusEmail(user, userDTO.isActive());
        } else {
            user.setActive(userDTO.isActive());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Atualiza o status de ativação de um usuário.
     *
     * @param id ID do usuário
     * @param active Status de ativação
     */
    @Transactional
    public void updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id).orElseThrow();
        
        // Verificar se o status foi alterado
        if (user.isActive() != active) {
            user.setActive(active);
            userRepository.save(user);
            
            // Enviar e-mail de notificação de alteração de status
            emailService.sendAccountStatusEmail(user, active);
        }
    }
    
    /**
     * Redefine a senha de um usuário.
     *
     * @param id ID do usuário
     * @param password Nova senha
     */
    @Transactional
    public void resetPassword(Long id, String password) {
        User user = userRepository.findById(id).orElseThrow();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        // Enviar e-mail de notificação de redefinição de senha
        emailService.sendPasswordResetEmail(user, password);
    }
    
    /**
     * Exclui um usuário.
     *
     * @param id ID do usuário
     */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Verifica se um usuário existe pelo ID.
     *
     * @param id ID do usuário
     * @return true se o usuário existe, false caso contrário
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    
    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return Optional contendo o usuário, se encontrado
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Verifica se um nome de usuário já existe.
     *
     * @param username Nome de usuário
     * @return true se o nome de usuário já existe, false caso contrário
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Verifica se um email já existe.
     *
     * @param email Email
     * @return true se o email já existe, false caso contrário
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Busca um usuário pelo nome de usuário.
     *
     * @param username Nome de usuário
     * @return Optional contendo o usuário, se encontrado
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Lista todos os usuários.
     *
     * @return Lista de usuários
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
}

