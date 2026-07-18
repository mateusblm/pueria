package br.com.pueria.pueria.alimentacao.dominio;

import java.time.LocalDate;
import java.util.List;

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
        Boolean blwMisto,
        Boolean autoalimentacao,
        Boolean alimentadoExclusivamentePorCuidador,
        TexturaAlimentar texturaPredominante,
        Boolean consomeFrutas,
        Boolean consomeLegumesVerduras,
        Boolean consomeLegumes,
        Boolean consomeVerduras,
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
        Boolean familiaTranquilaGanhoPesoAtual,
        Boolean preocupacaoFamilia,
        String observacao,
        TipoOrigemAlimento tipoOrigemAlimento,
        OrigemPreparoAlimento origemPreparoAlimento,
        List<AlimentoRegistroAlimentacao> alimentosOferecidos
) {
    public DadosAlimentacao(
            LocalDate dataRegistro, TipoLeiteAlimentacao tipoLeite, EstagioAlimentar estagioAlimentar,
            Integer idadeInicioAlimentacaoComplementarMeses, Integer refeicoesPorDia, Boolean consomeAgua,
            Boolean usaMamadeira, Boolean usaCopo, Boolean usaColher, Boolean blwMisto, Boolean autoalimentacao,
            TexturaAlimentar texturaPredominante, Boolean consomeFrutas, Boolean consomeLegumesVerduras,
            Boolean consomeLegumes, Boolean consomeVerduras, Boolean consomeCereaisTuberculos,
            Boolean consomeFeijoesLeguminosas, Boolean consomeCarnesOvos, Boolean ultraprocessadosFrequentes,
            Boolean bebidasAdocadas, Boolean acucarAdicionado, Boolean salAdicionado, Boolean telasDuranteRefeicoes,
            Boolean refeicoesEmFamilia, Boolean rotinaAlimentarRegular, Boolean seletividadeAlimentar,
            Boolean recusaPersistente, Boolean engasgosFrequentes, Boolean vomitosRecorrentes, Boolean constipacao,
            Boolean diarreiaRecorrente, Boolean dificuldadeGanhoPesoPercebida, Boolean familiaTranquilaGanhoPesoAtual,
            Boolean preocupacaoFamilia, String observacao, TipoOrigemAlimento tipoOrigemAlimento,
            OrigemPreparoAlimento origemPreparoAlimento, List<AlimentoRegistroAlimentacao> alimentosOferecidos
    ) {
        this(dataRegistro, tipoLeite, estagioAlimentar, idadeInicioAlimentacaoComplementarMeses, refeicoesPorDia,
                consomeAgua, usaMamadeira, usaCopo, usaColher, blwMisto, autoalimentacao, null,
                texturaPredominante, consomeFrutas, consomeLegumesVerduras, consomeLegumes, consomeVerduras,
                consomeCereaisTuberculos, consomeFeijoesLeguminosas, consomeCarnesOvos, ultraprocessadosFrequentes,
                bebidasAdocadas, acucarAdicionado, salAdicionado, telasDuranteRefeicoes, refeicoesEmFamilia,
                rotinaAlimentarRegular, seletividadeAlimentar, recusaPersistente, engasgosFrequentes,
                vomitosRecorrentes, constipacao, diarreiaRecorrente, dificuldadeGanhoPesoPercebida,
                familiaTranquilaGanhoPesoAtual, preocupacaoFamilia, observacao, tipoOrigemAlimento,
                origemPreparoAlimento, alimentosOferecidos);
    }
}
