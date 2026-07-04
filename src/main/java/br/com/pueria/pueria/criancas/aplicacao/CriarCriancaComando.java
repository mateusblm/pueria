package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Sexo;

import java.time.LocalDate;

public record CriarCriancaComando(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        boolean prematura,
        Integer semanasGestacionais,
        Integer pesoNascimentoGramas
) {
}
