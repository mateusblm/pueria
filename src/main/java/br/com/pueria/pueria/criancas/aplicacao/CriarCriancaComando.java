package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;

import java.time.LocalDate;

public record CriarCriancaComando(
        String emailResponsavel,
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        boolean prematura,
        Integer semanasGestacionais,
        Integer pesoNascimentoGramas,
        Parentesco parentesco,
        boolean aceiteConsentimento,
        String versaoTermoConsentimento
) {
}
