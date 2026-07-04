package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Sexo;

import java.time.LocalDate;
import java.util.UUID;

public record AtualizarCriancaComando(
        UUID id,
        String emailResponsavel,
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        boolean prematura,
        Integer semanasGestacionais,
        Integer pesoNascimentoGramas
) {
}
