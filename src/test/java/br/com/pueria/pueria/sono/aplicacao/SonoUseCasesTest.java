package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.sono.dominio.*;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SonoUseCasesTest {

    @Test
    void deveRegistrarSonoParaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(8));

        RegistroSonoDetalhado detalhado = ambiente.registrarUseCase.executar(new RegistroSonoComando(
                crianca.getId(),
                "mateus@email.com",
                dados(LocalTime.of(20, 0), LocalTime.of(6, 0), 180)
        ));

        assertEquals(crianca.getId(), detalhado.registro().getCriancaId());
        assertEquals("FAIXA_ESPERADA", detalhado.analise().classificacaoDuracao());
    }

    @Test
    void deveApontarSonoAbaixoDaFaixaSemDiagnosticar() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(18));

        RegistroSonoDetalhado detalhado = ambiente.registrarUseCase.executar(new RegistroSonoComando(
                crianca.getId(),
                "mateus@email.com",
                dados(LocalTime.of(23, 0), LocalTime.of(6, 0), 30)
        ));

        assertEquals("ABAIXO_DA_FAIXA", detalhado.analise().classificacaoDuracao());
        assertTrue(detalhado.analise().conversaConsulta().stream().anyMatch(texto -> texto.contains("abaixo da faixa esperada")));
    }

    @Test
    void naoDeveRegistrarSonoParaCriancaSemVinculo() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(8));
        ambiente.cadastrarResponsavel("outro@email.com");

        assertThrows(RecursoNaoEncontradoException.class, () -> ambiente.registrarUseCase.executar(new RegistroSonoComando(
                crianca.getId(),
                "outro@email.com",
                dados(LocalTime.of(20, 0), LocalTime.of(6, 0), 180)
        )));
    }

    @Test
    void deveAtualizarRegistroDaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com", LocalDate.now().minusMonths(8));
        RegistroSono registro = ambiente.registros.salvar(RegistroSono.registrar(crianca.getId(), dados(LocalTime.of(20, 0), LocalTime.of(6, 0), 180)));

        RegistroSonoDetalhado atualizado = ambiente.atualizarUseCase.executar(new AtualizarRegistroSonoComando(
                crianca.getId(),
                registro.getId(),
                "mateus@email.com",
                dados(LocalTime.of(21, 0), LocalTime.of(6, 30), 120)
        ));

        assertEquals(LocalTime.of(21, 0), atualizado.registro().getHorarioDormiu());
    }

    private static DadosSono dados(LocalTime dormiu, LocalTime acordou, Integer minutosCochilos) {
        return new DadosSono(
                LocalDate.now(),
                dormiu,
                acordou,
                2,
                minutosCochilos,
                1,
                false,
                true,
                false,
                SuperficieSono.BERCO,
                AmbienteSono.QUARTO_DOS_RESPONSAVEIS,
                List.of(),
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null
        );
    }

    private static class Ambiente {
        private final CriancaRepositorioEmMemoria criancas = new CriancaRepositorioEmMemoria();
        private final UsuarioRepositorioEmMemoria usuarios = new UsuarioRepositorioEmMemoria();
        private final VinculoRepositorioEmMemoria vinculos = new VinculoRepositorioEmMemoria();
        private final RegistroRepositorioEmMemoria registros = new RegistroRepositorioEmMemoria();
        private final SonoAcesso acesso = new SonoAcesso(criancas, usuarios, vinculos);
        private final AnaliseSonoService analiseService = new AnaliseSonoService();
        private final RegistrarSonoUseCase registrarUseCase = new RegistrarSonoUseCase(acesso, registros, analiseService);
        private final AtualizarSonoUseCase atualizarUseCase = new AtualizarSonoUseCase(acesso, registros, analiseService);

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

    private static class RegistroRepositorioEmMemoria implements RegistroSonoRepositorio {
        private final Map<UUID, RegistroSono> registros = new HashMap<>();

        @Override
        public RegistroSono salvar(RegistroSono registro) {
            registros.put(registro.getId(), registro);
            return registro;
        }

        @Override
        public Optional<RegistroSono> buscarPorId(UUID id) {
            return Optional.ofNullable(registros.get(id));
        }

        @Override
        public List<RegistroSono> listarPorCrianca(UUID criancaId) {
            return registros.values().stream()
                    .filter(registro -> registro.getCriancaId().equals(criancaId))
                    .sorted(Comparator.comparing(RegistroSono::getDataRegistro))
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
