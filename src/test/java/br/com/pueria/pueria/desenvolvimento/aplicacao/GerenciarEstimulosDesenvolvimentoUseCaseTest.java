package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.EstimuloDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.EstimuloDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.PapelClinicoMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroEstimuloDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GerenciarEstimulosDesenvolvimentoUseCaseTest {

    @Test
    void retornaAAtividadeVinculadaAoMarcoRespondido() {
        UUID criancaId = UUID.randomUUID();
        UUID marcoId = UUID.randomUUID();
        UUID estimuloId = UUID.randomUUID();
        Usuario responsavel = Usuario.cadastrarResponsavel("Responsável", "responsavel@teste.com", "senha-criptografada");
        Crianca crianca = Crianca.cadastrar("Lia", LocalDate.now().minusMonths(4), Sexo.FEMININO, false, 40, 3200);
        MarcoDesenvolvimento marco = MarcoDesenvolvimento.restaurar(marcoId, 4, AreaDesenvolvimento.LINGUAGEM_COMUNICACAO,
                "Faz arrulhos em uma troca de sons com outra pessoa.", "CDC", TipoFonteMarcoDesenvolvimento.CDC_2022,
                "NEURO_V2_2026_07", PapelClinicoMarcoDesenvolvimento.ACOMPANHAMENTO, false, true);
        EstimuloDesenvolvimento estimulo = new EstimuloDesenvolvimento(estimuloId, 4, 5, AreaDesenvolvimento.LINGUAGEM_COMUNICACAO,
                "Imite o arrulho", "Imite o som e aguarde uma resposta.", "Acompanhe o ritmo da criança.", "Vroom", "ESTIMULOS_V1_2026_07", true);

        CriancaRepositorio criancas = mock(CriancaRepositorio.class);
        UsuarioRepositorio usuarios = mock(UsuarioRepositorio.class);
        VinculoResponsavelCriancaRepositorio vinculos = mock(VinculoResponsavelCriancaRepositorio.class);
        EstimuloDesenvolvimentoRepositorio estimulos = mock(EstimuloDesenvolvimentoRepositorio.class);
        RegistroEstimuloDesenvolvimentoRepositorio registrosEstimulos = mock(RegistroEstimuloDesenvolvimentoRepositorio.class);
        RegistroMarcoDesenvolvimentoRepositorio registrosMarcos = mock(RegistroMarcoDesenvolvimentoRepositorio.class);
        MarcoDesenvolvimentoRepositorio marcos = mock(MarcoDesenvolvimentoRepositorio.class);

        when(usuarios.buscarPorEmail("responsavel@teste.com")).thenReturn(Optional.of(responsavel));
        when(vinculos.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)).thenReturn(true);
        when(criancas.buscarPorId(criancaId)).thenReturn(Optional.of(crianca));
        when(marcos.buscarPorId(marcoId)).thenReturn(Optional.of(marco));
        when(registrosEstimulos.listarPorCrianca(criancaId)).thenReturn(List.of());
        when(estimulos.buscarAtivoParaMarco(marcoId)).thenReturn(Optional.of(estimulo));

        GerenciarEstimulosDesenvolvimentoUseCase useCase = new GerenciarEstimulosDesenvolvimentoUseCase(
                criancas, usuarios, vinculos, estimulos, registrosEstimulos, registrosMarcos, marcos);

        EstimuloDesenvolvimentoResumo resultado = useCase.buscarParaMarco(criancaId, marcoId, "responsavel@teste.com");

        assertEquals(estimuloId, resultado.id());
        assertEquals("Imite o arrulho", resultado.titulo());
    }

    @Test
    void recomendaSomenteAtividadesDosMarcosPendentesDaFaixaSelecionada() {
        UUID criancaId = UUID.randomUUID();
        UUID marcoDaFaixaId = UUID.randomUUID();
        UUID marcoDeOutraFaixaId = UUID.randomUUID();
        UUID marcoClinicoId = UUID.randomUUID();
        UUID estimuloId = UUID.randomUUID();
        Usuario responsavel = Usuario.cadastrarResponsavel("Responsável", "responsavel@teste.com", "senha-criptografada");
        Crianca crianca = Crianca.cadastrar("Lia", LocalDate.now().minusMonths(4), Sexo.FEMININO, false, 40, 3200);
        MarcoDesenvolvimento marcoDaFaixa = marco(marcoDaFaixaId, 4, PapelClinicoMarcoDesenvolvimento.ACOMPANHAMENTO);
        MarcoDesenvolvimento marcoDeOutraFaixa = marco(marcoDeOutraFaixaId, 6, PapelClinicoMarcoDesenvolvimento.ACOMPANHAMENTO);
        MarcoDesenvolvimento marcoClinico = marco(marcoClinicoId, 4, PapelClinicoMarcoDesenvolvimento.ALTA_RELEVANCIA);
        EstimuloDesenvolvimento estimulo = new EstimuloDesenvolvimento(estimuloId, 4, 5, AreaDesenvolvimento.LINGUAGEM_COMUNICACAO,
                "Imite o arrulho", "Imite o som e aguarde uma resposta.", "Acompanhe o ritmo da criança.", "Vroom", "ESTIMULOS_V1_2026_07", true);

        CriancaRepositorio criancas = mock(CriancaRepositorio.class);
        UsuarioRepositorio usuarios = mock(UsuarioRepositorio.class);
        VinculoResponsavelCriancaRepositorio vinculos = mock(VinculoResponsavelCriancaRepositorio.class);
        EstimuloDesenvolvimentoRepositorio estimulos = mock(EstimuloDesenvolvimentoRepositorio.class);
        RegistroEstimuloDesenvolvimentoRepositorio registrosEstimulos = mock(RegistroEstimuloDesenvolvimentoRepositorio.class);
        RegistroMarcoDesenvolvimentoRepositorio registrosMarcos = mock(RegistroMarcoDesenvolvimentoRepositorio.class);
        MarcoDesenvolvimentoRepositorio marcos = mock(MarcoDesenvolvimentoRepositorio.class);
        when(usuarios.buscarPorEmail("responsavel@teste.com")).thenReturn(Optional.of(responsavel));
        when(vinculos.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)).thenReturn(true);
        when(criancas.buscarPorId(criancaId)).thenReturn(Optional.of(crianca));
        when(registrosEstimulos.listarPorCrianca(criancaId)).thenReturn(List.of());
        when(registrosMarcos.listarPorCrianca(criancaId)).thenReturn(List.of(
                RegistroMarcoDesenvolvimento.registrar(criancaId, marcoDaFaixaId, StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO, br.com.pueria.pueria.desenvolvimento.dominio.ModalidadeRegistroMarcoDesenvolvimento.ACOMPANHAMENTO_ATUAL, null),
                RegistroMarcoDesenvolvimento.registrar(criancaId, marcoDeOutraFaixaId, StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO, br.com.pueria.pueria.desenvolvimento.dominio.ModalidadeRegistroMarcoDesenvolvimento.ACOMPANHAMENTO_ATUAL, null),
                RegistroMarcoDesenvolvimento.registrar(criancaId, marcoClinicoId, StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO, br.com.pueria.pueria.desenvolvimento.dominio.ModalidadeRegistroMarcoDesenvolvimento.ACOMPANHAMENTO_ATUAL, null)));
        when(marcos.buscarPorId(marcoDaFaixaId)).thenReturn(Optional.of(marcoDaFaixa));
        when(marcos.buscarPorId(marcoDeOutraFaixaId)).thenReturn(Optional.of(marcoDeOutraFaixa));
        when(marcos.buscarPorId(marcoClinicoId)).thenReturn(Optional.of(marcoClinico));
        when(estimulos.buscarAtivoParaMarco(marcoDaFaixaId)).thenReturn(Optional.of(estimulo));

        GerenciarEstimulosDesenvolvimentoUseCase useCase = new GerenciarEstimulosDesenvolvimentoUseCase(
                criancas, usuarios, vinculos, estimulos, registrosEstimulos, registrosMarcos, marcos);

        List<EstimuloDesenvolvimentoResumo> resultado = useCase.listarRecomendacoes(criancaId, "responsavel@teste.com", 4);

        assertEquals(List.of(estimuloId), resultado.stream().map(EstimuloDesenvolvimentoResumo::id).toList());
    }

    private MarcoDesenvolvimento marco(UUID id, int idadeMeses, PapelClinicoMarcoDesenvolvimento papelClinico) {
        return MarcoDesenvolvimento.restaurar(id, idadeMeses, AreaDesenvolvimento.LINGUAGEM_COMUNICACAO,
                "Faz arrulhos em uma troca de sons com outra pessoa.", "CDC", TipoFonteMarcoDesenvolvimento.CDC_2022,
                "NEURO_V2_2026_07", papelClinico, papelClinico == PapelClinicoMarcoDesenvolvimento.ALTA_RELEVANCIA, true);
    }
}
