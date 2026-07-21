package br.com.pueria.pueria.home.infraestrutura.web;

import java.util.List;

/**
 * Retrato pronto para a home. O cliente apenas apresenta este contrato; a
 * leitura dos registros e das análises pertence ao servidor.
 */
public record ResumoHomeResponse(Estado estado, List<Area> areas) {
    public enum Estado { INICIAL, ACOMPANHAR, ATENCAO, TRANQUILO }
    public enum Modulo { CRESCIMENTO, NEURODESENVOLVIMENTO, SONO, ALIMENTACAO, HUMOR, TELAS, TRANSITO_INTESTINAL, SAUDE }
    public enum EstadoArea { SEM_REGISTROS, INICIAL, EM_DIA, ACOMPANHAR, ATENCAO }
    public enum Acao { VER, REGISTRAR, COMECAR }

    public record Area(Modulo modulo, EstadoArea estado, String resumo, int quantidadeRegistros,
                       int pontosAtencao, Acao acao) {}
}
