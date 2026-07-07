package br.com.pueria.pueria.sono.infraestrutura.web;

import br.com.pueria.pueria.sono.aplicacao.RegistroSonoDetalhado;
import br.com.pueria.pueria.sono.dominio.LocalSono;
import br.com.pueria.pueria.sono.dominio.RegistroSono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record RegistroSonoResponse(
        UUID id,
        UUID criancaId,
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
        String observacao,
        Integer minutosSonoNoturno,
        Integer minutosSonoTotal24h,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        AnaliseSonoResponse analise
) {
    static RegistroSonoResponse de(RegistroSonoDetalhado detalhado) {
        RegistroSono registro = detalhado.registro();
        return new RegistroSonoResponse(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getHorarioDormiu(),
                registro.getHorarioAcordou(),
                registro.getQuantidadeCochilos(),
                registro.getMinutosCochilos(),
                registro.getDespertaresNoturnos(),
                registro.getDificuldadeIniciarSono(),
                registro.getRotinaSonoConsistente(),
                registro.getTelasAntesDormir(),
                registro.getLocalSono(),
                registro.getRoncosFrequentes(),
                registro.getPausasRespiratoriasPercebidas(),
                registro.getSonoAgitado(),
                registro.getSonolenciaDiurna(),
                registro.getIrritabilidadeCansaco(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.minutosSonoNoturno(),
                registro.minutosSonoTotal24h(),
                registro.getCriadoEm(),
                registro.getAtualizadoEm(),
                AnaliseSonoResponse.de(detalhado.analise())
        );
    }
}
