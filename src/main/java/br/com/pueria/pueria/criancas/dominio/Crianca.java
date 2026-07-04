package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Crianca {

    private static final int TAMANHO_MAXIMO_NOME = 150;
    private static final int SEMANAS_GESTACIONAIS_MINIMAS = 22;
    private static final int SEMANAS_GESTACIONAIS_MAXIMAS = 42;
    private static final int LIMITE_MINIMO_PESO_NASCIMENTO_GRAMAS = 300;
    private static final int LIMITE_MAXIMO_PESO_NASCIMENTO_GRAMAS = 7000;
    private static final int IDADE_MAXIMA_ANOS_MVP = 6;

    private final UUID id;
    private final String nome;
    private final String nomeNormalizado;
    private final LocalDate dataNascimento;
    private final Sexo sexo;
    private final boolean prematura;
    private final Integer semanasGestacionais;
    private final Integer pesoNascimentoGramas;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private Crianca(
            UUID id,
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer pesoNascimentoGramas,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm,
            boolean validarEscopoEtarioMvp
    ) {
        this.id = validarId(id);
        this.nome = validarNome(nome);
        this.nomeNormalizado = normalizarNome(this.nome);
        this.dataNascimento = validarDataNascimento(dataNascimento, validarEscopoEtarioMvp);
        this.sexo = sexo == null ? Sexo.NAO_INFORMADO : sexo;
        this.prematura = prematura;
        this.semanasGestacionais = validarSemanasGestacionais(prematura, semanasGestacionais);
        this.pesoNascimentoGramas = validarPesoNascimento(pesoNascimentoGramas);
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = atualizadoEm;
    }

    public static Crianca cadastrar(
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer pesoNascimentoGramas
    ) {
        return new Crianca(
                UUID.randomUUID(),
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                pesoNascimentoGramas,
                LocalDateTime.now(),
                null,
                true
        );
    }

    public static Crianca restaurar(
            UUID id,
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer pesoNascimentoGramas,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        return new Crianca(
                id,
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                pesoNascimentoGramas,
                criadoEm,
                atualizadoEm,
                false
        );
    }

    public Crianca atualizar(
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer pesoNascimentoGramas
    ) {
        return new Crianca(
                id,
                nome,
                dataNascimento,
                sexo,
                prematura,
                semanasGestacionais,
                pesoNascimentoGramas,
                criadoEm,
                LocalDateTime.now(),
                true
        );
    }

    public IdadeCrianca idadeEm(LocalDate dataReferencia) {
        return IdadeCrianca.calcular(dataNascimento, dataReferencia);
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeNormalizado() {
        return nomeNormalizado;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public boolean isPrematura() {
        return prematura;
    }

    public Integer getSemanasGestacionais() {
        return semanasGestacionais;
    }

    public Integer getPesoNascimentoGramas() {
        return pesoNascimentoGramas;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    private static UUID validarId(UUID id) {
        if (id == null) {
            throw new RegraDominioException("O identificador da criança é obrigatório.");
        }
        return id;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDominioException("O nome da criança é obrigatório.");
        }

        String nomeTratado = nome.trim().replaceAll("\\s+", " ");
        if (nomeTratado.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraDominioException("O nome da criança deve ter no máximo 150 caracteres.");
        }

        return nomeTratado;
    }

    private static String normalizarNome(String nome) {
        return nome.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private static LocalDate validarDataNascimento(LocalDate dataNascimento, boolean validarEscopoEtarioMvp) {
        if (dataNascimento == null) {
            throw new RegraDominioException("A data de nascimento é obrigatória.");
        }

        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new RegraDominioException("A data de nascimento não pode estar no futuro.");
        }

        if (validarEscopoEtarioMvp && Period.between(dataNascimento, LocalDate.now()).getYears() > IDADE_MAXIMA_ANOS_MVP) {
            throw new RegraDominioException("No momento, o Pueria acompanha crianças de até 6 anos neste cadastro.");
        }

        return dataNascimento;
    }

    private static Integer validarSemanasGestacionais(boolean prematura, Integer semanasGestacionais) {
        if (semanasGestacionais == null) {
            throw new RegraDominioException("As semanas gestacionais são obrigatórias.");
        }

        if (semanasGestacionais < SEMANAS_GESTACIONAIS_MINIMAS || semanasGestacionais > SEMANAS_GESTACIONAIS_MAXIMAS) {
            throw new RegraDominioException("As semanas gestacionais devem estar entre 22 e 42.");
        }

        if (prematura && semanasGestacionais >= 37) {
            throw new RegraDominioException("Uma criança marcada como prematura deve ter menos de 37 semanas gestacionais.");
        }

        if (!prematura && semanasGestacionais < 37) {
            throw new RegraDominioException("Uma criança com menos de 37 semanas gestacionais deve ser marcada como prematura.");
        }

        return semanasGestacionais;
    }

    private static Integer validarPesoNascimento(Integer pesoNascimentoGramas) {
        if (pesoNascimentoGramas == null) {
            throw new RegraDominioException("O peso ao nascer é obrigatório.");
        }

        if (pesoNascimentoGramas < LIMITE_MINIMO_PESO_NASCIMENTO_GRAMAS
                || pesoNascimentoGramas > LIMITE_MAXIMO_PESO_NASCIMENTO_GRAMAS) {
            throw new RegraDominioException("O peso de nascimento informado está fora do limite operacional permitido.");
        }

        return pesoNascimentoGramas;
    }
}
