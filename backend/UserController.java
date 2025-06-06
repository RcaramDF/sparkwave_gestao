package com.sparkwave.login.controller;

import com.sparkwave.login.model.MessageResponse;
import com.sparkwave.login.model.User;
import com.sparkwave.login.model.UserDTO;
import com.sparkwave.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para gerenciamento de usuários na área de administração.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Obtém todos os usuários.
     *
     * @return Lista de usuários
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Obtém um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return Usuário
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo usuário.
     *
     * @param userDTO Dados do usuário
     * @return Usuário criado
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Nome de usuário já está em uso!"));
        }

        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Email já está em uso!"));
        }

        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(convertToDTO(user));
    }

    /**
     * Atualiza um usuário existente.
     *
     * @param id ID do usuário
     * @param userDTO Dados do usuário
     * @return Usuário atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(convertToDTO(user));
    }

    /**
     * Ativa ou desativa um usuário.
     *
     * @param id ID do usuário
     * @param active Status de ativação
     * @return Mensagem de resposta
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.updateUserStatus(id, active);
        String status = active ? "ativado" : "desativado";
        return ResponseEntity.ok(new MessageResponse("Usuário " + status + " com sucesso!"));
    }

    /**
     * Redefine a senha de um usuário.
     *
     * @param id ID do usuário
     * @param password Nova senha
     * @return Mensagem de resposta
     */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestParam String password) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.resetPassword(id, password);
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso!"));
    }

    /**
     * Exclui um usuário.
     *
     * @param id ID do usuário
     * @return Mensagem de resposta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("Usuário excluído com sucesso!"));
    }

    /**
     * Converte uma entidade User para um DTO.
     *
     * @param user Entidade User
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoles());
        return dto;
    }
}

