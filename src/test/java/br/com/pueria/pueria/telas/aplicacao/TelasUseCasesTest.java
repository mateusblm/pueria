package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.telas.dominio.*;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TelasUseCasesTest {

    @Test
    void deveRegistrarTelasParaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(30));

        RegistroTelasDetalhado detalhado = ambiente.registrarUseCase.executar(new RegistroTelasComando(
                crianca.getId(),
                "mateus@email.com",
                dados(30, 60, TipoConteudoTela.EDUCATIVO_INTERATIVO)
        ));

        assertEquals(crianca.getId(), detalhado.registro().getCriancaId());
        assertEquals("DENTRO_DA_REFERENCIA", detalhado.analise().classificacaoTempo());
    }

    @Test
    void deveOrientarSemDiagnosticarQuandoMenorDeDoisAnosTemTelaDeRotina() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(10));

        RegistroTelasDetalhado detalhado = ambiente.registrarUseCase.executar(new RegistroTelasComando(
                crianca.getId(),
                "mateus@email.com",
                dados(20, 30, TipoConteudoTela.VIDEO_PASSIVO)
        ));

        assertEquals("ACIMA_DA_REFERENCIA", detalhado.analise().classificacaoTempo());
        assertTrue(detalhado.analise().conversaConsulta().stream().anyMatch(texto -> texto.contains("menores de 2 anos")));
        assertTrue(detalhado.analise().resumo().contains("recomendacao"));
    }

    @Test
    void deveConsiderarContextoDeSonoRefeicaoEAcalmar() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(36));

        RegistroTelasDetalhado detalhado = ambiente.registrarUseCase.executar(new RegistroTelasComando(
                crianca.getId(),
                "mateus@email.com",
                dadosComContexto(90, 120)
        ));

        assertEquals("ACIMA_DA_REFERENCIA", detalhado.analise().classificacaoTempo());
        assertTrue(detalhado.analise().habitosApoio().stream().anyMatch(texto -> texto.contains("Refeicoes sem tela")));
        assertTrue(detalhado.analise().habitosApoio().stream().anyMatch(texto -> texto.contains("perto do sono")));
        assertTrue(detalhado.analise().habitosApoio().stream().anyMatch(texto -> texto.contains("para acalmar")));
    }

    @Test
    void naoDeveRegistrarTelasParaCriancaSemVinculo() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(30));
        ambiente.cadastrarResponsavel("outro@email.com");

        assertThrows(RecursoNaoEncontradoException.class, () -> ambiente.registrarUseCase.executar(new RegistroTelasComando(
                crianca.getId(),
                "outro@email.com",
                dados(30, 40, TipoConteudoTela.VIDEO_PASSIVO)
        )));
    }

    @Test
    void deveAtualizarRegistroDaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(30));
        RegistroTelas registro = ambiente.registros.salvar(RegistroTelas.registrar(crianca.getId(), dados(30, 40, TipoConteudoTela.VIDEO_PASSIVO)));

        RegistroTelasDetalhado atualizado = ambiente.atualizarUseCase.executar(new AtualizarRegistroTelasComando(
                crianca.getId(),
                registro.getId(),
                "mateus@email.com",
                dados(10, 20, TipoConteudoTela.EDUCATIVO_INTERATIVO)
        ));

        assertEquals(10, atualizado.registro().getMinutosDiaSemana());
        assertEquals("DENTRO_DA_REFERENCIA", atualizado.analise().classificacaoTempo());
    }

    private static DadosTelas dados(Integer semana, Integer fimSemana, TipoConteudoTela tipo) {
        return new DadosTelas(
                LocalDate.now(),
                semana,
                fimSemana,
                tipo,
                List.of(),
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                null
        );
    }

    private static DadosTelas dadosComContexto(Integer semana, Integer fimSemana) {
        return new DadosTelas(
                LocalDate.now(),
                semana,
                fimSemana,
                TipoConteudoTela.VIDEO_PASSIVO,
                List.of(),
                true,
                true,
                true,
                true,
                true,
                false,
                false,
                false,
                true,
                true,
                true,
                false,
                false,
                true,
                false,
                "Familia preocupada com uso recente."
        );
    }

    private static class Ambiente {
        private final CriancaRepositorioEmMemoria criancas = new CriancaRepositorioEmMemoria();
        private final UsuarioRepositorioEmMemoria usuarios = new UsuarioRepositorioEmMemoria();
        private final VinculoRepositorioEmMemoria vinculos = new VinculoRepositorioEmMemoria();
        private final RegistroRepositorioEmMemoria registros = new RegistroRepositorioEmMemoria();
        private final TelasAcesso acesso = new TelasAcesso(criancas, usuarios, vinculos);
        private final AnaliseTelasService analiseService = new AnaliseTelasService();
        private final RegistrarTelasUseCase registrarUseCase = new RegistrarTelasUseCase(acesso, registros, analiseService);
        private final AtualizarTelasUseCase atualizarUseCase = new AtualizarTelasUseCase(acesso, registros, analiseService);

        Usuario cadastrarResponsavel(String email) {
            Usuario usuario = Usuario.cadastrarResponsavel("Responsavel", email, "senha-criptografada");
            usuarios.salvar(usuario);
            return usuario;
        }

        Crianca criarCriancaVinculada(String email, LocalDate nascimento) {
            Usuario responsavel = usuarios.buscarPorEmail(email).orElseGet(() -> cadastrarResponsavel(email));
            Crianca crianca = Crianca.cadastrar("Ana", nascimento, Sexo.FEMININO, false, 39, 3200);
            criancas.salvar(crianca);
            vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(responsavel.getId(), crianca.getId(), Parentesco.PAI));
            return crianca;
        }
    }

    private static class RegistroRepositorioEmMemoria implements RegistroTelasRepositorio {
        private final Map<UUID, RegistroTelas> registros = new HashMap<>();

        @Override
        public RegistroTelas salvar(RegistroTelas registro) {
            registros.put(registro.getId(), registro);
            return registro;
        }

        @Override
        public Optional<RegistroTelas> buscarPorId(UUID id) {
            return Optional.ofNullable(registros.get(id));
        }

        @Override
        public List<RegistroTelas> listarPorCrianca(UUID criancaId) {
            return registros.values().stream()
                    .filter(registro -> registro.getCriancaId().equals(criancaId))
                    .sorted(Comparator.comparing(RegistroTelas::getDataRegistro))
                    .toList();
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
                    .toList();
        }

        @Override
        public void removerPorId(UUID id) {
            criancas.remove(id);
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
}
