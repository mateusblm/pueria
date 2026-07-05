package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.crescimento.dominio.ResultadoCurvaCrescimento;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AvaliacaoCurvaCrescimento(
        UUID medidaId,
        LocalDate dataMedicao,
        int idadeDias,
        int idadeCronologicaDias,
        boolean idadeCorrigida,
        String criterioIdade,
        List<ResultadoCurvaCrescimento> resultados
) {}
