package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.HistoricoRespostaMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.HistoricoRespostaMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.PapelClinicoMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoFonteMarcoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarTrajetoriaDesenvolvimentoUseCaseTest {

    @Test
    void apresentaSomenteObservacoesRegistradasSemInterpretacaoClinica() {
        UUID criancaId = UUID.randomUUID();
        UUID primeiroMarcoId = UUID.randomUUID();
        UUID segundoMarcoId = UUID.randomUUID();
        Usuario responsavel = Usuario.cadastrarResponsavel("Responsável", "responsavel@teste.com", "senha-criptografada");

        MarcoDesenvolvimento primeiroMarco = marco(primeiroMarcoId, "Sorri ao ver o rosto de alguém.");
        MarcoDesenvolvimento segundoMarco = marco(segundoMarcoId, "Faz sons além do choro.");
        List<HistoricoRespostaMarcoDesenvolvimento> eventos = List.of(
                evento(criancaId, primeiroMarcoId, null, StatusMarcoDesenvolvimento.OBSERVADO, LocalDateTime.of(2026, 7, 10, 10, 0)),
                evento(criancaId, segundoMarcoId, StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO, StatusMarcoDesenvolvimento.OBSERVADO, LocalDateTime.of(2026, 7, 11, 10, 0)),
                evento(criancaId, primeiroMarcoId, StatusMarcoDesenvolvimento.OBSERVADO, StatusMarcoDesenvolvimento.OBSERVADO, LocalDateTime.of(2026, 7, 12, 10, 0)),
                evento(criancaId, segundoMarcoId, StatusMarcoDesenvolvimento.OBSERVADO, StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA, LocalDateTime.of(2026, 7, 13, 10, 0))
        );

        ListarTrajetoriaDesenvolvimentoUseCase useCase = new ListarTrajetoriaDesenvolvimentoUseCase(
                usuarios(responsavel), vinculos(criancaId), historico(eventos), marcos(primeiroMarco, segundoMarco)
        );

        List<EventoTrajetoriaDesenvolvimentoResumo> resultado = useCase.executar(criancaId, "responsavel@teste.com");

        assertEquals(3, resultado.size());
        assertEquals("OBSERVADO_NOVAMENTE", resultado.get(0).tipo());
        assertEquals("NOVA_OBSERVACAO", resultado.get(1).tipo());
        assertEquals("PRIMEIRA_OBSERVACAO", resultado.get(2).tipo());
    }

    private static MarcoDesenvolvimento marco(UUID id, String descricao) {
        return MarcoDesenvolvimento.restaurar(id, 2, AreaDesenvolvimento.SOCIAL_EMOCIONAL, descricao, "CDC",
                TipoFonteMarcoDesenvolvimento.CDC_2022, "NEURO_V2", PapelClinicoMarcoDesenvolvimento.ACOMPANHAMENTO, false, true);
    }

    private static HistoricoRespostaMarcoDesenvolvimento evento(UUID criancaId, UUID marcoId, StatusMarcoDesenvolvimento anterior,
                                                                  StatusMarcoDesenvolvimento novo, LocalDateTime registradoEm) {
        return new HistoricoRespostaMarcoDesenvolvimento(UUID.randomUUID(), criancaId, marcoId, anterior, novo, null, null, null, registradoEm);
    }

    private static UsuarioRepositorio usuarios(Usuario responsavel) {
        return new UsuarioRepositorio() {
            @Override public Usuario salvar(Usuario item) { return item; }
            @Override public Optional<Usuario> buscarPorId(UUID id) { return Optional.of(responsavel); }
            @Override public Optional<Usuario> buscarPorEmail(String email) { return Optional.of(responsavel); }
            @Override public boolean existePorEmail(String email) { return true; }
        };
    }

    private static VinculoResponsavelCriancaRepositorio vinculos(UUID criancaId) {
        return new VinculoResponsavelCriancaRepositorio() {
            @Override public boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID id) { return criancaId.equals(id); }
            @Override public br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca salvar(br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca item) { return item; }
            @Override public Optional<br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca> buscarPorId(UUID id) { return Optional.empty(); }
            @Override public boolean existeResponsavelPrincipal(UUID id) { return true; }
            @Override public List<UUID> listarCriancaIdsPorUsuario(UUID id) { return List.of(criancaId); }
            @Override public void removerPorCrianca(UUID id) { }
        };
    }

    private static HistoricoRespostaMarcoDesenvolvimentoRepositorio historico(List<HistoricoRespostaMarcoDesenvolvimento> eventos) {
        return new HistoricoRespostaMarcoDesenvolvimentoRepositorio() {
            @Override public HistoricoRespostaMarcoDesenvolvimento salvar(HistoricoRespostaMarcoDesenvolvimento item) { return item; }
            @Override public List<HistoricoRespostaMarcoDesenvolvimento> listarPorCrianca(UUID id) { return eventos; }
        };
    }

    private static MarcoDesenvolvimentoRepositorio marcos(MarcoDesenvolvimento... marcos) {
        return new MarcoDesenvolvimentoRepositorio() {
            @Override public Optional<MarcoDesenvolvimento> buscarPorId(UUID id) { return List.of(marcos).stream().filter(marco -> marco.getId().equals(id)).findFirst(); }
            @Override public List<MarcoDesenvolvimento> listarAtivosAteIdadeMeses(int idadeMeses) { return List.of(marcos); }
        };
    }
}
