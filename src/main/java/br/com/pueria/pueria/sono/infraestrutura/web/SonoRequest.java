package br.com.pueria.pueria.sono.infraestrutura.web;

import br.com.pueria.pueria.sono.aplicacao.AtualizarRegistroSonoComando;
import br.com.pueria.pueria.sono.aplicacao.RegistroSonoComando;
import br.com.pueria.pueria.sono.dominio.DadosSono;
import br.com.pueria.pueria.sono.dominio.AmbienteSono;
import br.com.pueria.pueria.sono.dominio.SuperficieSono;
import br.com.pueria.pueria.sono.dominio.TipoDespertarNoturno;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

public record SonoRequest(
        @NotNull LocalDate dataRegistro,
        LocalTime horarioDormiu,
        LocalTime horarioAcordou,
        @Min(0) @Max(12) Integer quantidadeCochilos,
        @Min(0) @Max(1200) Integer minutosCochilos,
        @Min(0) @Max(30) Integer despertaresNoturnos,
        Boolean dificuldadeIniciarSono,
        Boolean rotinaSonoConsistente,
        Boolean telasAntesDormir,
        SuperficieSono superficieSono,
        AmbienteSono ambienteSono,
        List<TipoDespertarNoturno> tiposDespertarNoturno,
        Boolean roncosFrequentes,
        Boolean pausasRespiratoriasPercebidas,
        Boolean sonoAgitado,
        Boolean rangerDentesDuranteSono,
        Boolean acordaBemDisposto,
        Boolean sonolenciaDiurna,
        Boolean irritabilidadeCansaco,
        Boolean preocupacaoFamilia,
        @Size(max = 1000) String observacao
) {
    RegistroSonoComando paraRegistrar(UUID criancaId, String emailResponsavel) {
        return new RegistroSonoComando(criancaId, emailResponsavel, dados());
    }

    AtualizarRegistroSonoComando paraAtualizar(UUID criancaId, UUID registroId, String emailResponsavel) {
        return new AtualizarRegistroSonoComando(criancaId, registroId, emailResponsavel, dados());
    }

    private DadosSono dados() {
        return new DadosSono(
                dataRegistro,
                horarioDormiu,
                horarioAcordou,
                quantidadeCochilos,
                minutosCochilos,
                despertaresNoturnos,
                dificuldadeIniciarSono,
                rotinaSonoConsistente,
                telasAntesDormir,
                superficieSono,
                ambienteSono,
                tiposDespertarNoturno,
                roncosFrequentes,
                pausasRespiratoriasPercebidas,
                sonoAgitado,
                rangerDentesDuranteSono,
                acordaBemDisposto,
                sonolenciaDiurna,
                irritabilidadeCansaco,
                preocupacaoFamilia,
                observacao
        );
    }
}
