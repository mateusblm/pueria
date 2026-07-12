package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.PapelClinicoMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.TipoFonteMarcoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarMarcosDesenvolvimentoUseCaseTest {

    @Test
    void disponibilizaCheckpointDeDoisMesesParaCriancaNascidaATermoComTresMeses() {
        UUID criancaId = UUID.randomUUID();
        Usuario responsavel = Usuario.cadastrarResponsavel("Responsável", "responsavel@teste.com", "senha-criptografada");
        Crianca crianca = Crianca.cadastrar("Lia", LocalDate.now().minusMonths(3), Sexo.FEMININO, false, 40, 3200);
        MarcoDesenvolvimento marco = MarcoDesenvolvimento.restaurar(UUID.randomUUID(), 2, AreaDesenvolvimento.SOCIAL_EMOCIONAL,
                "Sorri ao ver o rosto de alguém.", "CDC", TipoFonteMarcoDesenvolvimento.CDC_2022,
                "NEURO_V2_2026_07", PapelClinicoMarcoDesenvolvimento.ACOMPANHAMENTO, false, true);

        ListarMarcosDesenvolvimentoUseCase useCase = new ListarMarcosDesenvolvimentoUseCase(
                new CriancaRepositorio() {
                    @Override public Crianca salvar(Crianca item) { return item; }
                    @Override public Optional<Crianca> buscarPorId(UUID id) { return Optional.of(crianca); }
                    @Override public List<Crianca> listarPorIds(List<UUID> ids) { return List.of(crianca); }
                    @Override public void removerPorId(UUID id) { }
                },
                new UsuarioRepositorio() {
                    @Override public Usuario salvar(Usuario item) { return item; }
                    @Override public Optional<Usuario> buscarPorId(UUID id) { return Optional.of(responsavel); }
                    @Override public Optional<Usuario> buscarPorEmail(String email) { return Optional.of(responsavel); }
                    @Override public boolean existePorEmail(String email) { return true; }
                },
                new VinculoResponsavelCriancaRepositorio() {
                    @Override public boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID id) { return criancaId.equals(id); }
                    @Override public br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca salvar(br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca item) { return item; }
                    @Override public Optional<br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca> buscarPorId(UUID id) { return Optional.empty(); }
                    @Override public boolean existeResponsavelPrincipal(UUID id) { return true; }
                    @Override public List<UUID> listarCriancaIdsPorUsuario(UUID id) { return List.of(criancaId); }
                    @Override public void removerPorCrianca(UUID id) { }
                },
                new MarcoDesenvolvimentoRepositorio() {
                    @Override public Optional<MarcoDesenvolvimento> buscarPorId(UUID id) { return Optional.of(marco); }
                    @Override public List<MarcoDesenvolvimento> listarAtivosAteIdadeMeses(int idadeMeses) { return idadeMeses >= 2 ? List.of(marco) : List.of(); }
                },
                new RegistroMarcoDesenvolvimentoRepositorio() {
                    @Override public RegistroMarcoDesenvolvimento salvar(RegistroMarcoDesenvolvimento item) { return item; }
                    @Override public Optional<RegistroMarcoDesenvolvimento> buscarPorCriancaEMarco(UUID id, UUID marcoId) { return Optional.empty(); }
                    @Override public List<RegistroMarcoDesenvolvimento> listarPorCrianca(UUID id) { return List.of(); }
                }
        );

        List<MarcoDesenvolvimentoResumo> resultado = useCase.executar(criancaId, "responsavel@teste.com");

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.getFirst().idadeMeses());
    }
}
