package br.com.pueria.pueria.sono.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class RegistroSono {

    private final UUID id;
    private final UUID criancaId;
    private final LocalDate dataRegistro;
    private final LocalTime horarioDormiu;
    private final LocalTime horarioAcordou;
    private final Integer quantidadeCochilos;
    private final Integer minutosCochilos;
    private final Integer despertaresNoturnos;
    private final Boolean dificuldadeIniciarSono;
    private final Boolean rotinaSonoConsistente;
    private final Boolean telasAntesDormir;
    private final LocalSono localSono;
    private final Boolean roncosFrequentes;
    private final Boolean pausasRespiratoriasPercebidas;
    private final Boolean sonoAgitado;
    private final Boolean sonolenciaDiurna;
    private final Boolean irritabilidadeCansaco;
    private final Boolean preocupacaoFamilia;
    private final String observacao;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private RegistroSono(
            UUID id,
            UUID criancaId,
            DadosSono dados,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do registro de sono é obrigatório.");
        this.criancaId = Objects.requireNonNull(criancaId, "A criança é obrigatória.");
        this.dataRegistro = validarData(dados.dataRegistro());
        this.horarioDormiu = dados.horarioDormiu();
        this.horarioAcordou = dados.horarioAcordou();
        validarHorarios(this.horarioDormiu, this.horarioAcordou);
        this.quantidadeCochilos = validarInteiro(dados.quantidadeCochilos(), 0, 12, "quantidade de cochilos");
        this.minutosCochilos = validarInteiro(dados.minutosCochilos(), 0, 1200, "tempo total de cochilos");
        this.despertaresNoturnos = validarInteiro(dados.despertaresNoturnos(), 0, 30, "despertares noturnos");
        this.dificuldadeIniciarSono = dados.dificuldadeIniciarSono();
        this.rotinaSonoConsistente = dados.rotinaSonoConsistente();
        this.telasAntesDormir = dados.telasAntesDormir();
        this.localSono = dados.localSono() == null ? LocalSono.NAO_INFORMADO : dados.localSono();
        this.roncosFrequentes = dados.roncosFrequentes();
        this.pausasRespiratoriasPercebidas = dados.pausasRespiratoriasPercebidas();
        this.sonoAgitado = dados.sonoAgitado();
        this.sonolenciaDiurna = dados.sonolenciaDiurna();
        this.irritabilidadeCansaco = dados.irritabilidadeCansaco();
        this.preocupacaoFamilia = dados.preocupacaoFamilia();
        this.observacao = tratarObservacao(dados.observacao());
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = atualizadoEm;
    }

    public static RegistroSono registrar(UUID criancaId, DadosSono dados) {
        return new RegistroSono(UUID.randomUUID(), criancaId, dados, LocalDateTime.now(), null);
    }

    public static RegistroSono restaurar(UUID id, UUID criancaId, DadosSono dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new RegistroSono(id, criancaId, dados, criadoEm, atualizadoEm);
    }

    public RegistroSono atualizar(DadosSono dados) {
        return new RegistroSono(id, criancaId, dados, criadoEm, LocalDateTime.now());
    }

    public Integer minutosSonoNoturno() {
        if (horarioDormiu == null || horarioAcordou == null) {
            return null;
        }

        long minutos = Duration.between(horarioDormiu, horarioAcordou).toMinutes();
        if (minutos < 0) {
            minutos += 24 * 60;
        }
        return Math.toIntExact(minutos);
    }

    public Integer minutosSonoTotal24h() {
        Integer noturno = minutosSonoNoturno();
        if (noturno == null && minutosCochilos == null) {
            return null;
        }
        return (noturno == null ? 0 : noturno) + (minutosCochilos == null ? 0 : minutosCochilos);
    }

    private static LocalDate validarData(LocalDate dataRegistro) {
        if (dataRegistro == null) {
            throw new RegraDominioException("A data do registro de sono é obrigatória.");
        }
        if (dataRegistro.isAfter(LocalDate.now())) {
            throw new RegraDominioException("A data do registro de sono não pode estar no futuro.");
        }
        return dataRegistro;
    }

    private static void validarHorarios(LocalTime dormiu, LocalTime acordou) {
        if ((dormiu == null && acordou != null) || (dormiu != null && acordou == null)) {
            throw new RegraDominioException("Informe horário de dormir e de acordar para calcular o sono noturno.");
        }
        if (dormiu != null && dormiu.equals(acordou)) {
            throw new RegraDominioException("O horário de dormir e o horário de acordar não podem ser iguais.");
        }
    }

    private static Integer validarInteiro(Integer valor, int minimo, int maximo, String nome) {
        if (valor == null) {
            return null;
        }
        if (valor < minimo || valor > maximo) {
            throw new RegraDominioException("O campo " + nome + " está fora do limite permitido.");
        }
        return valor;
    }

    private static String tratarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        String texto = observacao.trim().replaceAll("\\s+", " ");
        if (texto.length() > 1000) {
            throw new RegraDominioException("A observação deve ter no máximo 1000 caracteres.");
        }
        return texto;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public LocalTime getHorarioDormiu() { return horarioDormiu; }
    public LocalTime getHorarioAcordou() { return horarioAcordou; }
    public Integer getQuantidadeCochilos() { return quantidadeCochilos; }
    public Integer getMinutosCochilos() { return minutosCochilos; }
    public Integer getDespertaresNoturnos() { return despertaresNoturnos; }
    public Boolean getDificuldadeIniciarSono() { return dificuldadeIniciarSono; }
    public Boolean getRotinaSonoConsistente() { return rotinaSonoConsistente; }
    public Boolean getTelasAntesDormir() { return telasAntesDormir; }
    public LocalSono getLocalSono() { return localSono; }
    public Boolean getRoncosFrequentes() { return roncosFrequentes; }
    public Boolean getPausasRespiratoriasPercebidas() { return pausasRespiratoriasPercebidas; }
    public Boolean getSonoAgitado() { return sonoAgitado; }
    public Boolean getSonolenciaDiurna() { return sonolenciaDiurna; }
    public Boolean getIrritabilidadeCansaco() { return irritabilidadeCansaco; }
    public Boolean getPreocupacaoFamilia() { return preocupacaoFamilia; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
