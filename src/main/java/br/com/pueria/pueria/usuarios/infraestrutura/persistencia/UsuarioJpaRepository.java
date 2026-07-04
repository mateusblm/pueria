package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntidade, UUID> {

    Optional<UsuarioJpaEntidade> findByEmail(String email);

    boolean existsByEmail(String email);
}
