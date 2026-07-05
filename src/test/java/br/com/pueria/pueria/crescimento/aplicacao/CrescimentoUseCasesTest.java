package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import br.com.pueria.pueria.crescimento.dominio.OrigemMedidaCrescimento;
import br.com.pueria.pueria.crescimento.dominio.CurvaOmsCrescimentoService;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CrescimentoUseCasesTest {

    @Test
    void deveRegistrarMedidaParaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com");

        MedidaCrescimento medida = ambiente.registrarUseCase.executar(new RegistrarMedidaCrescimentoComando(
                crianca.getId(),
                "mateus@email.com",
                LocalDate.now(),
                new BigDecimal("7.35"),
                new BigDecimal("68.5"),
                null,
                OrigemMedidaCrescimento.CONSULTA,
                "consulta"
        ));

        assertEquals(crianca.getId(), medida.getCriancaId());
        assertEquals(1, ambiente.medidas.listarPorCrianca(crianca.getId()).size());
    }

    @Test
    void naoDeveRegistrarMedidaParaCriancaSemVinculo() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com");
        ambiente.cadastrarResponsavel("outro@email.com");

        assertThrows(RecursoNaoEncontradoException.class, () -> ambiente.registrarUseCase.executar(new RegistrarMedidaCrescimentoComando(
                crianca.getId(),
                "outro@email.com",
                LocalDate.now(),
                new BigDecimal("7.35"),
                null,
                null,
                OrigemMedidaCrescimento.CASA,
                null
        )));
    }

    @Test
    void deveListarApenasMedidasDaCrianca() {
        Ambiente ambiente = new Ambiente();
        Crianca ana = ambiente.criarCriancaVinculada("mateus@email.com");
        Crianca bia = ambiente.criarCriancaVinculada("mateus@email.com");
        ambiente.salvarMedida(ana.getId(), "7.35");
        ambiente.salvarMedida(bia.getId(), "8.10");

        List<MedidaCrescimento> medidas = ambiente.listarUseCase.executar(ana.getId(), "mateus@email.com");

        assertEquals(1, medidas.size());
        assertEquals(ana.getId(), medidas.getFirst().getCriancaId());
    }

    @Test
    void deveAtualizarMedidaDaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com");
        MedidaCrescimento medida = ambiente.salvarMedida(crianca.getId(), "7.35");

        MedidaCrescimento atualizada = ambiente.atualizarUseCase.executar(new AtualizarMedidaCrescimentoComando(
                crianca.getId(),
                medida.getId(),
                "mateus@email.com",
                LocalDate.now(),
                new BigDecimal("7.70"),
                new BigDecimal("69.1"),
                null,
                OrigemMedidaCrescimento.CONSULTA,
                "medida revisada"
        ));

        assertEquals(medida.getId(), atualizada.getId());
        assertEquals(new BigDecimal("7.70"), atualizada.getPesoKg());
        assertEquals(new BigDecimal("69.1"), atualizada.getComprimentoCm());
    }

    @Test
    void naoDeveAtualizarMedidaDeOutraCrianca() {
        Ambiente ambiente = new Ambiente();
        Crianca ana = ambiente.criarCriancaVinculada("mateus@email.com");
        Crianca bia = ambiente.criarCriancaVinculada("mateus@email.com");
        MedidaCrescimento medidaDaBia = ambiente.salvarMedida(bia.getId(), "8.10");

        assertThrows(RecursoNaoEncontradoException.class, () -> ambiente.atualizarUseCase.executar(new AtualizarMedidaCrescimentoComando(
                ana.getId(),
                medidaDaBia.getId(),
                "mateus@email.com",
                LocalDate.now(),
                new BigDecimal("7.70"),
                null,
                null,
                OrigemMedidaCrescimento.CASA,
                null
        )));
    }

    @Test
    void deveRemoverMedidaDaCriancaVinculada() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com");
        MedidaCrescimento medida = ambiente.salvarMedida(crianca.getId(), "7.35");

        ambiente.removerUseCase.executar(crianca.getId(), medida.getId(), "mateus@email.com");

        assertFalse(ambiente.medidas.buscarPorId(medida.getId()).isPresent());
    }

    @Test
    void deveListarAvaliacoesOmsDasMedidasDaCrianca() {
        Ambiente ambiente = new Ambiente();
        Crianca crianca = ambiente.criarCriancaVinculada("mateus@email.com");
        MedidaCrescimento medida = MedidaCrescimento.registrar(
                crianca.getId(),
                LocalDate.of(2024, 1, 10),
                new BigDecimal("3.2322"),
                new BigDecimal("49.1477"),
                new BigDecimal("33.8787"),
                OrigemMedidaCrescimento.CONSULTA,
                null
        );
        ambiente.medidas.salvar(medida);

        List<AvaliacaoCurvaCrescimento> avaliacoes = ambiente.listarAvaliacoesCurvaUseCase.executar(crianca.getId(), "mateus@email.com");

        assertEquals(1, avaliacoes.size());
        assertEquals(3, avaliacoes.getFirst().resultados().size());
        assertEquals(0.0, avaliacoes.getFirst().resultados().getFirst().zScore(), 0.001);
    }

    private static class Ambiente {
        private final CriancaRepositorioEmMemoria criancas = new CriancaRepositorioEmMemoria();
        private final UsuarioRepositorioEmMemoria usuarios = new UsuarioRepositorioEmMemoria();
        private final VinculoRepositorioEmMemoria vinculos = new VinculoRepositorioEmMemoria();
        private final MedidaRepositorioEmMemoria medidas = new MedidaRepositorioEmMemoria();
        private final CrescimentoAcesso acesso = new CrescimentoAcesso(criancas, usuarios, vinculos);
        private final CurvaOmsCrescimentoService curvaOmsService = new CurvaOmsCrescimentoService();

        private final RegistrarMedidaCrescimentoUseCase registrarUseCase = new RegistrarMedidaCrescimentoUseCase(acesso, medidas);
        private final ListarMedidasCrescimentoUseCase listarUseCase = new ListarMedidasCrescimentoUseCase(acesso, medidas);
        private final ListarAvaliacoesCurvaCrescimentoUseCase listarAvaliacoesCurvaUseCase = new ListarAvaliacoesCurvaCrescimentoUseCase(acesso, medidas, curvaOmsService);
        private final AtualizarMedidaCrescimentoUseCase atualizarUseCase = new AtualizarMedidaCrescimentoUseCase(acesso, medidas);
        private final RemoverMedidaCrescimentoUseCase removerUseCase = new RemoverMedidaCrescimentoUseCase(acesso, medidas);

        Usuario cadastrarResponsavel(String email) {
            Usuario usuario = Usuario.cadastrarResponsavel("Responsavel", email, "senha-criptografada");
            usuarios.salvar(usuario);
            return usuario;
        }

        Crianca criarCriancaVinculada(String email) {
            Usuario responsavel = usuarios.buscarPorEmail(email).orElseGet(() -> cadastrarResponsavel(email));
            Crianca crianca = Crianca.cadastrar("Ana", LocalDate.of(2024, 1, 10), Sexo.FEMININO, false, 39, 3200);
            criancas.salvar(crianca);
            vinculos.salvar(VinculoResponsavelCrianca.criarPrincipal(responsavel.getId(), crianca.getId(), Parentesco.PAI));
            return crianca;
        }

        MedidaCrescimento salvarMedida(UUID criancaId, String pesoKg) {
            MedidaCrescimento medida = MedidaCrescimento.registrar(
                    criancaId,
                    LocalDate.now(),
                    new BigDecimal(pesoKg),
                    null,
                    null,
                    OrigemMedidaCrescimento.CASA,
                    null
            );
            return medidas.salvar(medida);
        }
    }

    private static class MedidaRepositorioEmMemoria implements MedidaCrescimentoRepositorio {
        private final Map<UUID, MedidaCrescimento> medidas = new HashMap<>();

        @Override
        public MedidaCrescimento salvar(MedidaCrescimento medida) {
            medidas.put(medida.getId(), medida);
            return medida;
        }

        @Override
        public Optional<MedidaCrescimento> buscarPorId(UUID id) {
            return Optional.ofNullable(medidas.get(id));
        }

        @Override
        public List<MedidaCrescimento> listarPorCrianca(UUID criancaId) {
            return medidas.values().stream()
                    .filter(medida -> medida.getCriancaId().equals(criancaId))
                    .sorted(Comparator.comparing(MedidaCrescimento::getDataMedicao))
                    .toList();
        }

        @Override
        public void removerPorId(UUID id) {
            medidas.remove(id);
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
