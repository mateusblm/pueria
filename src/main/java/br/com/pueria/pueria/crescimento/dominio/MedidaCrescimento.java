package br.com.pueria.pueria.crescimento.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MedidaCrescimento {

    private static final BigDecimal PESO_MINIMO_KG = new BigDecimal("0.3");
    private static final BigDecimal PESO_MAXIMO_KG = new BigDecimal("80.0");
    private static final BigDecimal COMPRIMENTO_MINIMO_CM = new BigDecimal("20.0");
    private static final BigDecimal COMPRIMENTO_MAXIMO_CM = new BigDecimal("140.0");
    private static final BigDecimal PERIMETRO_MINIMO_CM = new BigDecimal("20.0");
    private static final BigDecimal PERIMETRO_MAXIMO_CM = new BigDecimal("65.0");

    private final UUID id;
    private final UUID criancaId;
    private final LocalDate dataMedicao;
    private final BigDecimal pesoKg;
    private final BigDecimal comprimentoCm;
    private final BigDecimal perimetroCefalicoCm;
    private final OrigemMedidaCrescimento origem;
    private final ResponsavelMedicaoCrescimento responsavelMedicao;
    private final String observacao;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private MedidaCrescimento(
            UUID id,
            UUID criancaId,
            LocalDate dataMedicao,
            BigDecimal pesoKg,
            BigDecimal comprimentoCm,
            BigDecimal perimetroCefalicoCm,
            OrigemMedidaCrescimento origem,
            ResponsavelMedicaoCrescimento responsavelMedicao,
            String observacao,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        this.id = Objects.requireNonNull(id, "O identificador da medida é obrigatório.");
        this.criancaId = Objects.requireNonNull(criancaId, "A criança é obrigatória.");
        this.dataMedicao = validarData(dataMedicao);
        this.pesoKg = validarMedida(pesoKg, PESO_MINIMO_KG, PESO_MAXIMO_KG, "peso");
        this.comprimentoCm = validarMedida(comprimentoCm, COMPRIMENTO_MINIMO_CM, COMPRIMENTO_MAXIMO_CM, "comprimento/estatura");
        this.perimetroCefalicoCm = validarMedida(perimetroCefalicoCm, PERIMETRO_MINIMO_CM, PERIMETRO_MAXIMO_CM, "perímetro cefálico");
        validarAoMenosUmaMedida(this.pesoKg, this.comprimentoCm, this.perimetroCefalicoCm);
        this.origem = origem == null ? OrigemMedidaCrescimento.CONSULTA : origem;
        this.responsavelMedicao = responsavelMedicao == null ? ResponsavelMedicaoCrescimento.NAO_INFORMADO : responsavelMedicao;
        this.observacao = tratarObservacao(observacao);
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = atualizadoEm;
    }

    public static MedidaCrescimento registrar(
            UUID criancaId,
            LocalDate dataMedicao,
            BigDecimal pesoKg,
            BigDecimal comprimentoCm,
            BigDecimal perimetroCefalicoCm,
            OrigemMedidaCrescimento origem,
            ResponsavelMedicaoCrescimento responsavelMedicao,
            String observacao
    ) {
        return new MedidaCrescimento(
                UUID.randomUUID(),
                criancaId,
                dataMedicao,
                pesoKg,
                comprimentoCm,
                perimetroCefalicoCm,
                origem,
                responsavelMedicao,
                observacao,
                LocalDateTime.now(),
                null
        );
    }

    public static MedidaCrescimento registrar(
            UUID criancaId,
            LocalDate dataMedicao,
            BigDecimal pesoKg,
            BigDecimal comprimentoCm,
            BigDecimal perimetroCefalicoCm,
            OrigemMedidaCrescimento origem,
            String observacao
    ) {
        return registrar(criancaId, dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, ResponsavelMedicaoCrescimento.NAO_INFORMADO, observacao);
    }

    public static MedidaCrescimento restaurar(
            UUID id,
            UUID criancaId,
            LocalDate dataMedicao,
            BigDecimal pesoKg,
            BigDecimal comprimentoCm,
            BigDecimal perimetroCefalicoCm,
            OrigemMedidaCrescimento origem,
            ResponsavelMedicaoCrescimento responsavelMedicao,
            String observacao,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        return new MedidaCrescimento(id, criancaId, dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, responsavelMedicao, observacao, criadoEm, atualizadoEm);
    }

    public MedidaCrescimento atualizar(
            LocalDate dataMedicao,
            BigDecimal pesoKg,
            BigDecimal comprimentoCm,
            BigDecimal perimetroCefalicoCm,
            OrigemMedidaCrescimento origem,
            ResponsavelMedicaoCrescimento responsavelMedicao,
            String observacao
    ) {
        return new MedidaCrescimento(id, criancaId, dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, responsavelMedicao, observacao, criadoEm, LocalDateTime.now());
    }

    public MedidaCrescimento atualizar(
            LocalDate dataMedicao,
            BigDecimal pesoKg,
            BigDecimal comprimentoCm,
            BigDecimal perimetroCefalicoCm,
            OrigemMedidaCrescimento origem,
            String observacao
    ) {
        return atualizar(dataMedicao, pesoKg, comprimentoCm, perimetroCefalicoCm, origem, ResponsavelMedicaoCrescimento.NAO_INFORMADO, observacao);
    }

    private static LocalDate validarData(LocalDate dataMedicao) {
        if (dataMedicao == null) {
            throw new RegraDominioException("A data da medição é obrigatória.");
        }
        if (dataMedicao.isAfter(LocalDate.now())) {
            throw new RegraDominioException("A data da medição não pode estar no futuro.");
        }
        return dataMedicao;
    }

    private static BigDecimal validarMedida(BigDecimal valor, BigDecimal minimo, BigDecimal maximo, String nome) {
        if (valor == null) {
            return null;
        }
        if (valor.compareTo(minimo) < 0 || valor.compareTo(maximo) > 0) {
            throw new RegraDominioException("A medida de " + nome + " está fora do limite operacional permitido.");
        }
        return valor;
    }

    private static void validarAoMenosUmaMedida(BigDecimal pesoKg, BigDecimal comprimentoCm, BigDecimal perimetroCefalicoCm) {
        if (pesoKg == null && comprimentoCm == null && perimetroCefalicoCm == null) {
            throw new RegraDominioException("Informe pelo menos uma medida de crescimento.");
        }
    }

    private static String tratarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        String texto = observacao.trim().replaceAll("\\s+", " ");
        if (texto.length() > 500) {
            throw new RegraDominioException("A observação deve ter no máximo 500 caracteres.");
        }
        return texto;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public LocalDate getDataMedicao() { return dataMedicao; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public BigDecimal getComprimentoCm() { return comprimentoCm; }
    public BigDecimal getPerimetroCefalicoCm() { return perimetroCefalicoCm; }
    public OrigemMedidaCrescimento getOrigem() { return origem; }
    public ResponsavelMedicaoCrescimento getResponsavelMedicao() { return responsavelMedicao; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
