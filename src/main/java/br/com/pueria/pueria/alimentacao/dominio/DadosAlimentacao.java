package br.com.pueria.pueria.alimentacao.dominio;

import java.time.LocalDate;

public record DadosAlimentacao(
        LocalDate dataRegistro,
        TipoLeiteAlimentacao tipoLeite,
        EstagioAlimentar estagioAlimentar,
        Integer idadeInicioAlimentacaoComplementarMeses,
        Integer refeicoesPorDia,
        Boolean consomeAgua,
        Boolean usaMamadeira,
        Boolean usaCopo,
        Boolean usaColher,
        Boolean autoalimentacao,
        TexturaAlimentar texturaPredominante,
        Boolean consomeFrutas,
        Boolean consomeLegumesVerduras,
        Boolean consomeCereaisTuberculos,
        Boolean consomeFeijoesLeguminosas,
        Boolean consomeCarnesOvos,
        Boolean ultraprocessadosFrequentes,
        Boolean bebidasAdocadas,
        Boolean acucarAdicionado,
        Boolean salAdicionado,
        Boolean telasDuranteRefeicoes,
        Boolean refeicoesEmFamilia,
        Boolean rotinaAlimentarRegular,
        Boolean seletividadeAlimentar,
        Boolean recusaPersistente,
        Boolean engasgosFrequentes,
        Boolean vomitosRecorrentes,
        Boolean constipacao,
        Boolean diarreiaRecorrente,
        Boolean dificuldadeGanhoPesoPercebida,
        Boolean preocupacaoFamilia,
        String observacao
) {}
