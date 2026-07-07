package br.com.pueria.pueria.sono.dominio;

import java.time.LocalDate;
import java.time.LocalTime;

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
        LocalSono localSono,
        Boolean roncosFrequentes,
        Boolean pausasRespiratoriasPercebidas,
        Boolean sonoAgitado,
        Boolean sonolenciaDiurna,
        Boolean irritabilidadeCansaco,
        Boolean preocupacaoFamilia,
        String observacao
) {}
