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
        entidade.setTipoGestacao(crianca.getContextoClinico().tipoGestacao());
        entidade.setStatusT21(crianca.getContextoClinico().statusT21());
        entidade.setStatusTurner(crianca.getContextoClinico().statusTurner());
        entidade.setOutraCondicaoRelevante(crianca.getContextoClinico().outraCondicaoRelevante());
        entidade.setObservacoesCondicaoRelevante(crianca.getContextoClinico().observacoesCondicaoRelevante());
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
        entidade.setPreNatalRealizado(crianca.isPreNatalRealizado());
        entidade.setConsultasPreNatal(crianca.getConsultasPreNatal());
        entidade.setDiabetesGestacional(crianca.isDiabetesGestacional());
        entidade.setHipertensaoGestacional(crianca.isHipertensaoGestacional());
        entidade.setInfeccaoGestacional(crianca.isInfeccaoGestacional());
        entidade.setSangramentoGestacional(crianca.isSangramentoGestacional());
        entidade.setUsoAlcoolGestacao(crianca.isUsoAlcoolGestacao());
        entidade.setUsoTabacoGestacao(crianca.isUsoTabacoGestacao());
        entidade.setOutrasExposicoesGestacao(crianca.isOutrasExposicoesGestacao());
        entidade.setObservacoesGestacao(crianca.getObservacoesGestacao());
        entidade.setDiasAltaHospitalar(crianca.getDiasAltaHospitalar());
        entidade.setRetornoHospitalarPrimeiraSemana(crianca.isRetornoHospitalarPrimeiraSemana());
        entidade.setTestePezinho(crianca.getTestePezinho());
        entidade.setTesteOrelhinha(crianca.getTesteOrelhinha());
        entidade.setTesteOlhinho(crianca.getTesteOlhinho());
        entidade.setTesteCoracaozinho(crianca.getTesteCoracaozinho());
        entidade.setAmamentacaoPrimeiraHora(crianca.isAmamentacaoPrimeiraHora());
        entidade.setAlimentacaoInicial(crianca.getAlimentacaoInicial());
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
                entidade.isPreNatalRealizado(),
                entidade.getConsultasPreNatal(),
                entidade.isDiabetesGestacional(),
                entidade.isHipertensaoGestacional(),
                entidade.isInfeccaoGestacional(),
                entidade.isSangramentoGestacional(),
                entidade.isUsoAlcoolGestacao(),
                entidade.isUsoTabacoGestacao(),
                entidade.isOutrasExposicoesGestacao(),
                entidade.getObservacoesGestacao(),
                entidade.getDiasAltaHospitalar(),
                entidade.isRetornoHospitalarPrimeiraSemana(),
                entidade.getTestePezinho(),
                entidade.getTesteOrelhinha(),
                entidade.getTesteOlhinho(),
                entidade.getTesteCoracaozinho(),
                entidade.isAmamentacaoPrimeiraHora(),
                entidade.getAlimentacaoInicial(),
                new br.com.pueria.pueria.criancas.dominio.ContextoClinicoCrianca(
                        entidade.getTipoGestacao(), entidade.getStatusT21(), entidade.getStatusTurner(),
                        entidade.isOutraCondicaoRelevante(), entidade.getObservacoesCondicaoRelevante()),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
