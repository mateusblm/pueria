package br.com.pueria.pueria.crescimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class CrescimentoAcesso {

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;

    CrescimentoAcesso(CriancaRepositorio criancaRepositorio, UsuarioRepositorio usuarioRepositorio, VinculoResponsavelCriancaRepositorio vinculoRepositorio) {
        this.criancaRepositorio = criancaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
    }

    Crianca validar(UUID criancaId, String emailResponsavel) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(emailResponsavel)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));

        if (!vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }

        return criancaRepositorio.buscarPorId(criancaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));
    }
}
