package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class TransitoIntestinalAcesso {

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;

    TransitoIntestinalAcesso(CriancaRepositorio criancaRepositorio, UsuarioRepositorio usuarioRepositorio, VinculoResponsavelCriancaRepositorio vinculoRepositorio) {
        this.criancaRepositorio = criancaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
    }

    Crianca validar(UUID criancaId, String emailResponsavel) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(emailResponsavel)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel autenticado nao encontrado."));

        if (!vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId)) {
            throw new RecursoNaoEncontradoException("Crianca nao encontrada.");
        }

        return criancaRepositorio.buscarPorId(criancaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Crianca nao encontrada."));
    }
}
