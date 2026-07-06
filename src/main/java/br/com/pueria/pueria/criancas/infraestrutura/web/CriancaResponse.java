package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.Sexo;
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
                crianca.getCriadoEm(),
                crianca.getAtualizadoEm()
        );
    }
}
