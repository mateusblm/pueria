package br.com.pueria.pueria.criancas.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Crianca {

    private static final int TAMANHO_MAXIMO_NOME = 150;
    private static final int TAMANHO_MAXIMO_OBSERVACOES = 1000;
    private static final int SEMANAS_GESTACIONAIS_MINIMAS = 22;
    private static final int SEMANAS_GESTACIONAIS_MAXIMAS = 42;
    private static final int DIAS_GESTACIONAIS_MINIMOS = 0;
    private static final int DIAS_GESTACIONAIS_MAXIMOS = 6;
    private static final int LIMITE_MINIMO_PESO_NASCIMENTO_GRAMAS = 300;
    private static final int LIMITE_MAXIMO_PESO_NASCIMENTO_GRAMAS = 7000;
    private static final int LIMITE_MAXIMO_CONSULTAS_PRE_NATAL = 60;
    private static final int LIMITE_MAXIMO_DIAS_ALTA_HOSPITALAR = 365;
    private static final BigDecimal COMPRIMENTO_NASCIMENTO_MINIMO_CM = new BigDecimal("20.0");
    private static final BigDecimal COMPRIMENTO_NASCIMENTO_MAXIMO_CM = new BigDecimal("70.0");
    private static final BigDecimal PERIMETRO_CEFALICO_NASCIMENTO_MINIMO_CM = new BigDecimal("20.0");
    private static final BigDecimal PERIMETRO_CEFALICO_NASCIMENTO_MAXIMO_CM = new BigDecimal("50.0");
    private static final int APGAR_MINIMO = 0;
    private static final int APGAR_MAXIMO = 10;
    private static final int IDADE_MAXIMA_ANOS_MVP = 6;

    private final UUID id;
    private final String nome;
    private final String nomeNormalizado;
    private final LocalDate dataNascimento;
    private final Sexo sexo;
    private final boolean prematura;
    private final Integer semanasGestacionais;
    private final Integer diasGestacionais;
    private final TipoParto tipoParto;
    private final Integer pesoNascimentoGramas;
    private final BigDecimal comprimentoNascimentoCm;
    private final BigDecimal perimetroCefalicoNascimentoCm;
    private final Integer apgarUmMinuto;
    private final Integer apgarCincoMinutos;
    private final boolean utiNeonatal;
    private final boolean reanimacaoNeonatal;
    private final boolean ictericiaNeonatal;
    private final boolean dificuldadeRespiratoria;
    private final boolean dificuldadeAmamentacao;
    private final String observacoesNascimento;
    private final boolean preNatalRealizado;
    private final Integer consultasPreNatal;
    private final boolean diabetesGestacional;
    private final boolean hipertensaoGestacional;
    private final boolean infeccaoGestacional;
    private final boolean sangramentoGestacional;
    private final boolean usoAlcoolGestacao;
    private final boolean usoTabacoGestacao;
    private final boolean outrasExposicoesGestacao;
    private final String observacoesGestacao;
    private final Integer diasAltaHospitalar;
    private final boolean retornoHospitalarPrimeiraSemana;
    private final StatusTriagemNeonatal testePezinho;
    private final StatusTriagemNeonatal testeOrelhinha;
    private final StatusTriagemNeonatal testeOlhinho;
    private final StatusTriagemNeonatal testeCoracaozinho;
    private final boolean amamentacaoPrimeiraHora;
    private final AlimentacaoInicial alimentacaoInicial;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private Crianca(
            UUID id,
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer diasGestacionais,
            TipoParto tipoParto,
            Integer pesoNascimentoGramas,
            BigDecimal comprimentoNascimentoCm,
            BigDecimal perimetroCefalicoNascimentoCm,
            Integer apgarUmMinuto,
            Integer apgarCincoMinutos,
            boolean utiNeonatal,
            boolean reanimacaoNeonatal,
            boolean ictericiaNeonatal,
            boolean dificuldadeRespiratoria,
            boolean dificuldadeAmamentacao,
            String observacoesNascimento,
            boolean preNatalRealizado,
            Integer consultasPreNatal,
            boolean diabetesGestacional,
            boolean hipertensaoGestacional,
            boolean infeccaoGestacional,
            boolean sangramentoGestacional,
            boolean usoAlcoolGestacao,
            boolean usoTabacoGestacao,
            boolean outrasExposicoesGestacao,
            String observacoesGestacao,
            Integer diasAltaHospitalar,
            boolean retornoHospitalarPrimeiraSemana,
            StatusTriagemNeonatal testePezinho,
            StatusTriagemNeonatal testeOrelhinha,
            StatusTriagemNeonatal testeOlhinho,
            StatusTriagemNeonatal testeCoracaozinho,
            boolean amamentacaoPrimeiraHora,
            AlimentacaoInicial alimentacaoInicial,
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
        this.diasGestacionais = validarDiasGestacionais(diasGestacionais);
        this.tipoParto = tipoParto == null ? TipoParto.NAO_INFORMADO : tipoParto;
        this.pesoNascimentoGramas = validarPesoNascimento(pesoNascimentoGramas);
        this.comprimentoNascimentoCm = validarMedidaNascimento(comprimentoNascimentoCm, COMPRIMENTO_NASCIMENTO_MINIMO_CM, COMPRIMENTO_NASCIMENTO_MAXIMO_CM, "comprimento ao nascer");
        this.perimetroCefalicoNascimentoCm = validarMedidaNascimento(perimetroCefalicoNascimentoCm, PERIMETRO_CEFALICO_NASCIMENTO_MINIMO_CM, PERIMETRO_CEFALICO_NASCIMENTO_MAXIMO_CM, "perímetro cefálico ao nascer");
        this.apgarUmMinuto = validarApgar(apgarUmMinuto, "Apgar no 1º minuto");
        this.apgarCincoMinutos = validarApgar(apgarCincoMinutos, "Apgar no 5º minuto");
        this.utiNeonatal = utiNeonatal;
        this.reanimacaoNeonatal = reanimacaoNeonatal;
        this.ictericiaNeonatal = ictericiaNeonatal;
        this.dificuldadeRespiratoria = dificuldadeRespiratoria;
        this.dificuldadeAmamentacao = dificuldadeAmamentacao;
        this.observacoesNascimento = validarObservacoes("As observações do nascimento", observacoesNascimento);
        this.preNatalRealizado = preNatalRealizado;
        this.consultasPreNatal = validarConsultasPreNatal(consultasPreNatal);
        this.diabetesGestacional = diabetesGestacional;
        this.hipertensaoGestacional = hipertensaoGestacional;
        this.infeccaoGestacional = infeccaoGestacional;
        this.sangramentoGestacional = sangramentoGestacional;
        this.usoAlcoolGestacao = usoAlcoolGestacao;
        this.usoTabacoGestacao = usoTabacoGestacao;
        this.outrasExposicoesGestacao = outrasExposicoesGestacao;
        this.observacoesGestacao = validarObservacoes("As observações da gestação", observacoesGestacao);
        this.diasAltaHospitalar = validarDiasAltaHospitalar(diasAltaHospitalar);
        this.retornoHospitalarPrimeiraSemana = retornoHospitalarPrimeiraSemana;
        this.testePezinho = testePezinho == null ? StatusTriagemNeonatal.NAO_INFORMADO : testePezinho;
        this.testeOrelhinha = testeOrelhinha == null ? StatusTriagemNeonatal.NAO_INFORMADO : testeOrelhinha;
        this.testeOlhinho = testeOlhinho == null ? StatusTriagemNeonatal.NAO_INFORMADO : testeOlhinho;
        this.testeCoracaozinho = testeCoracaozinho == null ? StatusTriagemNeonatal.NAO_INFORMADO : testeCoracaozinho;
        this.amamentacaoPrimeiraHora = amamentacaoPrimeiraHora;
        this.alimentacaoInicial = alimentacaoInicial == null ? AlimentacaoInicial.NAO_INFORMADO : alimentacaoInicial;
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = atualizadoEm;
    }

    public static Crianca cadastrar(String nome, LocalDate dataNascimento, Sexo sexo, boolean prematura, Integer semanasGestacionais, Integer pesoNascimentoGramas) {
        return cadastrar(
                nome, dataNascimento, sexo, prematura, semanasGestacionais, 0, TipoParto.NAO_INFORMADO,
                pesoNascimentoGramas, new BigDecimal("50.0"), new BigDecimal("34.0"), null, null,
                false, false, false, false, false, null,
                false, null, false, false, false, false, false, false, false, null,
                null, false, StatusTriagemNeonatal.NAO_INFORMADO, StatusTriagemNeonatal.NAO_INFORMADO,
                StatusTriagemNeonatal.NAO_INFORMADO, StatusTriagemNeonatal.NAO_INFORMADO, false,
                AlimentacaoInicial.NAO_INFORMADO
        );
    }

    public static Crianca cadastrar(
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer diasGestacionais,
            TipoParto tipoParto,
            Integer pesoNascimentoGramas,
            BigDecimal comprimentoNascimentoCm,
            BigDecimal perimetroCefalicoNascimentoCm,
            Integer apgarUmMinuto,
            Integer apgarCincoMinutos,
            boolean utiNeonatal,
            boolean reanimacaoNeonatal,
            boolean ictericiaNeonatal,
            boolean dificuldadeRespiratoria,
            boolean dificuldadeAmamentacao,
            String observacoesNascimento,
            boolean preNatalRealizado,
            Integer consultasPreNatal,
            boolean diabetesGestacional,
            boolean hipertensaoGestacional,
            boolean infeccaoGestacional,
            boolean sangramentoGestacional,
            boolean usoAlcoolGestacao,
            boolean usoTabacoGestacao,
            boolean outrasExposicoesGestacao,
            String observacoesGestacao,
            Integer diasAltaHospitalar,
            boolean retornoHospitalarPrimeiraSemana,
            StatusTriagemNeonatal testePezinho,
            StatusTriagemNeonatal testeOrelhinha,
            StatusTriagemNeonatal testeOlhinho,
            StatusTriagemNeonatal testeCoracaozinho,
            boolean amamentacaoPrimeiraHora,
            AlimentacaoInicial alimentacaoInicial
    ) {
        return new Crianca(
                UUID.randomUUID(), nome, dataNascimento, sexo, prematura, semanasGestacionais, diasGestacionais,
                tipoParto, pesoNascimentoGramas, comprimentoNascimentoCm, perimetroCefalicoNascimentoCm,
                apgarUmMinuto, apgarCincoMinutos, utiNeonatal, reanimacaoNeonatal, ictericiaNeonatal,
                dificuldadeRespiratoria, dificuldadeAmamentacao, observacoesNascimento, preNatalRealizado,
                consultasPreNatal, diabetesGestacional, hipertensaoGestacional, infeccaoGestacional,
                sangramentoGestacional, usoAlcoolGestacao, usoTabacoGestacao, outrasExposicoesGestacao,
                observacoesGestacao, diasAltaHospitalar, retornoHospitalarPrimeiraSemana, testePezinho,
                testeOrelhinha, testeOlhinho, testeCoracaozinho, amamentacaoPrimeiraHora, alimentacaoInicial,
                LocalDateTime.now(), null, true
        );
    }

    public static Crianca restaurar(
            UUID id,
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer diasGestacionais,
            TipoParto tipoParto,
            Integer pesoNascimentoGramas,
            BigDecimal comprimentoNascimentoCm,
            BigDecimal perimetroCefalicoNascimentoCm,
            Integer apgarUmMinuto,
            Integer apgarCincoMinutos,
            boolean utiNeonatal,
            boolean reanimacaoNeonatal,
            boolean ictericiaNeonatal,
            boolean dificuldadeRespiratoria,
            boolean dificuldadeAmamentacao,
            String observacoesNascimento,
            boolean preNatalRealizado,
            Integer consultasPreNatal,
            boolean diabetesGestacional,
            boolean hipertensaoGestacional,
            boolean infeccaoGestacional,
            boolean sangramentoGestacional,
            boolean usoAlcoolGestacao,
            boolean usoTabacoGestacao,
            boolean outrasExposicoesGestacao,
            String observacoesGestacao,
            Integer diasAltaHospitalar,
            boolean retornoHospitalarPrimeiraSemana,
            StatusTriagemNeonatal testePezinho,
            StatusTriagemNeonatal testeOrelhinha,
            StatusTriagemNeonatal testeOlhinho,
            StatusTriagemNeonatal testeCoracaozinho,
            boolean amamentacaoPrimeiraHora,
            AlimentacaoInicial alimentacaoInicial,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        return new Crianca(
                id, nome, dataNascimento, sexo, prematura, semanasGestacionais, diasGestacionais, tipoParto,
                pesoNascimentoGramas, comprimentoNascimentoCm, perimetroCefalicoNascimentoCm, apgarUmMinuto,
                apgarCincoMinutos, utiNeonatal, reanimacaoNeonatal, ictericiaNeonatal, dificuldadeRespiratoria,
                dificuldadeAmamentacao, observacoesNascimento, preNatalRealizado, consultasPreNatal,
                diabetesGestacional, hipertensaoGestacional, infeccaoGestacional, sangramentoGestacional,
                usoAlcoolGestacao, usoTabacoGestacao, outrasExposicoesGestacao, observacoesGestacao,
                diasAltaHospitalar, retornoHospitalarPrimeiraSemana, testePezinho, testeOrelhinha,
                testeOlhinho, testeCoracaozinho, amamentacaoPrimeiraHora, alimentacaoInicial, criadoEm,
                atualizadoEm, false
        );
    }

    public Crianca atualizar(String nome, LocalDate dataNascimento, Sexo sexo, boolean prematura, Integer semanasGestacionais, Integer pesoNascimentoGramas) {
        return atualizar(
                nome, dataNascimento, sexo, prematura, semanasGestacionais, this.diasGestacionais, this.tipoParto,
                pesoNascimentoGramas, this.comprimentoNascimentoCm, this.perimetroCefalicoNascimentoCm,
                this.apgarUmMinuto, this.apgarCincoMinutos, this.utiNeonatal, this.reanimacaoNeonatal,
                this.ictericiaNeonatal, this.dificuldadeRespiratoria, this.dificuldadeAmamentacao,
                this.observacoesNascimento, this.preNatalRealizado, this.consultasPreNatal,
                this.diabetesGestacional, this.hipertensaoGestacional, this.infeccaoGestacional,
                this.sangramentoGestacional, this.usoAlcoolGestacao, this.usoTabacoGestacao,
                this.outrasExposicoesGestacao, this.observacoesGestacao, this.diasAltaHospitalar,
                this.retornoHospitalarPrimeiraSemana, this.testePezinho, this.testeOrelhinha,
                this.testeOlhinho, this.testeCoracaozinho, this.amamentacaoPrimeiraHora,
                this.alimentacaoInicial
        );
    }

    public Crianca atualizar(
            String nome,
            LocalDate dataNascimento,
            Sexo sexo,
            boolean prematura,
            Integer semanasGestacionais,
            Integer diasGestacionais,
            TipoParto tipoParto,
            Integer pesoNascimentoGramas,
            BigDecimal comprimentoNascimentoCm,
            BigDecimal perimetroCefalicoNascimentoCm,
            Integer apgarUmMinuto,
            Integer apgarCincoMinutos,
            boolean utiNeonatal,
            boolean reanimacaoNeonatal,
            boolean ictericiaNeonatal,
            boolean dificuldadeRespiratoria,
            boolean dificuldadeAmamentacao,
            String observacoesNascimento,
            boolean preNatalRealizado,
            Integer consultasPreNatal,
            boolean diabetesGestacional,
            boolean hipertensaoGestacional,
            boolean infeccaoGestacional,
            boolean sangramentoGestacional,
            boolean usoAlcoolGestacao,
            boolean usoTabacoGestacao,
            boolean outrasExposicoesGestacao,
            String observacoesGestacao,
            Integer diasAltaHospitalar,
            boolean retornoHospitalarPrimeiraSemana,
            StatusTriagemNeonatal testePezinho,
            StatusTriagemNeonatal testeOrelhinha,
            StatusTriagemNeonatal testeOlhinho,
            StatusTriagemNeonatal testeCoracaozinho,
            boolean amamentacaoPrimeiraHora,
            AlimentacaoInicial alimentacaoInicial
    ) {
        return new Crianca(
                id, nome, dataNascimento, sexo, prematura, semanasGestacionais, diasGestacionais, tipoParto,
                pesoNascimentoGramas, comprimentoNascimentoCm, perimetroCefalicoNascimentoCm, apgarUmMinuto,
                apgarCincoMinutos, utiNeonatal, reanimacaoNeonatal, ictericiaNeonatal, dificuldadeRespiratoria,
                dificuldadeAmamentacao, observacoesNascimento, preNatalRealizado, consultasPreNatal,
                diabetesGestacional, hipertensaoGestacional, infeccaoGestacional, sangramentoGestacional,
                usoAlcoolGestacao, usoTabacoGestacao, outrasExposicoesGestacao, observacoesGestacao,
                diasAltaHospitalar, retornoHospitalarPrimeiraSemana, testePezinho, testeOrelhinha,
                testeOlhinho, testeCoracaozinho, amamentacaoPrimeiraHora, alimentacaoInicial, criadoEm,
                LocalDateTime.now(), true
        );
    }

    public IdadeCrianca idadeEm(LocalDate dataReferencia) {
        return IdadeCrianca.calcular(dataNascimento, dataReferencia);
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getNomeNormalizado() { return nomeNormalizado; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Sexo getSexo() { return sexo; }
    public boolean isPrematura() { return prematura; }
    public Integer getSemanasGestacionais() { return semanasGestacionais; }
    public Integer getDiasGestacionais() { return diasGestacionais; }
    public TipoParto getTipoParto() { return tipoParto; }
    public Integer getPesoNascimentoGramas() { return pesoNascimentoGramas; }
    public BigDecimal getComprimentoNascimentoCm() { return comprimentoNascimentoCm; }
    public BigDecimal getPerimetroCefalicoNascimentoCm() { return perimetroCefalicoNascimentoCm; }
    public Integer getApgarUmMinuto() { return apgarUmMinuto; }
    public Integer getApgarCincoMinutos() { return apgarCincoMinutos; }
    public boolean isUtiNeonatal() { return utiNeonatal; }
    public boolean isReanimacaoNeonatal() { return reanimacaoNeonatal; }
    public boolean isIctericiaNeonatal() { return ictericiaNeonatal; }
    public boolean isDificuldadeRespiratoria() { return dificuldadeRespiratoria; }
    public boolean isDificuldadeAmamentacao() { return dificuldadeAmamentacao; }
    public String getObservacoesNascimento() { return observacoesNascimento; }
    public boolean isPreNatalRealizado() { return preNatalRealizado; }
    public Integer getConsultasPreNatal() { return consultasPreNatal; }
    public boolean isDiabetesGestacional() { return diabetesGestacional; }
    public boolean isHipertensaoGestacional() { return hipertensaoGestacional; }
    public boolean isInfeccaoGestacional() { return infeccaoGestacional; }
    public boolean isSangramentoGestacional() { return sangramentoGestacional; }
    public boolean isUsoAlcoolGestacao() { return usoAlcoolGestacao; }
    public boolean isUsoTabacoGestacao() { return usoTabacoGestacao; }
    public boolean isOutrasExposicoesGestacao() { return outrasExposicoesGestacao; }
    public String getObservacoesGestacao() { return observacoesGestacao; }
    public Integer getDiasAltaHospitalar() { return diasAltaHospitalar; }
    public boolean isRetornoHospitalarPrimeiraSemana() { return retornoHospitalarPrimeiraSemana; }
    public StatusTriagemNeonatal getTestePezinho() { return testePezinho; }
    public StatusTriagemNeonatal getTesteOrelhinha() { return testeOrelhinha; }
    public StatusTriagemNeonatal getTesteOlhinho() { return testeOlhinho; }
    public StatusTriagemNeonatal getTesteCoracaozinho() { return testeCoracaozinho; }
    public boolean isAmamentacaoPrimeiraHora() { return amamentacaoPrimeiraHora; }
    public AlimentacaoInicial getAlimentacaoInicial() { return alimentacaoInicial; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }

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

    private static Integer validarDiasGestacionais(Integer diasGestacionais) {
        if (diasGestacionais == null) {
            throw new RegraDominioException("Os dias gestacionais são obrigatórios.");
        }
        if (diasGestacionais < DIAS_GESTACIONAIS_MINIMOS || diasGestacionais > DIAS_GESTACIONAIS_MAXIMOS) {
            throw new RegraDominioException("Os dias gestacionais devem estar entre 0 e 6.");
        }
        return diasGestacionais;
    }

    private static Integer validarPesoNascimento(Integer pesoNascimentoGramas) {
        if (pesoNascimentoGramas == null) {
            throw new RegraDominioException("O peso ao nascer é obrigatório.");
        }
        if (pesoNascimentoGramas < LIMITE_MINIMO_PESO_NASCIMENTO_GRAMAS || pesoNascimentoGramas > LIMITE_MAXIMO_PESO_NASCIMENTO_GRAMAS) {
            throw new RegraDominioException("O peso de nascimento informado está fora do limite operacional permitido.");
        }
        return pesoNascimentoGramas;
    }

    private static BigDecimal validarMedidaNascimento(BigDecimal medida, BigDecimal minimo, BigDecimal maximo, String nomeMedida) {
        if (medida == null) {
            throw new RegraDominioException("O " + nomeMedida + " é obrigatório.");
        }
        if (medida.compareTo(minimo) < 0 || medida.compareTo(maximo) > 0) {
            throw new RegraDominioException("O " + nomeMedida + " informado está fora do limite operacional permitido.");
        }
        return medida;
    }

    private static Integer validarApgar(Integer apgar, String campo) {
        if (apgar == null) {
            return null;
        }
        if (apgar < APGAR_MINIMO || apgar > APGAR_MAXIMO) {
            throw new RegraDominioException(campo + " deve estar entre 0 e 10.");
        }
        return apgar;
    }

    private static Integer validarConsultasPreNatal(Integer consultasPreNatal) {
        if (consultasPreNatal == null) {
            return null;
        }
        if (consultasPreNatal < 0 || consultasPreNatal > LIMITE_MAXIMO_CONSULTAS_PRE_NATAL) {
            throw new RegraDominioException("O número de consultas de pré-natal está fora do limite operacional permitido.");
        }
        return consultasPreNatal;
    }

    private static Integer validarDiasAltaHospitalar(Integer diasAltaHospitalar) {
        if (diasAltaHospitalar == null) {
            return null;
        }
        if (diasAltaHospitalar < 0 || diasAltaHospitalar > LIMITE_MAXIMO_DIAS_ALTA_HOSPITALAR) {
            throw new RegraDominioException("Os dias até a alta hospitalar estão fora do limite operacional permitido.");
        }
        return diasAltaHospitalar;
    }

    private static String validarObservacoes(String campo, String observacoes) {
        if (observacoes == null || observacoes.isBlank()) {
            return null;
        }
        String observacoesTratadas = observacoes.trim();
        if (observacoesTratadas.length() > TAMANHO_MAXIMO_OBSERVACOES) {
            throw new RegraDominioException(campo + " devem ter no máximo 1000 caracteres.");
        }
        return observacoesTratadas;
    }
}
