package br.com.pueria.pueria.criancas.infraestrutura.persistencia;

import br.com.pueria.pueria.criancas.dominio.Crianca;

final class CriancaMapper {

    private CriancaMapper() {
    }

    static CriancaJpaEntidade paraEntidade(Crianca crianca) {
        CriancaJpaEntidade entidade = new CriancaJpaEntidade();
        entidade.setId(crianca.getId());
        entidade.setNome(crianca.getNome());
        entidade.setNomeNormalizado(crianca.getNomeNormalizado());
        entidade.setDataNascimento(crianca.getDataNascimento());
        entidade.setSexo(crianca.getSexo());
        entidade.setPrematura(crianca.isPrematura());
        entidade.setSemanasGestacionais(crianca.getSemanasGestacionais());
        entidade.setDiasGestacionais(crianca.getDiasGestacionais());
        entidade.setTipoParto(crianca.getTipoParto());
        entidade.setPesoNascimentoGramas(crianca.getPesoNascimentoGramas());
        entidade.setComprimentoNascimentoCm(crianca.getComprimentoNascimentoCm());
        entidade.setPerimetroCefalicoNascimentoCm(crianca.getPerimetroCefalicoNascimentoCm());
        entidade.setApgarUmMinuto(crianca.getApgarUmMinuto());
        entidade.setApgarCincoMinutos(crianca.getApgarCincoMinutos());
        entidade.setUtiNeonatal(crianca.isUtiNeonatal());
        entidade.setReanimacaoNeonatal(crianca.isReanimacaoNeonatal());
        entidade.setIctericiaNeonatal(crianca.isIctericiaNeonatal());
        entidade.setDificuldadeRespiratoria(crianca.isDificuldadeRespiratoria());
        entidade.setDificuldadeAmamentacao(crianca.isDificuldadeAmamentacao());
        entidade.setObservacoesNascimento(crianca.getObservacoesNascimento());
        entidade.setCriadoEm(crianca.getCriadoEm());
        entidade.setAtualizadoEm(crianca.getAtualizadoEm());
        return entidade;
    }

    static Crianca paraDominio(CriancaJpaEntidade entidade) {
        return Crianca.restaurar(
                entidade.getId(),
                entidade.getNome(),
                entidade.getDataNascimento(),
                entidade.getSexo(),
                entidade.isPrematura(),
                entidade.getSemanasGestacionais(),
                entidade.getDiasGestacionais(),
                entidade.getTipoParto(),
                entidade.getPesoNascimentoGramas(),
                entidade.getComprimentoNascimentoCm(),
                entidade.getPerimetroCefalicoNascimentoCm(),
                entidade.getApgarUmMinuto(),
                entidade.getApgarCincoMinutos(),
                entidade.isUtiNeonatal(),
                entidade.isReanimacaoNeonatal(),
                entidade.isIctericiaNeonatal(),
                entidade.isDificuldadeRespiratoria(),
                entidade.isDificuldadeAmamentacao(),
                entidade.getObservacoesNascimento(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
