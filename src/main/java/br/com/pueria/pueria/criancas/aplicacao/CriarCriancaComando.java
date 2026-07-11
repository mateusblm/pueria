package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.AlimentacaoInicial;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.criancas.dominio.StatusTriagemNeonatal;
import br.com.pueria.pueria.criancas.dominio.TipoParto;
import br.com.pueria.pueria.criancas.dominio.ContextoClinicoCrianca;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarCriancaComando(
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
        String observacoesNascimento,
        boolean preNatalRealizado,
        Integer consultasPreNatal,
        boolean diabetesGestacional,
        boolean hipertensaoGestacional,
        boolean infeccaoGestacional,
        boolean sangramentoGestacional,
        boolean usoAlcoolGestacao,
        boolean usoTabacoGestacao,
        boolean outrasExposicoesGestacao,
        String observacoesGestacao,
        Integer diasAltaHospitalar,
        boolean retornoHospitalarPrimeiraSemana,
        StatusTriagemNeonatal testePezinho,
        StatusTriagemNeonatal testeOrelhinha,
        StatusTriagemNeonatal testeOlhinho,
        StatusTriagemNeonatal testeCoracaozinho,
        boolean amamentacaoPrimeiraHora,
        AlimentacaoInicial alimentacaoInicial,
        ContextoClinicoCrianca contextoClinico,
        Parentesco parentesco,
        boolean aceiteConsentimento,
        String versaoTermoConsentimento
) {
    public CriarCriancaComando(
            String emailResponsavel,
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer pesoNascimentoGramas,
            Parentesco parentesco,
            boolean aceiteConsentimento,
            String versaoTermoConsentimento
    ) {
        this(
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
                null,
                false,
                null,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null,
                false,
                StatusTriagemNeonatal.NAO_INFORMADO,
                StatusTriagemNeonatal.NAO_INFORMADO,
                StatusTriagemNeonatal.NAO_INFORMADO,
                StatusTriagemNeonatal.NAO_INFORMADO,
                false,
                AlimentacaoInicial.NAO_INFORMADO,
                ContextoClinicoCrianca.naoInformado(),
                parentesco,
                aceiteConsentimento,
                versaoTermoConsentimento
        );
    }
}
