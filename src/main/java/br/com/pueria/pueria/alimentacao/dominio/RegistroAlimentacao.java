package br.com.pueria.pueria.alimentacao.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RegistroAlimentacao {

    private final UUID id;
    private final UUID criancaId;
    private final LocalDate dataRegistro;
    private final TipoLeiteAlimentacao tipoLeite;
    private final EstagioAlimentar estagioAlimentar;
    private final Integer idadeInicioAlimentacaoComplementarMeses;
    private final Integer refeicoesPorDia;
    private final Boolean consomeAgua;
    private final Boolean usaMamadeira;
    private final Boolean usaCopo;
    private final Boolean usaColher;
    private final Boolean blwMisto;
    private final Boolean autoalimentacao;
    private final TexturaAlimentar texturaPredominante;
    private final Boolean consomeFrutas;
    private final Boolean consomeLegumesVerduras;
    private final Boolean consomeLegumes;
    private final Boolean consomeVerduras;
    private final Boolean consomeCereaisTuberculos;
    private final Boolean consomeFeijoesLeguminosas;
    private final Boolean consomeCarnesOvos;
    private final Boolean ultraprocessadosFrequentes;
    private final Boolean bebidasAdocadas;
    private final Boolean acucarAdicionado;
    private final Boolean salAdicionado;
    private final Boolean telasDuranteRefeicoes;
    private final Boolean refeicoesEmFamilia;
    private final Boolean rotinaAlimentarRegular;
    private final Boolean seletividadeAlimentar;
    private final Boolean recusaPersistente;
    private final Boolean engasgosFrequentes;
    private final Boolean vomitosRecorrentes;
    private final Boolean constipacao;
    private final Boolean diarreiaRecorrente;
    private final Boolean dificuldadeGanhoPesoPercebida;
    private final Boolean familiaTranquilaGanhoPesoAtual;
    private final Boolean preocupacaoFamilia;
    private final String observacao;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private RegistroAlimentacao(
            UUID id,
            UUID criancaId,
            LocalDate dataRegistro,
            TipoLeiteAlimentacao tipoLeite,
            EstagioAlimentar estagioAlimentar,
            Integer idadeInicioAlimentacaoComplementarMeses,
            Integer refeicoesPorDia,
            Boolean consomeAgua,
            Boolean usaMamadeira,
            Boolean usaCopo,
            Boolean usaColher,
            Boolean blwMisto,
            Boolean autoalimentacao,
            TexturaAlimentar texturaPredominante,
            Boolean consomeFrutas,
            Boolean consomeLegumesVerduras,
            Boolean consomeLegumes,
            Boolean consomeVerduras,
            Boolean consomeCereaisTuberculos,
            Boolean consomeFeijoesLeguminosas,
            Boolean consomeCarnesOvos,
            Boolean ultraprocessadosFrequentes,
            Boolean bebidasAdocadas,
            Boolean acucarAdicionado,
            Boolean salAdicionado,
            Boolean telasDuranteRefeicoes,
            Boolean refeicoesEmFamilia,
            Boolean rotinaAlimentarRegular,
            Boolean seletividadeAlimentar,
            Boolean recusaPersistente,
            Boolean engasgosFrequentes,
            Boolean vomitosRecorrentes,
            Boolean constipacao,
            Boolean diarreiaRecorrente,
            Boolean dificuldadeGanhoPesoPercebida,
            Boolean familiaTranquilaGanhoPesoAtual,
            Boolean preocupacaoFamilia,
            String observacao,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do registro alimentar é obrigatório.");
        this.criancaId = Objects.requireNonNull(criancaId, "A criança é obrigatória.");
        this.dataRegistro = validarData(dataRegistro);
        this.tipoLeite = tipoLeite == null ? TipoLeiteAlimentacao.NAO_INFORMADO : tipoLeite;
        this.estagioAlimentar = estagioAlimentar == null ? EstagioAlimentar.NAO_INFORMADO : estagioAlimentar;
        this.idadeInicioAlimentacaoComplementarMeses = validarInteiro(idadeInicioAlimentacaoComplementarMeses, 0, 24, "idade de início da alimentação complementar");
        this.refeicoesPorDia = validarInteiro(refeicoesPorDia, 0, 10, "número de refeições por dia");
        this.consomeAgua = consomeAgua;
        this.usaMamadeira = usaMamadeira;
        this.usaCopo = usaCopo;
        this.usaColher = usaColher;
        this.blwMisto = blwMisto;
        this.autoalimentacao = autoalimentacao;
        this.texturaPredominante = texturaPredominante == null ? TexturaAlimentar.NAO_INFORMADO : texturaPredominante;
        this.consomeFrutas = consomeFrutas;
        this.consomeLegumesVerduras = consomeLegumesVerduras;
        this.consomeLegumes = consomeLegumes;
        this.consomeVerduras = consomeVerduras;
        this.consomeCereaisTuberculos = consomeCereaisTuberculos;
        this.consomeFeijoesLeguminosas = consomeFeijoesLeguminosas;
        this.consomeCarnesOvos = consomeCarnesOvos;
        this.ultraprocessadosFrequentes = ultraprocessadosFrequentes;
        this.bebidasAdocadas = bebidasAdocadas;
        this.acucarAdicionado = acucarAdicionado;
        this.salAdicionado = salAdicionado;
        this.telasDuranteRefeicoes = telasDuranteRefeicoes;
        this.refeicoesEmFamilia = refeicoesEmFamilia;
        this.rotinaAlimentarRegular = rotinaAlimentarRegular;
        this.seletividadeAlimentar = seletividadeAlimentar;
        this.recusaPersistente = recusaPersistente;
        this.engasgosFrequentes = engasgosFrequentes;
        this.vomitosRecorrentes = vomitosRecorrentes;
        this.constipacao = constipacao;
        this.diarreiaRecorrente = diarreiaRecorrente;
        this.dificuldadeGanhoPesoPercebida = dificuldadeGanhoPesoPercebida;
        this.familiaTranquilaGanhoPesoAtual = familiaTranquilaGanhoPesoAtual;
        this.preocupacaoFamilia = preocupacaoFamilia;
        this.observacao = tratarObservacao(observacao);
        this.criadoEm = Objects.requireNonNull(criadoEm, "A data de criação é obrigatória.");
        this.atualizadoEm = atualizadoEm;
    }

    public static RegistroAlimentacao registrar(UUID criancaId, DadosAlimentacao dados) {
        return new RegistroAlimentacao(
                UUID.randomUUID(),
                criancaId,
                dados.dataRegistro(),
                dados.tipoLeite(),
                dados.estagioAlimentar(),
                dados.idadeInicioAlimentacaoComplementarMeses(),
                dados.refeicoesPorDia(),
                dados.consomeAgua(),
                dados.usaMamadeira(),
                dados.usaCopo(),
                dados.usaColher(),
                dados.blwMisto(),
                dados.autoalimentacao(),
                dados.texturaPredominante(),
                dados.consomeFrutas(),
                dados.consomeLegumesVerduras(),
                dados.consomeLegumes(),
                dados.consomeVerduras(),
                dados.consomeCereaisTuberculos(),
                dados.consomeFeijoesLeguminosas(),
                dados.consomeCarnesOvos(),
                dados.ultraprocessadosFrequentes(),
                dados.bebidasAdocadas(),
                dados.acucarAdicionado(),
                dados.salAdicionado(),
                dados.telasDuranteRefeicoes(),
                dados.refeicoesEmFamilia(),
                dados.rotinaAlimentarRegular(),
                dados.seletividadeAlimentar(),
                dados.recusaPersistente(),
                dados.engasgosFrequentes(),
                dados.vomitosRecorrentes(),
                dados.constipacao(),
                dados.diarreiaRecorrente(),
                dados.dificuldadeGanhoPesoPercebida(),
                dados.familiaTranquilaGanhoPesoAtual(),
                dados.preocupacaoFamilia(),
                dados.observacao(),
                LocalDateTime.now(),
                null
        );
    }

    public static RegistroAlimentacao restaurar(UUID id, UUID criancaId, DadosAlimentacao dados, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new RegistroAlimentacao(
                id,
                criancaId,
                dados.dataRegistro(),
                dados.tipoLeite(),
                dados.estagioAlimentar(),
                dados.idadeInicioAlimentacaoComplementarMeses(),
                dados.refeicoesPorDia(),
                dados.consomeAgua(),
                dados.usaMamadeira(),
                dados.usaCopo(),
                dados.usaColher(),
                dados.blwMisto(),
                dados.autoalimentacao(),
                dados.texturaPredominante(),
                dados.consomeFrutas(),
                dados.consomeLegumesVerduras(),
                dados.consomeLegumes(),
                dados.consomeVerduras(),
                dados.consomeCereaisTuberculos(),
                dados.consomeFeijoesLeguminosas(),
                dados.consomeCarnesOvos(),
                dados.ultraprocessadosFrequentes(),
                dados.bebidasAdocadas(),
                dados.acucarAdicionado(),
                dados.salAdicionado(),
                dados.telasDuranteRefeicoes(),
                dados.refeicoesEmFamilia(),
                dados.rotinaAlimentarRegular(),
                dados.seletividadeAlimentar(),
                dados.recusaPersistente(),
                dados.engasgosFrequentes(),
                dados.vomitosRecorrentes(),
                dados.constipacao(),
                dados.diarreiaRecorrente(),
                dados.dificuldadeGanhoPesoPercebida(),
                dados.familiaTranquilaGanhoPesoAtual(),
                dados.preocupacaoFamilia(),
                dados.observacao(),
                criadoEm,
                atualizadoEm
        );
    }

    public RegistroAlimentacao atualizar(DadosAlimentacao dados) {
        return new RegistroAlimentacao(
                id,
                criancaId,
                dados.dataRegistro(),
                dados.tipoLeite(),
                dados.estagioAlimentar(),
                dados.idadeInicioAlimentacaoComplementarMeses(),
                dados.refeicoesPorDia(),
                dados.consomeAgua(),
                dados.usaMamadeira(),
                dados.usaCopo(),
                dados.usaColher(),
                dados.blwMisto(),
                dados.autoalimentacao(),
                dados.texturaPredominante(),
                dados.consomeFrutas(),
                dados.consomeLegumesVerduras(),
                dados.consomeLegumes(),
                dados.consomeVerduras(),
                dados.consomeCereaisTuberculos(),
                dados.consomeFeijoesLeguminosas(),
                dados.consomeCarnesOvos(),
                dados.ultraprocessadosFrequentes(),
                dados.bebidasAdocadas(),
                dados.acucarAdicionado(),
                dados.salAdicionado(),
                dados.telasDuranteRefeicoes(),
                dados.refeicoesEmFamilia(),
                dados.rotinaAlimentarRegular(),
                dados.seletividadeAlimentar(),
                dados.recusaPersistente(),
                dados.engasgosFrequentes(),
                dados.vomitosRecorrentes(),
                dados.constipacao(),
                dados.diarreiaRecorrente(),
                dados.dificuldadeGanhoPesoPercebida(),
                dados.familiaTranquilaGanhoPesoAtual(),
                dados.preocupacaoFamilia(),
                dados.observacao(),
                criadoEm,
                LocalDateTime.now()
        );
    }

    private static LocalDate validarData(LocalDate dataRegistro) {
        if (dataRegistro == null) {
            throw new RegraDominioException("A data do registro alimentar é obrigatória.");
        }
        if (dataRegistro.isAfter(LocalDate.now())) {
            throw new RegraDominioException("A data do registro alimentar não pode estar no futuro.");
        }
        return dataRegistro;
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
    public TipoLeiteAlimentacao getTipoLeite() { return tipoLeite; }
    public EstagioAlimentar getEstagioAlimentar() { return estagioAlimentar; }
    public Integer getIdadeInicioAlimentacaoComplementarMeses() { return idadeInicioAlimentacaoComplementarMeses; }
    public Integer getRefeicoesPorDia() { return refeicoesPorDia; }
    public Boolean getConsomeAgua() { return consomeAgua; }
    public Boolean getUsaMamadeira() { return usaMamadeira; }
    public Boolean getUsaCopo() { return usaCopo; }
    public Boolean getUsaColher() { return usaColher; }
    public Boolean getBlwMisto() { return blwMisto; }
    public Boolean getAutoalimentacao() { return autoalimentacao; }
    public TexturaAlimentar getTexturaPredominante() { return texturaPredominante; }
    public Boolean getConsomeFrutas() { return consomeFrutas; }
    public Boolean getConsomeLegumesVerduras() { return consomeLegumesVerduras; }
    public Boolean getConsomeLegumes() { return consomeLegumes; }
    public Boolean getConsomeVerduras() { return consomeVerduras; }
    public Boolean getConsomeCereaisTuberculos() { return consomeCereaisTuberculos; }
    public Boolean getConsomeFeijoesLeguminosas() { return consomeFeijoesLeguminosas; }
    public Boolean getConsomeCarnesOvos() { return consomeCarnesOvos; }
    public Boolean getUltraprocessadosFrequentes() { return ultraprocessadosFrequentes; }
    public Boolean getBebidasAdocadas() { return bebidasAdocadas; }
    public Boolean getAcucarAdicionado() { return acucarAdicionado; }
    public Boolean getSalAdicionado() { return salAdicionado; }
    public Boolean getTelasDuranteRefeicoes() { return telasDuranteRefeicoes; }
    public Boolean getRefeicoesEmFamilia() { return refeicoesEmFamilia; }
    public Boolean getRotinaAlimentarRegular() { return rotinaAlimentarRegular; }
    public Boolean getSeletividadeAlimentar() { return seletividadeAlimentar; }
    public Boolean getRecusaPersistente() { return recusaPersistente; }
    public Boolean getEngasgosFrequentes() { return engasgosFrequentes; }
    public Boolean getVomitosRecorrentes() { return vomitosRecorrentes; }
    public Boolean getConstipacao() { return constipacao; }
    public Boolean getDiarreiaRecorrente() { return diarreiaRecorrente; }
    public Boolean getDificuldadeGanhoPesoPercebida() { return dificuldadeGanhoPesoPercebida; }
    public Boolean getFamiliaTranquilaGanhoPesoAtual() { return familiaTranquilaGanhoPesoAtual; }
    public Boolean getPreocupacaoFamilia() { return preocupacaoFamilia; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
