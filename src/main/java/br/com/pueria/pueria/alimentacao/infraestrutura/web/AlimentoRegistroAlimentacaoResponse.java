package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.dominio.AceitacaoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.AlimentoRegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.ClassificacaoGluten;
import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.SituacaoSinaisOferta;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;

import java.time.LocalDate;
import java.util.List;

public record AlimentoRegistroAlimentacaoResponse(
        String codigo,
        String nome,
        GrupoAlimento grupo,
        Boolean alergenico,
        LocalDate dataIntroducao,
        String formaPreparo,
        TexturaAlimentar textura,
        String quantidadeAproximada,
        AceitacaoAlimento aceitacao,
        ClassificacaoGluten classificacaoGluten,
        String tipoPeixe,
        List<LocalDate> datasReexposicao,
        SituacaoSinaisOferta situacaoSinais,
        Boolean repetiuOutroDia,
        Boolean sintomasPele,
        Boolean sintomasIntestinais,
        Boolean sintomasRespiratorios,
        Boolean alteracaoSono,
        Boolean alteracaoComportamento,
        String observacao
) {
    static AlimentoRegistroAlimentacaoResponse de(AlimentoRegistroAlimentacao alimento) {
        return new AlimentoRegistroAlimentacaoResponse(
                alimento.codigo(), alimento.nome(), alimento.grupo(), alimento.alergenico(),
                alimento.dataIntroducao(), alimento.formaPreparo(), alimento.textura(),
                alimento.quantidadeAproximada(), alimento.aceitacao(), alimento.classificacaoGluten(),
                alimento.tipoPeixe(), alimento.datasReexposicao(), alimento.situacaoSinais(), alimento.repetiuOutroDia(),
                alimento.sintomasPele(), alimento.sintomasIntestinais(), alimento.sintomasRespiratorios(),
                alimento.alteracaoSono(), alimento.alteracaoComportamento(), alimento.observacao()
        );
    }
}
