package br.com.pueria.pueria.alimentacao.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroAlimentacaoTest {

    @Test
    void deveRegistrarComValoresPadraoQuandoCamposOpcionaisNaoForemInformados() {
        RegistroAlimentacao registro = RegistroAlimentacao.registrar(UUID.randomUUID(), dadosValidos(null, null, null, null));

        assertEquals(TipoLeiteAlimentacao.NAO_INFORMADO, registro.getTipoLeite());
        assertEquals(EstagioAlimentar.NAO_INFORMADO, registro.getEstagioAlimentar());
        assertEquals(TexturaAlimentar.NAO_INFORMADO, registro.getTexturaPredominante());
        assertEquals(TipoOrigemAlimento.NAO_INFORMADO, registro.getTipoOrigemAlimento());
        assertEquals(OrigemPreparoAlimento.NAO_INFORMADO, registro.getOrigemPreparoAlimento());
    }

    @Test
    void deveNormalizarAlimentosOferecidosRemovendoDuplicados() {
        DadosAlimentacao dados = dadosValidos(null, null, null, null, List.of(
                new AlimentoRegistroAlimentacao("banana", "Banana", GrupoAlimento.FRUTA),
                new AlimentoRegistroAlimentacao("banana", "Banana prata", GrupoAlimento.FRUTA),
                new AlimentoRegistroAlimentacao("cenoura", "Cenoura", GrupoAlimento.LEGUME_HORTALICA_FRUTO)
        ));

        RegistroAlimentacao registro = RegistroAlimentacao.registrar(UUID.randomUUID(), dados);

        assertEquals(2, registro.getAlimentosOferecidos().size());
    }

    @Test
    void naoDevePermitirDataFutura() {
        assertThrows(RegraDominioException.class, () ->
                RegistroAlimentacao.registrar(UUID.randomUUID(), dadosValidos(LocalDate.now().plusDays(1), null, null, null))
        );
    }

    @Test
    void naoDevePermitirIdadeInicioComplementarForaDoLimite() {
        assertThrows(RegraDominioException.class, () ->
                RegistroAlimentacao.registrar(UUID.randomUUID(), dadosValidos(null, 25, null, null))
        );
    }

    @Test
    void deveManterDetalhesOpcionaisDoAlimento() {
        LocalDate primeiraOferta = LocalDate.now().minusDays(2);
        LocalDate reexposicao = LocalDate.now().minusDays(1);
        AlimentoRegistroAlimentacao ovo = new AlimentoRegistroAlimentacao(
                "ovo", "Ovo", GrupoAlimento.OVO, true, primeiraOferta, " Cozido e amassado ",
                TexturaAlimentar.AMASSADA, "2 colheres", AceitacaoAlimento.BOA,
                ClassificacaoGluten.NAO_SE_APLICA, null, List.of(reexposicao), SituacaoSinaisOferta.NENHUM_PERCEBIDO, true,
                false, false, false, false, false, " Sem sinais observados "
        );

        RegistroAlimentacao registro = RegistroAlimentacao.registrar(
                UUID.randomUUID(), dadosValidos(null, null, null, null, List.of(ovo))
        );

        AlimentoRegistroAlimentacao salvo = registro.getAlimentosOferecidos().getFirst();
        assertEquals(primeiraOferta, salvo.dataIntroducao());
        assertEquals("Cozido e amassado", salvo.formaPreparo());
        assertEquals(AceitacaoAlimento.BOA, salvo.aceitacao());
        assertEquals(true, salvo.alergenico());
        assertEquals(List.of(reexposicao), salvo.datasReexposicao());
        assertEquals(SituacaoSinaisOferta.NENHUM_PERCEBIDO, salvo.situacaoSinais());
    }

    @Test
    void naoDevePermitirPrimeiraOfertaDepoisDaDataDoRegistro() {
        AlimentoRegistroAlimentacao alimento = new AlimentoRegistroAlimentacao(
                "ovo", "Ovo", GrupoAlimento.OVO, true, LocalDate.now(), null,
                null, null, null, ClassificacaoGluten.NAO_SE_APLICA, null, List.of(),
                SituacaoSinaisOferta.NAO_INFORMADO, false, false, false, false, false, false, null
        );

        assertThrows(RegraDominioException.class, () -> RegistroAlimentacao.registrar(
                UUID.randomUUID(), dadosValidos(LocalDate.now().minusDays(1), null, null, null, List.of(alimento))
        ));
    }

    @Test
    void naoDevePermitirReexposicaoAntesDaPrimeiraOferta() {
        LocalDate primeiraOferta = LocalDate.now().minusDays(2);
        AlimentoRegistroAlimentacao alimento = new AlimentoRegistroAlimentacao(
                "ovo", "Ovo", GrupoAlimento.OVO, true, primeiraOferta, null, null, null, null,
                ClassificacaoGluten.NAO_SE_APLICA, null, List.of(primeiraOferta.minusDays(1)),
                SituacaoSinaisOferta.NAO_INFORMADO, true, false, false, false, false, false, null
        );

        assertThrows(RegraDominioException.class, () -> RegistroAlimentacao.registrar(
                UUID.randomUUID(), dadosValidos(null, null, null, null, List.of(alimento))
        ));
    }

    @Test
    void naoDeveMisturarAusenciaDeSinaisComSinalMarcado() {
        assertThrows(RegraDominioException.class, () -> new AlimentoRegistroAlimentacao(
                "ovo", "Ovo", GrupoAlimento.OVO, true, LocalDate.now(), null, null, null, null,
                ClassificacaoGluten.NAO_SE_APLICA, null, List.of(), SituacaoSinaisOferta.NENHUM_PERCEBIDO,
                false, true, false, false, false, false, null
        ));
    }

    private DadosAlimentacao dadosValidos(LocalDate data, Integer idadeInicio, Integer refeicoes, String observacao) {
        return dadosValidos(data, idadeInicio, refeicoes, observacao, List.of());
    }

    private DadosAlimentacao dadosValidos(LocalDate data, Integer idadeInicio, Integer refeicoes, String observacao, List<AlimentoRegistroAlimentacao> alimentos) {
        return new DadosAlimentacao(
                data == null ? LocalDate.now() : data,
                null,
                null,
                idadeInicio,
                refeicoes,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                observacao,
                TipoOrigemAlimento.NAO_INFORMADO,
                OrigemPreparoAlimento.NAO_INFORMADO,
                alimentos
        );
    }
}
