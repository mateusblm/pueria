package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TokenRedefinicaoSenhaJpaRepository extends JpaRepository<TokenRedefinicaoSenhaJpaEntidade, UUID> {
    Optional<TokenRedefinicaoSenhaJpaEntidade> findByTokenHash(String tokenHash);
    boolean existsByUsuarioIdAndCriadoEmAfter(UUID usuarioId, LocalDateTime inicio);
    List<TokenRedefinicaoSenhaJpaEntidade> findByUsuarioIdAndUsadoEmIsNull(UUID usuarioId);
}
