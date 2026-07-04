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
        entidade.setPesoNascimentoGramas(crianca.getPesoNascimentoGramas());
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
                entidade.getPesoNascimentoGramas(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
