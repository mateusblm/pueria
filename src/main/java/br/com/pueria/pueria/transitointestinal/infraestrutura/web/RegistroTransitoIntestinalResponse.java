package br.com.pueria.pueria.transitointestinal.infraestrutura.web;

import br.com.pueria.pueria.transitointestinal.aplicacao.RegistroTransitoIntestinalDetalhado;
import br.com.pueria.pueria.transitointestinal.dominio.FacilidadeLimpezaFezes;
import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.TipoFezesBristol;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroTransitoIntestinalResponse(
        UUID id,
        UUID criancaId,
        LocalDate dataRegistro,
        TipoFezesBristol tipoFezes,
        Integer evacuacoesPorDia,
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
        String observacao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        AnaliseTransitoIntestinalResponse analise
) {
    static RegistroTransitoIntestinalResponse de(RegistroTransitoIntestinalDetalhado detalhado) {
        RegistroTransitoIntestinal registro = detalhado.registro();
        return new RegistroTransitoIntestinalResponse(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getTipoFezes(),
                registro.getEvacuacoesPorDia(),
                registro.getFacilidadeLimpeza(),
                registro.getMuco(),
                registro.getRestosAlimentares(),
                registro.getRaiasSangue(),
                registro.getConstipacao(),
                registro.getDiarreia(),
                registro.getDorEvacuar(),
                registro.getEscapeFecal(),
                registro.getAssaduraFrequente(),
                registro.getAssaduraVermelhidao(),
                registro.getAssaduraPontosVermelhos(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm(),
                AnaliseTransitoIntestinalResponse.de(detalhado.analise())
        );
    }
}
