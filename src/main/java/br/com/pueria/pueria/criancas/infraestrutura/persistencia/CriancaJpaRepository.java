package br.com.pueria.pueria.criancas.infraestrutura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface CriancaJpaRepository extends JpaRepository<CriancaJpaEntidade, UUID> {
}
