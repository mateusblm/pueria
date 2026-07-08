package br.com.pueria.pueria.telas.infraestrutura.web;

import br.com.pueria.pueria.telas.aplicacao.AtualizarRegistroTelasComando;
import br.com.pueria.pueria.telas.aplicacao.RegistroTelasComando;
import br.com.pueria.pueria.telas.dominio.DadosTelas;
import br.com.pueria.pueria.telas.dominio.TipoConteudoTela;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TelasRequest(
        @NotNull LocalDate dataRegistro,
        @Min(0) @Max(1440) Integer minutosDiaSemana,
        @Min(0) @Max(1440) Integer minutosFimSemana,
        TipoConteudoTela tipoConteudoPredominante,
        Boolean telaAoAcordar,
        Boolean telaDuranteRefeicoes,
        Boolean telaAntesDormir,
        Boolean telaParaAcalmar,
        Boolean telaEmSegundoPlano,
        Boolean usoAcompanhadoAdulto,
        Boolean conteudoAdultoSupervisionado,
        Boolean videochamadaFamilia,
        Boolean autoplayAtivo,
        Boolean notificacoesAtivas,
        Boolean dispositivoNoQuarto,
        Boolean brincaAoArLivre,
        Boolean leituraBrincadeiraSemTela,
        Boolean preocupacaoFamilia,
        @Size(max = 1000) String observacao
) {
    RegistroTelasComando paraRegistrar(UUID criancaId, String emailResponsavel) {
        return new RegistroTelasComando(criancaId, emailResponsavel, dados());
    }

    AtualizarRegistroTelasComando paraAtualizar(UUID criancaId, UUID registroId, String emailResponsavel) {
        return new AtualizarRegistroTelasComando(criancaId, registroId, emailResponsavel, dados());
    }

    private DadosTelas dados() {
        return new DadosTelas(
                dataRegistro,
                minutosDiaSemana,
                minutosFimSemana,
                tipoConteudoPredominante,
                telaAoAcordar,
                telaDuranteRefeicoes,
                telaAntesDormir,
                telaParaAcalmar,
                telaEmSegundoPlano,
                usoAcompanhadoAdulto,
                conteudoAdultoSupervisionado,
                videochamadaFamilia,
                autoplayAtivo,
                notificacoesAtivas,
                dispositivoNoQuarto,
                brincaAoArLivre,
                leituraBrincadeiraSemTela,
                preocupacaoFamilia,
                observacao
        );
    }
}
