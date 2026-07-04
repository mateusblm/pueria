package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.consentimentos.dominio.Consentimento;
import br.com.pueria.pueria.consentimentos.dominio.ConsentimentoRepositorio;
import br.com.pueria.pueria.consentimentos.dominio.TipoConsentimento;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriancaUseCasesTest {

    @Test
    void deveCriarCriancaComResponsavelEConsentimento() {
        Ambiente ambiente = new Ambiente();
        Usuario responsavel = ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");

        Crianca crianca = ambiente.criarUseCase.executar(new CriarCriancaComando(
                "mateus@email.com",
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200,
                Parentesco.PAI,
                true,
                "2026.07"
        ));

        assertNotNull(crianca.getId());
        assertEquals("Ana", crianca.getNome());
        assertEquals(1, ambiente.criancas.quantidade());
        assertTrue(ambiente.vinculos.usuarioPodeAcessarCrianca(responsavel.getId(), crianca.getId()));
        assertTrue(ambiente.consentimentos.existeAceite(
                responsavel.getId(),
                crianca.getId(),
                TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL
        ));
    }

    @Test
    void naoDeveCriarCriancaSemConsentimentoAceito() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");

        assertThrows(RegraDominioException.class, () -> ambiente.criarUseCase.executar(new CriarCriancaComando(
                "mateus@email.com",
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200,
                Parentesco.PAI,
                false,
                "2026.07"
        )));
    }

    @Test
    void deveListarApenasCriancasVinculadasAoResponsavel() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");
        ambiente.cadastrarResponsavel("Outro", "outro@email.com");

        Crianca ana = ambiente.criarCriancaPadrao("mateus@email.com", "Ana");
        ambiente.criarCriancaPadrao("outro@email.com", "Pedro");

        List<Crianca> criancas = ambiente.listarUseCase.executar("mateus@email.com");

        assertEquals(1, criancas.size());
        assertEquals(ana.getId(), criancas.getFirst().getId());
    }

    @Test
    void deveBuscarCriancaVinculadaAoResponsavel() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");
        Crianca ana = ambiente.criarCriancaPadrao("mateus@email.com", "Ana");

        Crianca encontrada = ambiente.buscarUseCase.executar(ana.getId(), "mateus@email.com");

        assertEquals(ana.getId(), encontrada.getId());
    }

    @Test
    void naoDeveBuscarCriancaDeOutroResponsavel() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");
        ambiente.cadastrarResponsavel("Outro", "outro@email.com");
        Crianca ana = ambiente.criarCriancaPadrao("mateus@email.com", "Ana");

        assertThrows(RecursoNaoEncontradoException.class,
                () -> ambiente.buscarUseCase.executar(ana.getId(), "outro@email.com"));
    }

    @Test
    void deveAtualizarCriancaVinculadaAoResponsavel() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");
        Crianca ana = ambiente.criarCriancaPadrao("mateus@email.com", "Ana");

        Crianca atualizada = ambiente.atualizarUseCase.executar(new AtualizarCriancaComando(
                ana.getId(),
                "mateus@email.com",
                "Ana Clara",
                LocalDate.of(2024, 1, 12),
                Sexo.FEMININO,
                false,
                40,
                3300
        ));

        assertEquals(ana.getId(), atualizada.getId());
        assertEquals("Ana Clara", atualizada.getNome());
        assertEquals(40, atualizada.getSemanasGestacionais());
        assertEquals(3300, atualizada.getPesoNascimentoGramas());
    }

    @Test
    void naoDeveAtualizarCriancaDeOutroResponsavel() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");
        ambiente.cadastrarResponsavel("Outro", "outro@email.com");
        Crianca ana = ambiente.criarCriancaPadrao("mateus@email.com", "Ana");

        assertThrows(RecursoNaoEncontradoException.class, () -> ambiente.atualizarUseCase.executar(new AtualizarCriancaComando(
                ana.getId(),
                "outro@email.com",
                "Ana Clara",
                LocalDate.of(2024, 1, 12),
                Sexo.FEMININO,
                false,
                40,
                3300
        )));
    }

    @Test
    void deveRemoverCriancaVinculadaAoResponsavel() {
        Ambiente ambiente = new Ambiente();
        ambiente.cadastrarResponsavel("Mateus", "mateus@email.com");
        Crianca ana = ambiente.criarCriancaPadrao("mateus@email.com", "Ana");

        ambiente.removerUseCase.executar(ana.getId(), "mateus@email.com");

        assertFalse(ambiente.criancas.buscarPorId(ana.getId()).isPresent());
        assertFalse(ambiente.vinculos.usuarioPodeAcessarCrianca(
                ambiente.usuarios.buscarPorEmail("mateus@email.com").orElseThrow().getId(),
                ana.getId()
        ));
    }

    private static class Ambiente {
        private final CriancaRepositorioEmMemoria criancas = new CriancaRepositorioEmMemoria();
        private final UsuarioRepositorioEmMemoria usuarios = new UsuarioRepositorioEmMemoria();
        private final VinculoRepositorioEmMemoria vinculos = new VinculoRepositorioEmMemoria();
        private final ConsentimentoRepositorioEmMemoria consentimentos = new ConsentimentoRepositorioEmMemoria();

        private final CriarCriancaUseCase criarUseCase = new CriarCriancaUseCase(criancas, usuarios, vinculos, consentimentos);
        private final BuscarCriancaUseCase buscarUseCase = new BuscarCriancaUseCase(criancas, usuarios, vinculos);
        private final ListarCriancasUseCase listarUseCase = new ListarCriancasUseCase(criancas, usuarios, vinculos);
        private final AtualizarCriancaUseCase atualizarUseCase = new AtualizarCriancaUseCase(criancas, usuarios, vinculos);
        private final RemoverCriancaUseCase removerUseCase = new RemoverCriancaUseCase(criancas, usuarios, vinculos, consentimentos);

        Usuario cadastrarResponsavel(String nome, String email) {
            Usuario usuario = Usuario.cadastrarResponsavel(nome, email, "senha-criptografada");
            usuarios.salvar(usuario);
            return usuario;
        }

        Crianca criarCriancaPadrao(String email, String nome) {
            return criarUseCase.executar(new CriarCriancaComando(
                    email,
                    nome,
                    LocalDate.of(2024, 1, 10),
                    Sexo.FEMININO,
                    false,
                    39,
                    3200,
                    Parentesco.PAI,
                    true,
                    "2026.07"
            ));
        }
    }

    private static class CriancaRepositorioEmMemoria implements CriancaRepositorio {
        private final Map<UUID, Crianca> criancas = new HashMap<>();

        @Override
        public Crianca salvar(Crianca crianca) {
            criancas.put(crianca.getId(), crianca);
            return crianca;
        }

        @Override
        public Optional<Crianca> buscarPorId(UUID id) {
            return Optional.ofNullable(criancas.get(id));
        }

        @Override
        public List<Crianca> listarPorIds(List<UUID> ids) {
            return criancas.values().stream()
                    .filter(crianca -> ids.contains(crianca.getId()))
                    .sorted(java.util.Comparator.comparing(Crianca::getNome))
                    .toList();
        }

        @Override
        public void removerPorId(UUID id) {
            criancas.remove(id);
        }

        int quantidade() {
            return criancas.size();
        }
    }

    private static class UsuarioRepositorioEmMemoria implements UsuarioRepositorio {
        private final Map<UUID, Usuario> usuariosPorId = new HashMap<>();
        private final Map<String, Usuario> usuariosPorEmail = new HashMap<>();

        @Override
        public Usuario salvar(Usuario usuario) {
            usuariosPorId.put(usuario.getId(), usuario);
            usuariosPorEmail.put(usuario.getEmail(), usuario);
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return Optional.ofNullable(usuariosPorId.get(id));
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.ofNullable(usuariosPorEmail.get(email));
        }

        @Override
        public boolean existePorEmail(String email) {
            return usuariosPorEmail.containsKey(email);
        }
    }

    private static class VinculoRepositorioEmMemoria implements VinculoResponsavelCriancaRepositorio {
        private final Map<UUID, VinculoResponsavelCrianca> vinculos = new HashMap<>();

        @Override
        public VinculoResponsavelCrianca salvar(VinculoResponsavelCrianca vinculo) {
            vinculos.put(vinculo.getId(), vinculo);
            return vinculo;
        }

        @Override
        public Optional<VinculoResponsavelCrianca> buscarPorId(UUID id) {
            return Optional.ofNullable(vinculos.get(id));
        }

        @Override
        public boolean existeResponsavelPrincipal(UUID criancaId) {
            return vinculos.values().stream()
                    .anyMatch(vinculo -> vinculo.getCriancaId().equals(criancaId) && vinculo.isPrincipal());
        }

        @Override
        public boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID criancaId) {
            return vinculos.values().stream()
                    .anyMatch(vinculo -> vinculo.getUsuarioId().equals(usuarioId) && vinculo.getCriancaId().equals(criancaId));
        }

        @Override
        public List<UUID> listarCriancaIdsPorUsuario(UUID usuarioId) {
            return vinculos.values().stream()
                    .filter(vinculo -> vinculo.getUsuarioId().equals(usuarioId))
                    .map(VinculoResponsavelCrianca::getCriancaId)
                    .toList();
        }

        @Override
        public void removerPorCrianca(UUID criancaId) {
            vinculos.entrySet().removeIf(entry -> entry.getValue().getCriancaId().equals(criancaId));
        }
    }

    private static class ConsentimentoRepositorioEmMemoria implements ConsentimentoRepositorio {
        private final Map<UUID, Consentimento> consentimentos = new HashMap<>();

        @Override
        public Consentimento salvar(Consentimento consentimento) {
            consentimentos.put(consentimento.getId(), consentimento);
            return consentimento;
        }

        @Override
        public Optional<Consentimento> buscarPorId(UUID id) {
            return Optional.ofNullable(consentimentos.get(id));
        }

        @Override
        public boolean existeAceite(UUID usuarioId, UUID criancaId, TipoConsentimento tipo) {
            return consentimentos.values().stream()
                    .anyMatch(consentimento -> consentimento.getUsuarioId().equals(usuarioId)
                            && consentimento.getCriancaId().equals(criancaId)
                            && consentimento.getTipo() == tipo
                            && consentimento.isAceito());
        }

        @Override
        public void removerPorCrianca(UUID criancaId) {
            consentimentos.entrySet().removeIf(entry -> entry.getValue().getCriancaId().equals(criancaId));
        }
    }
}
