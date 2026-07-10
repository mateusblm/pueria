package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import br.com.pueria.pueria.alimentacao.dominio.AlimentoRegistroAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.DadosAlimentacao;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;

import java.util.List;

class RegistroAlimentacaoMapper {

    static RegistroAlimentacao paraDominio(RegistroAlimentacaoJpaEntidade entidade) {
        return RegistroAlimentacao.restaurar(
                entidade.getId(),
                entidade.getCriancaId(),
                dados(entidade),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }

    static RegistroAlimentacaoJpaEntidade paraEntidade(RegistroAlimentacao registro) {
        return new RegistroAlimentacaoJpaEntidade(
                registro.getId(),
                registro.getCriancaId(),
                registro.getDataRegistro(),
                registro.getTipoLeite(),
                registro.getEstagioAlimentar(),
                registro.getIdadeInicioAlimentacaoComplementarMeses(),
                registro.getRefeicoesPorDia(),
                registro.getConsomeAgua(),
                registro.getUsaMamadeira(),
                registro.getUsaCopo(),
                registro.getUsaColher(),
                registro.getBlwMisto(),
                registro.getAutoalimentacao(),
                registro.getTexturaPredominante(),
                registro.getConsomeFrutas(),
                registro.getConsomeLegumesVerduras(),
                registro.getConsomeLegumes(),
                registro.getConsomeVerduras(),
                registro.getConsomeCereaisTuberculos(),
                registro.getConsomeFeijoesLeguminosas(),
                registro.getConsomeCarnesOvos(),
                registro.getUltraprocessadosFrequentes(),
                registro.getBebidasAdocadas(),
                registro.getAcucarAdicionado(),
                registro.getSalAdicionado(),
                registro.getTelasDuranteRefeicoes(),
                registro.getRefeicoesEmFamilia(),
                registro.getRotinaAlimentarRegular(),
                registro.getSeletividadeAlimentar(),
                registro.getRecusaPersistente(),
                registro.getEngasgosFrequentes(),
                registro.getVomitosRecorrentes(),
                registro.getConstipacao(),
                registro.getDiarreiaRecorrente(),
                registro.getDificuldadeGanhoPesoPercebida(),
                registro.getFamiliaTranquilaGanhoPesoAtual(),
                registro.getPreocupacaoFamilia(),
                registro.getObservacao(),
                registro.getTipoOrigemAlimento(),
                alimentosParaEntidade(registro.getAlimentosOferecidos()),
                registro.getCriadoEm(),
                registro.getAtualizadoEm()
        );
    }

    private static DadosAlimentacao dados(RegistroAlimentacaoJpaEntidade entidade) {
        return new DadosAlimentacao(
                entidade.getDataRegistro(),
                entidade.getTipoLeite(),
                entidade.getEstagioAlimentar(),
                entidade.getIdadeInicioAlimentacaoComplementarMeses(),
                entidade.getRefeicoesPorDia(),
                entidade.getConsomeAgua(),
                entidade.getUsaMamadeira(),
                entidade.getUsaCopo(),
                entidade.getUsaColher(),
                entidade.getBlwMisto(),
                entidade.getAutoalimentacao(),
                entidade.getTexturaPredominante(),
                entidade.getConsomeFrutas(),
                entidade.getConsomeLegumesVerduras(),
                entidade.getConsomeLegumes(),
                entidade.getConsomeVerduras(),
                entidade.getConsomeCereaisTuberculos(),
                entidade.getConsomeFeijoesLeguminosas(),
                entidade.getConsomeCarnesOvos(),
                entidade.getUltraprocessadosFrequentes(),
                entidade.getBebidasAdocadas(),
                entidade.getAcucarAdicionado(),
                entidade.getSalAdicionado(),
                entidade.getTelasDuranteRefeicoes(),
                entidade.getRefeicoesEmFamilia(),
                entidade.getRotinaAlimentarRegular(),
                entidade.getSeletividadeAlimentar(),
                entidade.getRecusaPersistente(),
                entidade.getEngasgosFrequentes(),
                entidade.getVomitosRecorrentes(),
                entidade.getConstipacao(),
                entidade.getDiarreiaRecorrente(),
                entidade.getDificuldadeGanhoPesoPercebida(),
                entidade.getFamiliaTranquilaGanhoPesoAtual(),
                entidade.getPreocupacaoFamilia(),
                entidade.getObservacao(),
                entidade.getTipoOrigemAlimento(),
                alimentosParaDominio(entidade.getAlimentosOferecidos())
        );
    }

    private static List<AlimentoRegistroAlimentacaoJpaEmbeddable> alimentosParaEntidade(List<AlimentoRegistroAlimentacao> alimentos) {
        return alimentos.stream()
                .map((alimento) -> new AlimentoRegistroAlimentacaoJpaEmbeddable(
                        alimento.codigo(),
                        alimento.nome(),
                        alimento.grupo(),
                        alimento.alergenico(),
                        alimento.dataIntroducao(),
                        alimento.formaPreparo(),
                        alimento.textura(),
                        alimento.quantidadeAproximada(),
                        alimento.aceitacao(),
                        alimento.repetiuOutroDia(),
                        alimento.sintomasPele(),
                        alimento.sintomasIntestinais(),
                        alimento.sintomasRespiratorios(),
                        alimento.alteracaoSono(),
                        alimento.alteracaoComportamento(),
                        alimento.observacao()
                ))
                .toList();
    }

    private static List<AlimentoRegistroAlimentacao> alimentosParaDominio(List<AlimentoRegistroAlimentacaoJpaEmbeddable> alimentos) {
        return alimentos.stream()
                .map((alimento) -> new AlimentoRegistroAlimentacao(
                        alimento.getCodigo(),
                        alimento.getNome(),
                        alimento.getGrupo(),
                        alimento.getAlergenico(),
                        alimento.getDataIntroducao(),
                        alimento.getFormaPreparo(),
                        alimento.getTextura(),
                        alimento.getQuantidadeAproximada(),
                        alimento.getAceitacao(),
                        alimento.getRepetiuOutroDia(),
                        alimento.getSintomasPele(),
                        alimento.getSintomasIntestinais(),
                        alimento.getSintomasRespiratorios(),
                        alimento.getAlteracaoSono(),
                        alimento.getAlteracaoComportamento(),
                        alimento.getObservacao()
                ))
                .toList();
    }
}
