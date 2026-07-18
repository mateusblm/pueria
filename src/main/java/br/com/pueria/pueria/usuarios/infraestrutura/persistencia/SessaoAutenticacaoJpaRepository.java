package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SessaoAutenticacaoJpaRepository extends JpaRepository<SessaoAutenticacaoJpaEntidade, UUID> {
    Optional<SessaoAutenticacaoJpaEntidade> findByTokenHash(String tokenHash);
    List<SessaoAutenticacaoJpaEntidade> findByUsuarioIdAndRevogadoEmIsNull(UUID usuarioId);
}
