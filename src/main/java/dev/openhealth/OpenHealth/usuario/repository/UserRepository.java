package dev.openhealth.OpenHealth.usuario.repository;

import dev.openhealth.OpenHealth.usuario.entity.ModeloUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<ModeloUsuario, Long> {

    Optional<ModeloUsuario> findByCpf(String cpf);
}
