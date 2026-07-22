package br.com.pueria.pueria.responsaveis.infraestrutura.persistencia;

import br.com.pueria.pueria.responsaveis.dominio.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConviteCuidadorRepositorioJpa implements ConviteCuidadorRepositorio {
    private final ConviteCuidadorJpaRepository repository;
    public ConviteCuidadorRepositorioJpa(ConviteCuidadorJpaRepository repository) { this.repository = repository; }
    public ConviteCuidador salvar(ConviteCuidador convite) { return paraDominio(repository.save(paraEntidade(convite))); }
    public Optional<ConviteCuidador> buscarPorId(UUID id) { return repository.findById(id).map(this::paraDominio); }
    public boolean existePendente(UUID criancaId, UUID usuarioId) { return repository.existsByCriancaIdAndConvidadoUsuarioIdAndEstado(criancaId, usuarioId, EstadoConviteCuidador.PENDENTE); }
    public List<ConviteCuidador> listarPendentesPorConvidado(UUID usuarioId) { return repository.findByConvidadoUsuarioIdAndEstadoOrderByCriadoEmDesc(usuarioId, EstadoConviteCuidador.PENDENTE).stream().map(this::paraDominio).toList(); }
    public List<ConviteCuidador> listarPendentesPorCrianca(UUID criancaId) { return repository.findByCriancaIdAndEstadoOrderByCriadoEmDesc(criancaId, EstadoConviteCuidador.PENDENTE).stream().map(this::paraDominio).toList(); }
    private ConviteCuidadorJpaEntidade paraEntidade(ConviteCuidador c) { var e = new ConviteCuidadorJpaEntidade(); e.setId(c.getId()); e.setCriancaId(c.getCriancaId()); e.setConvidadoUsuarioId(c.getConvidadoUsuarioId()); e.setCriadoPorUsuarioId(c.getCriadoPorUsuarioId()); e.setParentesco(c.getParentesco()); e.setEstado(c.getEstado()); e.setCriadoEm(c.getCriadoEm()); e.setRespondidoEm(c.getRespondidoEm()); return e; }
    private ConviteCuidador paraDominio(ConviteCuidadorJpaEntidade e) { return ConviteCuidador.restaurar(e.getId(), e.getCriancaId(), e.getConvidadoUsuarioId(), e.getCriadoPorUsuarioId(), e.getParentesco(), e.getEstado(), e.getCriadoEm(), e.getRespondidoEm()); }
}
