package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record IdadeCrianca(int mesesCompletos, long diasDeVida) {

    public static IdadeCrianca calcular(LocalDate dataNascimento, LocalDate dataReferencia) {
        if (dataNascimento == null) {
            throw new RegraDominioException("A data de nascimento é obrigatória.");
        }

        if (dataReferencia == null) {
            throw new RegraDominioException("A data de referência é obrigatória.");
        }

        if (dataNascimento.isAfter(dataReferencia)) {
            throw new RegraDominioException("A data de nascimento não pode ser posterior à data de referência.");
        }

        long meses = ChronoUnit.MONTHS.between(dataNascimento, dataReferencia);
        long dias = ChronoUnit.DAYS.between(dataNascimento, dataReferencia);

        return new IdadeCrianca(Math.toIntExact(meses), dias);
    }
}
