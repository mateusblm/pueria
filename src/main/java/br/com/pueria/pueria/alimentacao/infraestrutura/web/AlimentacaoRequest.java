package br.com.pueria.pueria.alimentacao.infraestrutura.web;

import br.com.pueria.pueria.alimentacao.aplicacao.AtualizarRegistroAlimentacaoComando;
import br.com.pueria.pueria.alimentacao.aplicacao.RegistroAlimentacaoComando;
import br.com.pueria.pueria.alimentacao.dominio.DadosAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.EstagioAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TexturaAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.TipoLeiteAlimentacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
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
        Boolean blwMisto,
        Boolean autoalimentacao,
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
        @Size(max = 1000) String observacao,
        @Valid @Size(max = 150) List<AlimentoRegistroAlimentacaoRequest> alimentosOferecidos
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
                blwMisto,
                autoalimentacao,
                texturaPredominante,
                consomeFrutas,
                consomeLegumesVerduras,
                consomeLegumes,
                consomeVerduras,
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
                familiaTranquilaGanhoPesoAtual,
                preocupacaoFamilia,
                observacao,
                alimentosOferecidos == null ? List.of() : alimentosOferecidos.stream()
                        .map(AlimentoRegistroAlimentacaoRequest::paraDominio)
                        .toList()
        );
    }
}
