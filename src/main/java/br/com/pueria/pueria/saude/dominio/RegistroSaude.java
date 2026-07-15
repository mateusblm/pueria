package br.com.pueria.pueria.saude.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RegistroSaude {
    private final UUID id;
    private final UUID criancaId;
    private final TipoRegistroSaude tipo;
    private final LocalDate dataRegistro;
    private final String descricao;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private RegistroSaude(UUID id, UUID criancaId, DadosRegistroSaude dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = Objects.requireNonNull(id, "O identificador do registro de saúde é obrigatório.");
        this.criancaId = Objects.requireNonNull(criancaId, "A criança é obrigatória.");
        this.tipo = Objects.requireNonNull(dados.tipo(), "O tipo de registro é obrigatório.");
        this.dataRegistro = validarData(dados.dataRegistro());
        this.descricao = validarDescricao(dados.descricao());
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = atualizadoEm;
    }

    public static RegistroSaude registrar(UUID criancaId, DadosRegistroSaude dados) {
        return new RegistroSaude(UUID.randomUUID(), criancaId, dados, LocalDateTime.now(), null);
    }

    public static RegistroSaude restaurar(UUID id, UUID criancaId, DadosRegistroSaude dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new RegistroSaude(id, criancaId, dados, criadoEm, atualizadoEm);
    }

    public RegistroSaude atualizar(DadosRegistroSaude dados) {
        return new RegistroSaude(id, criancaId, dados, criadoEm, LocalDateTime.now());
    }

    private LocalDate validarData(LocalDate data) {
        if (data == null) throw new RegraDominioException("A data do registro é obrigatória.");
        if (data.isAfter(LocalDate.now())) throw new RegraDominioException("A data do registro não pode estar no futuro.");
        return data;
    }

    private String validarDescricao(String valor) {
        if (valor == null || valor.isBlank()) throw new RegraDominioException("Escreva o registro antes de salvar.");
        String texto = valor.trim();
        if (texto.length() > 4000) throw new RegraDominioException("O registro deve ter no máximo 4000 caracteres.");
        return texto;
    }

    public UUID getId() { return id; }
    public UUID getCriancaId() { return criancaId; }
    public TipoRegistroSaude getTipo() { return tipo; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
