package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarMarcoDesenvolvimentoUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;
    private final MarcoDesenvolvimentoRepositorio marcoRepositorio;
    private final RegistroMarcoDesenvolvimentoRepositorio registroRepositorio;

    public RegistrarMarcoDesenvolvimentoUseCase(
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio,
            MarcoDesenvolvimentoRepositorio marcoRepositorio,
            RegistroMarcoDesenvolvimentoRepositorio registroRepositorio
    ) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
        this.marcoRepositorio = marcoRepositorio;
        this.registroRepositorio = registroRepositorio;
    }

    @Transactional
    public RegistroMarcoDesenvolvimento executar(RegistrarMarcoDesenvolvimentoComando comando) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(comando.emailResponsavel())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));

        if (!vinculoRepositorio.usuarioPodeAcessarCrianca(responsavel.getId(), comando.criancaId())) {
            throw new RecursoNaoEncontradoException("Criança não encontrada.");
        }

        marcoRepositorio.buscarPorId(comando.marcoId())
                .filter(Marco -> Marco.isAtivo())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Marco de desenvolvimento não encontrado."));

        RegistroMarcoDesenvolvimento registro = registroRepositorio.buscarPorCriancaEMarco(comando.criancaId(), comando.marcoId())
                .map(existente -> existente.atualizar(comando.status(), comando.observacao()))
                .orElseGet(() -> RegistroMarcoDesenvolvimento.registrar(
                        comando.criancaId(),
                        comando.marcoId(),
                        comando.status(),
                        comando.observacao()
                ));

        return registroRepositorio.salvar(registro);
    }
}
