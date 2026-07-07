package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.aplicacao.RegistroAlimentacaoDetalhado;
import br.com.pueria.pueria.alimentacao.dominio.EstagioAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TipoLeiteAlimentacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroAlimentacaoResponse(
        UUID id,
        UUID criancaId,
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
        String observacao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        AnaliseAlimentacaoResponse analise
) {
    static RegistroAlimentacaoResponse de(RegistroAlimentacaoDetalhado detalhado) {
        RegistroAlimentacao registro = detalhado.registro();
        return new RegistroAlimentacaoResponse(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getTipoLeite(),
                registro.getEstagioAlimentar(),
                registro.getIdadeInicioAlimentacaoComplementarMeses(),
                registro.getRefeicoesPorDia(),
                registro.getConsomeAgua(),
                registro.getUsaMamadeira(),
                registro.getUsaCopo(),
                registro.getUsaColher(),
                registro.getAutoalimentacao(),
                registro.getTexturaPredominante(),
                registro.getConsomeFrutas(),
                registro.getConsomeLegumesVerduras(),
                registro.getConsomeCereaisTuberculos(),
                registro.getConsomeFeijoesLeguminosas(),
                registro.getConsomeCarnesOvos(),
                registro.getUltraprocessadosFrequentes(),
                registro.getBebidasAdocadas(),
                registro.getAcucarAdicionado(),
                registro.getSalAdicionado(),
                registro.getTelasDuranteRefeicoes(),
                registro.getRefeicoesEmFamilia(),
                registro.getRotinaAlimentarRegular(),
                registro.getSeletividadeAlimentar(),
                registro.getRecusaPersistente(),
                registro.getEngasgosFrequentes(),
                registro.getVomitosRecorrentes(),
                registro.getConstipacao(),
                registro.getDiarreiaRecorrente(),
                registro.getDificuldadeGanhoPesoPercebida(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm(),
                AnaliseAlimentacaoResponse.de(detalhado.analise())
        );
    }
}
