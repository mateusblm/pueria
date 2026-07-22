package br.com.pueria.pueria.responsaveis.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.ConviteCuidador;
import br.com.pueria.pueria.responsaveis.dominio.ConviteCuidadorRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GerenciarCuidadoresUseCaseTest {
    private final Usuarios usuarios = new Usuarios();
    private final Criancas criancas = new Criancas();
    private final Vinculos vinculos = new Vinculos();
    private final Convites convites = new Convites();
    private final GerenciarCuidadoresUseCase useCase = new GerenciarCuidadoresUseCase(usuarios, criancas, vinculos, convites);

    @Test void responsavelPrincipalVinculaOutroUsuarioAtivo() {
        Usuario marina = usuarios.salvar(Usuario.cadastrarResponsavel("Marina", "marina@email.com", "hash"));
        Usuario rafael = usuarios.salvar(Usuario.cadastrarResponsavel("Rafael", "rafael@email.com", "hash"));
        Crianca lucas = criancas.salvar(Crianca.cadastrar("Lucas", LocalDate.of(2025, 1, 10), Sexo.MASCULINO, false, 39, 3200));
        vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(marina.getId(), lucas.getId(), Parentesco.MAE));

        var convite = useCase.convidar(lucas.getId(), marina.getEmail(), rafael.getEmail(), Parentesco.PAI);

        assertThat(convite.nomeCrianca()).isEqualTo("Lucas");
        assertThat(vinculos.usuarioPodeAcessarCrianca(rafael.getId(), lucas.getId())).isFalse();
        useCase.responderConvite(convite.id(), rafael.getEmail(), true);
        assertThat(vinculos.usuarioPodeAcessarCrianca(rafael.getId(), lucas.getId())).isTrue();
    }

    @Test void cuidadorNaoAdministraOutrosCuidadores() {
        Usuario marina = usuarios.salvar(Usuario.cadastrarResponsavel("Marina", "marina@email.com", "hash"));
        Usuario rafael = usuarios.salvar(Usuario.cadastrarResponsavel("Rafael", "rafael@email.com", "hash"));
        Usuario joana = usuarios.salvar(Usuario.cadastrarResponsavel("Joana", "joana@email.com", "hash"));
        Crianca lucas = criancas.salvar(Crianca.cadastrar("Lucas", LocalDate.of(2025, 1, 10), Sexo.MASCULINO, false, 39, 3200));
        vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(marina.getId(), lucas.getId(), Parentesco.MAE));
        vinculos.salvar(VinculoResponsavelCrianca.criarCuidador(rafael.getId(), lucas.getId(), Parentesco.PAI));

        assertThatThrownBy(() -> useCase.convidar(lucas.getId(), rafael.getEmail(), joana.getEmail(), Parentesco.OUTRO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test void naoDuplicaVinculoParaMesmaCrianca() {
        Usuario marina = usuarios.salvar(Usuario.cadastrarResponsavel("Marina", "marina@email.com", "hash"));
        Usuario rafael = usuarios.salvar(Usuario.cadastrarResponsavel("Rafael", "rafael@email.com", "hash"));
        Crianca lucas = criancas.salvar(Crianca.cadastrar("Lucas", LocalDate.of(2025, 1, 10), Sexo.MASCULINO, false, 39, 3200));
        vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(marina.getId(), lucas.getId(), Parentesco.MAE));
        useCase.convidar(lucas.getId(), marina.getEmail(), rafael.getEmail(), Parentesco.OUTRO);

        assertThatThrownBy(() -> useCase.convidar(lucas.getId(), marina.getEmail(), rafael.getEmail(), Parentesco.OUTRO))
                .isInstanceOf(RegraDominioException.class);
    }

    private static class Usuarios implements UsuarioRepositorio {
        final Map<UUID, Usuario> itens = new HashMap<>();
        public Usuario salvar(Usuario item) { itens.put(item.getId(), item); return item; }
        public Optional<Usuario> buscarPorId(UUID id) { return Optional.ofNullable(itens.get(id)); }
        public Optional<Usuario> buscarPorEmail(String email) { return itens.values().stream().filter(item -> item.getEmail().equalsIgnoreCase(email)).findFirst(); }
        public boolean existePorEmail(String email) { return buscarPorEmail(email).isPresent(); }
    }
    private static class Criancas implements CriancaRepositorio {
        final Map<UUID, Crianca> itens = new HashMap<>();
        public Crianca salvar(Crianca item) { itens.put(item.getId(), item); return item; }
        public Optional<Crianca> buscarPorId(UUID id) { return Optional.ofNullable(itens.get(id)); }
        public List<Crianca> listarPorIds(List<UUID> ids) { return ids.stream().map(itens::get).filter(java.util.Objects::nonNull).toList(); }
        public void removerPorId(UUID id) { itens.remove(id); }
    }
    private static class Vinculos implements VinculoResponsavelCriancaRepositorio {
        final List<VinculoResponsavelCrianca> itens = new ArrayList<>();
        public VinculoResponsavelCrianca salvar(VinculoResponsavelCrianca item) { itens.removeIf(atual -> atual.getId().equals(item.getId())); itens.add(item); return item; }
        public Optional<VinculoResponsavelCrianca> buscarPorId(UUID id) { return itens.stream().filter(item -> item.getId().equals(id)).findFirst(); }
        public boolean existeResponsavelPrincipal(UUID criancaId) { return itens.stream().anyMatch(item -> item.getCriancaId().equals(criancaId) && item.isPrincipal()); }
        public boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID criancaId) { return itens.stream().anyMatch(item -> item.getUsuarioId().equals(usuarioId) && item.getCriancaId().equals(criancaId)); }
        public boolean usuarioEhResponsavelPrincipal(UUID usuarioId, UUID criancaId) { return itens.stream().anyMatch(item -> item.getUsuarioId().equals(usuarioId) && item.getCriancaId().equals(criancaId) && item.isPrincipal()); }
        public List<UUID> listarCriancaIdsPorUsuario(UUID usuarioId) { return itens.stream().filter(item -> item.getUsuarioId().equals(usuarioId)).map(VinculoResponsavelCrianca::getCriancaId).toList(); }
        public List<VinculoResponsavelCrianca> listarPorCrianca(UUID criancaId) { return itens.stream().filter(item -> item.getCriancaId().equals(criancaId)).toList(); }
        public void removerPorCrianca(UUID criancaId) { itens.removeIf(item -> item.getCriancaId().equals(criancaId)); }
    }
    private static class Convites implements ConviteCuidadorRepositorio {
        final Map<UUID, ConviteCuidador> itens = new HashMap<>();
        public ConviteCuidador salvar(ConviteCuidador item) { itens.put(item.getId(), item); return item; }
        public Optional<ConviteCuidador> buscarPorId(UUID id) { return Optional.ofNullable(itens.get(id)); }
        public boolean existePendente(UUID criancaId, UUID usuarioId) { return itens.values().stream().anyMatch(item -> item.getCriancaId().equals(criancaId) && item.getConvidadoUsuarioId().equals(usuarioId) && item.getEstado().name().equals("PENDENTE")); }
        public List<ConviteCuidador> listarPendentesPorConvidado(UUID usuarioId) { return itens.values().stream().filter(item -> item.getConvidadoUsuarioId().equals(usuarioId) && item.getEstado().name().equals("PENDENTE")).toList(); }
        public List<ConviteCuidador> listarPendentesPorCrianca(UUID criancaId) { return itens.values().stream().filter(item -> item.getCriancaId().equals(criancaId) && item.getEstado().name().equals("PENDENTE")).toList(); }
    }
}
