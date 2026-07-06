package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.criancas.dominio.TipoParto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AtualizarCriancaComando(
        UUID id,
        String emailResponsavel,
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        boolean prematura,
        Integer semanasGestacionais,
        Integer diasGestacionais,
        TipoParto tipoParto,
        Integer pesoNascimentoGramas,
        BigDecimal comprimentoNascimentoCm,
        BigDecimal perimetroCefalicoNascimentoCm,
        Integer apgarUmMinuto,
        Integer apgarCincoMinutos,
        boolean utiNeonatal,
        boolean reanimacaoNeonatal,
        boolean ictericiaNeonatal,
        boolean dificuldadeRespiratoria,
        boolean dificuldadeAmamentacao,
        String observacoesNascimento
) {
    public AtualizarCriancaComando(
            UUID id,
            String emailResponsavel,
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer pesoNascimentoGramas
    ) {
        this(
                id,
                emailResponsavel,
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                0,
                TipoParto.NAO_INFORMADO,
                pesoNascimentoGramas,
                new BigDecimal("50.0"),
                new BigDecimal("34.0"),
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                null
        );
    }
}
