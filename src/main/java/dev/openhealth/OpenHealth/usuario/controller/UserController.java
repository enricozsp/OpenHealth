package dev.openhealth.OpenHealth.usuario.controller;

import dev.openhealth.OpenHealth.usuario.dto.UserResponseDTO;
import dev.openhealth.OpenHealth.usuario.entity.ModeloUsuario;
import dev.openhealth.OpenHealth.usuario.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequestMapping("/usuarios")

public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public UserResponseDTO cadastrar(@RequestBody ModeloUsuario usuario) {
        return service.cadastrar(usuario);
    }

    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody ModeloUsuario usuario) {
        return service.login(usuario.getCpf(), usuario.getSenha());
    }

}
