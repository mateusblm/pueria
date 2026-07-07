package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.aplicacao.AtualizarRegistroAlimentacaoComando;
import br.com.pueria.pueria.alimentacao.aplicacao.RegistroAlimentacaoComando;
import br.com.pueria.pueria.alimentacao.dominio.DadosAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.EstagioAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TipoLeiteAlimentacao;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AlimentacaoRequest(
        @NotNull LocalDate dataRegistro,
        TipoLeiteAlimentacao tipoLeite,
        EstagioAlimentar estagioAlimentar,
        @Min(0) @Max(24) Integer idadeInicioAlimentacaoComplementarMeses,
        @Min(0) @Max(10) Integer refeicoesPorDia,
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
        @Size(max = 1000) String observacao
) {
    RegistroAlimentacaoComando paraRegistrar(UUID criancaId, String emailResponsavel) {
        return new RegistroAlimentacaoComando(criancaId, emailResponsavel, dados());
    }

    AtualizarRegistroAlimentacaoComando paraAtualizar(UUID criancaId, UUID registroId, String emailResponsavel) {
        return new AtualizarRegistroAlimentacaoComando(criancaId, registroId, emailResponsavel, dados());
    }

    private DadosAlimentacao dados() {
        return new DadosAlimentacao(
                dataRegistro,
                tipoLeite,
                estagioAlimentar,
                idadeInicioAlimentacaoComplementarMeses,
                refeicoesPorDia,
                consomeAgua,
                usaMamadeira,
                usaCopo,
                usaColher,
                autoalimentacao,
                texturaPredominante,
                consomeFrutas,
                consomeLegumesVerduras,
                consomeCereaisTuberculos,
                consomeFeijoesLeguminosas,
                consomeCarnesOvos,
                ultraprocessadosFrequentes,
                bebidasAdocadas,
                acucarAdicionado,
                salAdicionado,
                telasDuranteRefeicoes,
                refeicoesEmFamilia,
                rotinaAlimentarRegular,
                seletividadeAlimentar,
                recusaPersistente,
                engasgosFrequentes,
                vomitosRecorrentes,
                constipacao,
                diarreiaRecorrente,
                dificuldadeGanhoPesoPercebida,
                preocupacaoFamilia,
                observacao
        );
    }
}
