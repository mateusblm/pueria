package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;

public record RegistroAlimentacaoDetalhado(
        RegistroAlimentacao registro,
        AnaliseAlimentacao analise
) {}
