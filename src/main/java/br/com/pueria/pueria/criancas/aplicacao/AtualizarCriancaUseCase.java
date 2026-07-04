package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarCriancaUseCase {

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;

    public AtualizarCriancaUseCase(
            CriancaRepositorio criancaRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio
    ) {
        this.criancaRepositorio = criancaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
    }

    @Transactional
    public Crianca executar(AtualizarCriancaComando comando) {
        Usuario responsavel = buscarResponsavelAtivo(comando.emailResponsavel());
        Crianca crianca = buscarCriancaAcessivel(comando.id(), responsavel);

        Crianca atualizada = crianca.atualizar(
                comando.nome(),
                comando.dataNascimento(),
                comando.sexo(),
                comando.prematura(),
                comando.semanasGestacionais(),
                comando.pesoNascimentoGramas()
        );

        return criancaRepositorio.salvar(atualizada);
    }

    private Usuario buscarResponsavelAtivo(String email) {
        return usuarioRepositorio.buscarPorEmail(email)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));
    }

    private Crianca buscarCriancaAcessivel(java.util.UUID criancaId, Usuario responsavel) {
        boolean podeAcessar = vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), criancaId);
        if (!podeAcessar) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }

        return criancaRepositorio.buscarPorId(criancaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));
    }
}
