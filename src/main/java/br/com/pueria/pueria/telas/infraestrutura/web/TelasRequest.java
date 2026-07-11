package br.com.pueria.pueria.telas.infraestrutura.web;

import br.com.pueria.pueria.telas.aplicacao.AtualizarRegistroTelasComando;
import br.com.pueria.pueria.telas.aplicacao.RegistroTelasComando;
import br.com.pueria.pueria.telas.dominio.DadosTelas;
import br.com.pueria.pueria.telas.dominio.TipoConteudoTela;
import br.com.pueria.pueria.telas.dominio.ContextoUsoTela;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

public record TelasRequest(
        @NotNull LocalDate dataRegistro,
        @Min(0) @Max(1440) Integer minutosDiaSemana,
        @Min(0) @Max(1440) Integer minutosFimSemana,
        TipoConteudoTela tipoConteudoPredominante,
        List<ContextoUsoTela> contextosUso,
        Boolean telaAoAcordar,
        Boolean telaDuranteRefeicoes,
        Boolean telaAntesDormir,
        Boolean telaParaAcalmar,
        Boolean telaEmSegundoPlano,
        Boolean usoAcompanhadoAdulto,
        Boolean conteudoAdultoSupervisionado,
        Boolean criancaEscolheConteudoLivremente,
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
                contextosUso,
                telaAoAcordar,
                telaDuranteRefeicoes,
                telaAntesDormir,
                telaParaAcalmar,
                telaEmSegundoPlano,
                usoAcompanhadoAdulto,
                conteudoAdultoSupervisionado,
                criancaEscolheConteudoLivremente,
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
