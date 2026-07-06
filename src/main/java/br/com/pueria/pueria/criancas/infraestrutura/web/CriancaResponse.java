package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.dominio.AlimentacaoInicial;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.criancas.dominio.StatusTriagemNeonatal;
import br.com.pueria.pueria.criancas.dominio.TipoParto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CriancaResponse(
        UUID id,
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
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static CriancaResponse de(Crianca crianca) {
        return new CriancaResponse(
                crianca.getId(),
                crianca.getNome(),
                crianca.getDataNascimento(),
                crianca.getSexo(),
                crianca.isPrematura(),
                crianca.getSemanasGestacionais(),
                crianca.getDiasGestacionais(),
                crianca.getTipoParto(),
                crianca.getPesoNascimentoGramas(),
                crianca.getComprimentoNascimentoCm(),
                crianca.getPerimetroCefalicoNascimentoCm(),
                crianca.getApgarUmMinuto(),
                crianca.getApgarCincoMinutos(),
                crianca.isUtiNeonatal(),
                crianca.isReanimacaoNeonatal(),
                crianca.isIctericiaNeonatal(),
                crianca.isDificuldadeRespiratoria(),
                crianca.isDificuldadeAmamentacao(),
                crianca.getObservacoesNascimento(),
                crianca.isPreNatalRealizado(),
                crianca.getConsultasPreNatal(),
                crianca.isDiabetesGestacional(),
                crianca.isHipertensaoGestacional(),
                crianca.isInfeccaoGestacional(),
                crianca.isSangramentoGestacional(),
                crianca.isUsoAlcoolGestacao(),
                crianca.isUsoTabacoGestacao(),
                crianca.isOutrasExposicoesGestacao(),
                crianca.getObservacoesGestacao(),
                crianca.getDiasAltaHospitalar(),
                crianca.isRetornoHospitalarPrimeiraSemana(),
                crianca.getTestePezinho(),
                crianca.getTesteOrelhinha(),
                crianca.getTesteOlhinho(),
                crianca.getTesteCoracaozinho(),
                crianca.isAmamentacaoPrimeiraHora(),
                crianca.getAlimentacaoInicial(),
                crianca.getCriadoEm(),
                crianca.getAtualizadoEm()
        );
    }
}
