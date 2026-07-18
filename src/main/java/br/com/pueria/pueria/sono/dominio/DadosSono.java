package br.com.pueria.pueria.sono.dominio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DadosSono(
        LocalDate dataRegistro,
        LocalTime horarioDormiu,
        LocalTime horarioAcordou,
        Integer quantidadeCochilos,
        Integer minutosCochilos,
        Integer despertaresNoturnos,
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
        Boolean dificilDeSerAcordado,
        Boolean malHumorado,
        Boolean irritado,
        Boolean preocupacaoFamilia,
        String observacao
) {
    public DadosSono(
            LocalDate dataRegistro, LocalTime horarioDormiu, LocalTime horarioAcordou,
            Integer quantidadeCochilos, Integer minutosCochilos, Integer despertaresNoturnos,
            Boolean dificuldadeIniciarSono, Boolean rotinaSonoConsistente, Boolean telasAntesDormir,
            SuperficieSono superficieSono, AmbienteSono ambienteSono, List<TipoDespertarNoturno> tiposDespertarNoturno,
            Boolean roncosFrequentes, Boolean pausasRespiratoriasPercebidas, Boolean sonoAgitado,
            Boolean rangerDentesDuranteSono, Boolean acordaBemDisposto, Boolean sonolenciaDiurna,
            Boolean irritabilidadeCansaco, Boolean preocupacaoFamilia, String observacao
    ) {
        this(dataRegistro, horarioDormiu, horarioAcordou, quantidadeCochilos, minutosCochilos, despertaresNoturnos,
                dificuldadeIniciarSono, rotinaSonoConsistente, telasAntesDormir, superficieSono, ambienteSono,
                tiposDespertarNoturno, roncosFrequentes, pausasRespiratoriasPercebidas, sonoAgitado,
                rangerDentesDuranteSono, acordaBemDisposto, sonolenciaDiurna, irritabilidadeCansaco,
                null, null, null, preocupacaoFamilia, observacao);
    }
}
