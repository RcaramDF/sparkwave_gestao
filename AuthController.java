package com.sparkwave.login.controller;

import com.sparkwave.login.model.*;
import com.sparkwave.login.security.JwtUtils;
import com.sparkwave.login.service.AccessLogService;
import com.sparkwave.login.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para autenticação e registro de usuários.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AccessLogService accessLogService;
    
    @Value("${sparkwave.app.redirect.url}")
    private String redirectUrl;

    /**
     * Endpoint para autenticação de usuários.
     *
     * @param loginRequest Requisição de login
     * @param request Requisição HTTP
     * @return Resposta com token JWT
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toList());
            
            User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
            
            // Registrar o acesso bem-sucedido
            accessLogService.logAccess(user, "LOGIN", "SUCCESS", request);
            
            return ResponseEntity.ok(new JwtResponse(jwt, 
                                                     user.getId(), 
                                                     user.getUsername(), 
                                                     user.getEmail(), 
                                                     roles,
                                                     redirectUrl));
        } catch (AuthenticationException e) {
            // Tentar encontrar o usuário para registrar a tentativa de login
            userService.findByUsername(loginRequest.getUsername()).ifPresent(user -> {
                accessLogService.logAccess(user, "LOGIN", "FAILED", request);
            });
            
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Credenciais inválidas!"));
        }
    }

    /**
     * Endpoint para registro de usuários.
     *
     * @param signupRequest Requisição de registro
     * @param request Requisição HTTP
     * @return Mensagem de resposta
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest, HttpServletRequest request) {
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Nome de usuário já está em uso!"));
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Email já está em uso!"));
        }

        User user = userService.registerUser(signupRequest);
        
        // Registrar o registro bem-sucedido
        accessLogService.logAccess(user, "REGISTER", "SUCCESS", request);

        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }
    
    /**
     * Endpoint para logout de usuários.
     *
     * @param request Requisição HTTP
     * @return Mensagem de resposta
     */
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
            
            if (user != null) {
                // Registrar o logout
                accessLogService.logAccess(user, "LOGOUT", "SUCCESS", request);
            }
            
            // Limpar o contexto de segurança
            SecurityContextHolder.clearContext();
        }
        
        return ResponseEntity.ok(new MessageResponse("Logout realizado com sucesso!"));
    }
}

