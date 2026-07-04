package br.com.pueria.pueria.criancas.infraestrutura.web;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.Sexo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CriancaResponse(
        UUID id,
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        boolean prematura,
        Integer semanasGestacionais,
        Integer pesoNascimentoGramas,
        LocalDateTime criadoEm
) {

    public static CriancaResponse de(Crianca crianca) {
        return new CriancaResponse(
                crianca.getId(),
                crianca.getNome(),
                crianca.getDataNascimento(),
                crianca.getSexo(),
                crianca.isPrematura(),
                crianca.getSemanasGestacionais(),
                crianca.getPesoNascimentoGramas(),
                crianca.getCriadoEm()
        );
    }
}
