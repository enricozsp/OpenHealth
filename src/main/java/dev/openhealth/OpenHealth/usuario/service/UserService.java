package dev.openhealth.OpenHealth.usuario.service;

import dev.openhealth.OpenHealth.usuario.entity.ModeloUsuario;
import dev.openhealth.OpenHealth.usuario.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public ModeloUsuario cadastrar(ModeloUsuario usuario) {

        if (repository.findByCpf(usuario.getCpf()).isPresent()) {
            throw new RuntimeException("CPF ja cadastrado");
        }

        String senhaCriptografada = encoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        return repository.save(usuario);
    }

    public ModeloUsuario login(String cpf, String senha) {
        ModeloUsuario usuario = repository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!encoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return usuario;
    }
}
