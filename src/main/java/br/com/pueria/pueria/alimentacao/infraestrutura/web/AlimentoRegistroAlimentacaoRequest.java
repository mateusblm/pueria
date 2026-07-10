package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.dominio.AceitacaoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.AlimentoRegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.ClassificacaoGluten;
import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.SituacaoSinaisOferta;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record AlimentoRegistroAlimentacaoRequest(
        @NotBlank @Size(max = 80) String codigo,
        @NotBlank @Size(max = 120) String nome,
        @NotNull GrupoAlimento grupo,
        Boolean alergenico,
        LocalDate dataIntroducao,
        @Size(max = 160) String formaPreparo,
        TexturaAlimentar textura,
        @Size(max = 80) String quantidadeAproximada,
        AceitacaoAlimento aceitacao,
        ClassificacaoGluten classificacaoGluten,
        @Size(max = 120) String tipoPeixe,
        @Size(max = 50) List<LocalDate> datasReexposicao,
        SituacaoSinaisOferta situacaoSinais,
        Boolean repetiuOutroDia,
        Boolean sintomasPele,
        Boolean sintomasIntestinais,
        Boolean sintomasRespiratorios,
        Boolean alteracaoSono,
        Boolean alteracaoComportamento,
        @Size(max = 500) String observacao
) {
    AlimentoRegistroAlimentacao paraDominio() {
        return new AlimentoRegistroAlimentacao(
                codigo, nome, grupo, alergenico, dataIntroducao, formaPreparo, textura,
                quantidadeAproximada, aceitacao, classificacaoGluten, tipoPeixe,
                datasReexposicao, situacaoSinais, repetiuOutroDia, sintomasPele,
                sintomasIntestinais, sintomasRespiratorios, alteracaoSono,
                alteracaoComportamento, observacao
        );
    }
}
