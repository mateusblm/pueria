package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListarCriancasUseCase {

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;

    public ListarCriancasUseCase(
            CriancaRepositorio criancaRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio
    ) {
        this.criancaRepositorio = criancaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Crianca> executar(String emailResponsavel) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(emailResponsavel)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));

        List<UUID> ids = vinculoRepositorio.listarCriancaIdsPorUsuario(responsavel.getId());
        if (ids.isEmpty()) {
            return List.of();
        }

        return criancaRepositorio.listarPorIds(ids);
    }
}
