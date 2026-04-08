package dev.openhealth.OpenHealth.usuario.controller;

import dev.openhealth.OpenHealth.usuario.dto.UserResponseDTO;
import dev.openhealth.OpenHealth.usuario.entity.ModeloUsuario;
import dev.openhealth.OpenHealth.usuario.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:8080"})

public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UserResponseDTO> cadastrar(@RequestBody ModeloUsuario usuario) {
        try {
            UserResponseDTO response = service.cadastrar(usuario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody ModeloUsuario usuario) {
        try {
            UserResponseDTO response = service.login(usuario.getCpf(), usuario.getSenha());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserResponseDTO(e.getMessage()));
        }
    }

}
