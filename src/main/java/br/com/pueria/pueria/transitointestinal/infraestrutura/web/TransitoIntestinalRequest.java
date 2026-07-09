package br.com.pueria.pueria.transitointestinal.infraestrutura.web;

import br.com.pueria.pueria.transitointestinal.aplicacao.AtualizarRegistroTransitoIntestinalComando;
import br.com.pueria.pueria.transitointestinal.aplicacao.RegistroTransitoIntestinalComando;
import br.com.pueria.pueria.transitointestinal.dominio.DadosTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.FacilidadeLimpezaFezes;
import br.com.pueria.pueria.transitointestinal.dominio.TipoFezesBristol;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TransitoIntestinalRequest(
        @NotNull LocalDate dataRegistro,
        TipoFezesBristol tipoFezes,
        @Min(0) @Max(30) Integer evacuacoesPorDia,
        FacilidadeLimpezaFezes facilidadeLimpeza,
        Boolean muco,
        Boolean restosAlimentares,
        Boolean raiasSangue,
        Boolean constipacao,
        Boolean diarreia,
        Boolean dorEvacuar,
        Boolean escapeFecal,
        Boolean assaduraFrequente,
        Boolean assaduraVermelhidao,
        Boolean assaduraPontosVermelhos,
        Boolean preocupacaoFamilia,
        @Size(max = 1000) String observacao
) {
    RegistroTransitoIntestinalComando paraRegistrar(UUID criancaId, String emailResponsavel) {
        return new RegistroTransitoIntestinalComando(criancaId, emailResponsavel, dados());
    }

    AtualizarRegistroTransitoIntestinalComando paraAtualizar(UUID criancaId, UUID registroId, String emailResponsavel) {
        return new AtualizarRegistroTransitoIntestinalComando(criancaId, registroId, emailResponsavel, dados());
    }

    private DadosTransitoIntestinal dados() {
        return new DadosTransitoIntestinal(
                dataRegistro,
                tipoFezes,
                evacuacoesPorDia,
                facilidadeLimpeza,
                muco,
                restosAlimentares,
                raiasSangue,
                constipacao,
                diarreia,
                dorEvacuar,
                escapeFecal,
                assaduraFrequente,
                assaduraVermelhidao,
                assaduraPontosVermelhos,
                preocupacaoFamilia,
                observacao
        );
    }
}
