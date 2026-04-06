package dev.openhealth.OpenHealth.usuario.controller;

import dev.openhealth.OpenHealth.usuario.entity.ModeloUsuario;
import dev.openhealth.OpenHealth.usuario.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")

public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ModeloUsuario cadastrar(@RequestBody ModeloUsuario usuario) {
        return service.cadastrar(usuario);
    }

    @PostMapping("/login")
    public ModeloUsuario login(@RequestBody ModeloUsuario usuario) {
        return service.login(usuario.getCpf(), usuario.getSenha());
    }

}
