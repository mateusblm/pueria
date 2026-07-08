package br.com.pueria.pueria.telas.dominio;

import java.time.LocalDate;

public record DadosTelas(
        LocalDate dataRegistro,
        Integer minutosDiaSemana,
        Integer minutosFimSemana,
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
        String observacao
) {}
