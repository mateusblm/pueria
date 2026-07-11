package br.com.pueria.pueria.telas.infraestrutura.web;

import br.com.pueria.pueria.telas.aplicacao.RegistroTelasDetalhado;
import br.com.pueria.pueria.telas.dominio.RegistroTelas;
import br.com.pueria.pueria.telas.dominio.TipoConteudoTela;
import br.com.pueria.pueria.telas.dominio.ContextoUsoTela;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public record RegistroTelasResponse(
        UUID id,
        UUID criancaId,
        LocalDate dataRegistro,
        Integer minutosDiaSemana,
        Integer minutosFimSemana,
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
        String observacao,
        Integer minutosMediosDia,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        AnaliseTelasResponse analise
) {
    static RegistroTelasResponse de(RegistroTelasDetalhado detalhado) {
        RegistroTelas registro = detalhado.registro();
        return new RegistroTelasResponse(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getMinutosDiaSemana(),
                registro.getMinutosFimSemana(),
                registro.getTipoConteudoPredominante(),
                registro.getContextosUso(),
                registro.getTelaAoAcordar(),
                registro.getTelaDuranteRefeicoes(),
                registro.getTelaAntesDormir(),
                registro.getTelaParaAcalmar(),
                registro.getTelaEmSegundoPlano(),
                registro.getUsoAcompanhadoAdulto(),
                registro.getConteudoAdultoSupervisionado(),
                registro.getCriancaEscolheConteudoLivremente(),
                registro.getVideochamadaFamilia(),
                registro.getAutoplayAtivo(),
                registro.getNotificacoesAtivas(),
                registro.getDispositivoNoQuarto(),
                registro.getBrincaAoArLivre(),
                registro.getLeituraBrincadeiraSemTela(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.minutosMediosDia(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm(),
                AnaliseTelasResponse.de(detalhado.analise())
        );
    }
}
