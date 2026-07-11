package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.aplicacao.CriarCriancaComando;
import br.com.pueria.pueria.criancas.dominio.AlimentacaoInicial;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.criancas.dominio.StatusTriagemNeonatal;
import br.com.pueria.pueria.criancas.dominio.TipoParto;
import br.com.pueria.pueria.criancas.dominio.TipoGestacao;
import br.com.pueria.pueria.criancas.dominio.StatusCondicaoClinica;
import br.com.pueria.pueria.criancas.dominio.ContextoClinicoCrianca;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarCriancaRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @PastOrPresent(message = "A data de nascimento não pode estar no futuro.")
        LocalDate dataNascimento,

        Sexo sexo,
        boolean prematura,

        @NotNull(message = "As semanas gestacionais são obrigatórias.")
        @Min(value = 22, message = "As semanas gestacionais devem estar entre 22 e 42.")
        @Max(value = 42, message = "As semanas gestacionais devem estar entre 22 e 42.")
        Integer semanasGestacionais,

        @NotNull(message = "Os dias gestacionais são obrigatórios.")
        @Min(value = 0, message = "Os dias gestacionais devem estar entre 0 e 6.")
        @Max(value = 6, message = "Os dias gestacionais devem estar entre 0 e 6.")
        Integer diasGestacionais,

        @NotNull(message = "O tipo de parto é obrigatório.")
        TipoParto tipoParto,

        @NotNull(message = "O peso ao nascer é obrigatório.")
        @Min(value = 300, message = "O peso de nascimento informado está fora do limite operacional permitido.")
        @Max(value = 7000, message = "O peso de nascimento informado está fora do limite operacional permitido.")
        Integer pesoNascimentoGramas,

        @NotNull(message = "O comprimento ao nascer é obrigatório.")
        @DecimalMin(value = "20.0", message = "O comprimento ao nascer informado está fora do limite operacional permitido.")
        @DecimalMax(value = "70.0", message = "O comprimento ao nascer informado está fora do limite operacional permitido.")
        BigDecimal comprimentoNascimentoCm,

        @NotNull(message = "O perímetro cefálico ao nascer é obrigatório.")
        @DecimalMin(value = "20.0", message = "O perímetro cefálico ao nascer informado está fora do limite operacional permitido.")
        @DecimalMax(value = "50.0", message = "O perímetro cefálico ao nascer informado está fora do limite operacional permitido.")
        BigDecimal perimetroCefalicoNascimentoCm,

        @Min(value = 0, message = "O Apgar deve estar entre 0 e 10.")
        @Max(value = 10, message = "O Apgar deve estar entre 0 e 10.")
        Integer apgarUmMinuto,

        @Min(value = 0, message = "O Apgar deve estar entre 0 e 10.")
        @Max(value = 10, message = "O Apgar deve estar entre 0 e 10.")
        Integer apgarCincoMinutos,

        boolean utiNeonatal,
        boolean reanimacaoNeonatal,
        boolean ictericiaNeonatal,
        boolean dificuldadeRespiratoria,
        boolean dificuldadeAmamentacao,

        @Size(max = 1000, message = "As observações do nascimento devem ter no máximo 1000 caracteres.")
        String observacoesNascimento,

        boolean preNatalRealizado,

        @Min(value = 0, message = "O número de consultas de pré-natal está fora do limite operacional permitido.")
        @Max(value = 60, message = "O número de consultas de pré-natal está fora do limite operacional permitido.")
        Integer consultasPreNatal,

        boolean diabetesGestacional,
        boolean hipertensaoGestacional,
        boolean infeccaoGestacional,
        boolean sangramentoGestacional,
        boolean usoAlcoolGestacao,
        boolean usoTabacoGestacao,
        boolean outrasExposicoesGestacao,

        @Size(max = 1000, message = "As observações da gestação devem ter no máximo 1000 caracteres.")
        String observacoesGestacao,

        @Min(value = 0, message = "Os dias até a alta hospitalar estão fora do limite operacional permitido.")
        @Max(value = 365, message = "Os dias até a alta hospitalar estão fora do limite operacional permitido.")
        Integer diasAltaHospitalar,

        boolean retornoHospitalarPrimeiraSemana,
        StatusTriagemNeonatal testePezinho,
        StatusTriagemNeonatal testeOrelhinha,
        StatusTriagemNeonatal testeOlhinho,
        StatusTriagemNeonatal testeCoracaozinho,
        boolean amamentacaoPrimeiraHora,
        AlimentacaoInicial alimentacaoInicial,
        TipoGestacao tipoGestacao,
        StatusCondicaoClinica statusT21,
        StatusCondicaoClinica statusTurner,
        Boolean outraCondicaoRelevante,
        @Size(max = 1000) String observacoesCondicaoRelevante,

        @NotNull(message = "O parentesco é obrigatório.")
        Parentesco parentesco,

        @AssertTrue(message = "O consentimento precisa estar aceito para cadastrar a criança.")
        boolean aceiteConsentimento,

        @NotBlank(message = "A versão do termo de consentimento é obrigatória.")
        @Size(max = 30, message = "A versão do termo de consentimento deve ter no máximo 30 caracteres.")
        String versaoTermoConsentimento
) {

    public CriarCriancaComando paraComando(String emailResponsavel) {
        return new CriarCriancaComando(
                emailResponsavel, nome, dataNascimento, sexo, prematura, semanasGestacionais, diasGestacionais,
                tipoParto, pesoNascimentoGramas, comprimentoNascimentoCm, perimetroCefalicoNascimentoCm,
                apgarUmMinuto, apgarCincoMinutos, utiNeonatal, reanimacaoNeonatal, ictericiaNeonatal,
                dificuldadeRespiratoria, dificuldadeAmamentacao, observacoesNascimento, preNatalRealizado,
                consultasPreNatal, diabetesGestacional, hipertensaoGestacional, infeccaoGestacional,
                sangramentoGestacional, usoAlcoolGestacao, usoTabacoGestacao, outrasExposicoesGestacao,
                observacoesGestacao, diasAltaHospitalar, retornoHospitalarPrimeiraSemana, testePezinho,
                testeOrelhinha, testeOlhinho, testeCoracaozinho, amamentacaoPrimeiraHora, alimentacaoInicial,
                new ContextoClinicoCrianca(tipoGestacao, statusT21, statusTurner,
                        Boolean.TRUE.equals(outraCondicaoRelevante), observacoesCondicaoRelevante),
                parentesco, aceiteConsentimento, versaoTermoConsentimento
        );
    }
}
