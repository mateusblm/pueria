package br.com.pueria.pueria.desenvolvimento.dominio;

import br.com.pueria.pueria.criancas.dominio.Crianca;

import java.time.LocalDate;
import java.time.Period;

public final class IdadeReferenciaDesenvolvimento {

    private static final int SEMANAS_GESTACAO_TERMO = 40;
    private static final int MESES_LIMITE_IDADE_CORRIGIDA = 24;

    private IdadeReferenciaDesenvolvimento() {
    }

    public static int mesesParaCheckpoints(Crianca crianca, LocalDate dataReferencia) {
        int cronologica = mesesCompletos(crianca.getDataNascimento(), dataReferencia);
        if (!crianca.isPrematura() || crianca.getSemanasGestacionais() == null) {
            return cronologica;
        }

        int diasGestacionais = crianca.getSemanasGestacionais() * 7 + (crianca.getDiasGestacionais() == null ? 0 : crianca.getDiasGestacionais());
        int diasParaTermo = Math.max(0, SEMANAS_GESTACAO_TERMO * 7 - diasGestacionais);
        int corrigida = mesesCompletos(crianca.getDataNascimento().plusDays(diasParaTermo), dataReferencia);

        return corrigida < MESES_LIMITE_IDADE_CORRIGIDA ? corrigida : cronologica;
    }

    private static int mesesCompletos(LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio)) {
            return 0;
        }
        Period periodo = Period.between(inicio, fim);
        return Math.max(0, periodo.getYears() * 12 + periodo.getMonths());
    }
}
