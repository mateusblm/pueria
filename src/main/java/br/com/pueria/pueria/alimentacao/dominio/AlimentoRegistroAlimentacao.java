package br.com.pueria.pueria.alimentacao.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.util.List;

public record AlimentoRegistroAlimentacao(
        String codigo,
        String nome,
        GrupoAlimento grupo,
        Boolean alergenico,
        LocalDate dataIntroducao,
        String formaPreparo,
        TexturaAlimentar textura,
        String quantidadeAproximada,
        AceitacaoAlimento aceitacao,
        ClassificacaoGluten classificacaoGluten,
        String tipoPeixe,
        List<LocalDate> datasReexposicao,
        SituacaoSinaisOferta situacaoSinais,
        Boolean repetiuOutroDia,
        Boolean sintomasPele,
        Boolean sintomasIntestinais,
        Boolean sintomasRespiratorios,
        Boolean alteracaoSono,
        Boolean alteracaoComportamento,
        String observacao
) {
    public AlimentoRegistroAlimentacao(String codigo, String nome, GrupoAlimento grupo) {
        this(codigo, nome, grupo, false, null, null, null, null, null,
                ClassificacaoGluten.NAO_SE_APLICA, null, List.of(), SituacaoSinaisOferta.NAO_INFORMADO,
                false, false, false, false, false, false, null);
    }

    public AlimentoRegistroAlimentacao {
        if (codigo == null || codigo.isBlank()) {
            throw new RegraDominioException("O codigo do alimento e obrigatorio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new RegraDominioException("O nome do alimento e obrigatorio.");
        }
        if (grupo == null) {
            throw new RegraDominioException("O grupo do alimento e obrigatorio.");
        }

        codigo = codigo.trim().toLowerCase();
        nome = nome.trim().replaceAll("\\s+", " ");
        alergenico = Boolean.TRUE.equals(alergenico);
        formaPreparo = tratarTexto(formaPreparo, 160, "A forma de preparo");
        textura = textura == null ? TexturaAlimentar.NAO_INFORMADO : textura;
        quantidadeAproximada = tratarTexto(quantidadeAproximada, 80, "A quantidade aproximada");
        aceitacao = aceitacao == null ? AceitacaoAlimento.NAO_INFORMADA : aceitacao;
        classificacaoGluten = classificacaoGluten == null ? ClassificacaoGluten.NAO_SE_APLICA : classificacaoGluten;
        tipoPeixe = tratarTexto(tipoPeixe, 120, "O tipo de peixe");
        datasReexposicao = normalizarDatas(datasReexposicao);
        repetiuOutroDia = Boolean.TRUE.equals(repetiuOutroDia) || !datasReexposicao.isEmpty();
        sintomasPele = Boolean.TRUE.equals(sintomasPele);
        sintomasIntestinais = Boolean.TRUE.equals(sintomasIntestinais);
        sintomasRespiratorios = Boolean.TRUE.equals(sintomasRespiratorios);
        alteracaoSono = Boolean.TRUE.equals(alteracaoSono);
        alteracaoComportamento = Boolean.TRUE.equals(alteracaoComportamento);
        observacao = tratarTexto(observacao, 500, "A observacao do alimento");

        boolean possuiSinalMarcado = sintomasPele || sintomasIntestinais || sintomasRespiratorios
                || alteracaoSono || alteracaoComportamento;
        situacaoSinais = situacaoSinais == null
                ? (possuiSinalMarcado ? SituacaoSinaisOferta.SINAIS_PERCEBIDOS : SituacaoSinaisOferta.NAO_INFORMADO)
                : situacaoSinais;
        if (situacaoSinais == SituacaoSinaisOferta.NENHUM_PERCEBIDO && possuiSinalMarcado) {
            throw new RegraDominioException("Nao e possivel marcar sinais quando nenhum sinal foi percebido.");
        }

        if (codigo.length() > 80) {
            throw new RegraDominioException("O codigo do alimento deve ter no maximo 80 caracteres.");
        }
        if (nome.length() > 120) {
            throw new RegraDominioException("O nome do alimento deve ter no maximo 120 caracteres.");
        }
    }

    private static String tratarTexto(String valor, int limite, String nomeCampo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String texto = valor.trim().replaceAll("\\s+", " ");
        if (texto.length() > limite) {
            throw new RegraDominioException(nomeCampo + " deve ter no maximo " + limite + " caracteres.");
        }
        return texto;
    }

    private static List<LocalDate> normalizarDatas(List<LocalDate> datas) {
        if (datas == null || datas.isEmpty()) {
            return List.of();
        }
        if (datas.size() > 50) {
            throw new RegraDominioException("Registre no maximo 50 reexposicoes por alimento.");
        }
        return datas.stream().filter(java.util.Objects::nonNull).distinct().sorted().toList();
    }
}
