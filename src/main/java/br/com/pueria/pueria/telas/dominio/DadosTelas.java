package br.com.pueria.pueria.telas.dominio;

import java.time.LocalDate;
import java.util.List;

public record DadosTelas(
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
        String observacao
) {}
