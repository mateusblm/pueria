package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.IdadeReferenciaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.ModalidadeRegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDate;

@Service
public class RegistrarMarcoDesenvolvimentoUseCase {

    private static final int IDADE_MAXIMA_MARCOS_MESES = 60;

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;
    private final MarcoDesenvolvimentoRepositorio marcoRepositorio;
    private final RegistroMarcoDesenvolvimentoRepositorio registroRepositorio;

    public RegistrarMarcoDesenvolvimentoUseCase(
            CriancaRepositorio criancaRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio,
            MarcoDesenvolvimentoRepositorio marcoRepositorio,
            RegistroMarcoDesenvolvimentoRepositorio registroRepositorio
    ) {
        this.criancaRepositorio = criancaRepositorio;
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

        Crianca crianca = criancaRepositorio.buscarPorId(comando.criancaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));

        MarcoDesenvolvimento marco = marcoRepositorio.buscarPorId(comando.marcoId())
                .filter(MarcoDesenvolvimento::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Marco de desenvolvimento não encontrado."));

        int idadeMeses = Math.min(IdadeReferenciaDesenvolvimento.mesesParaCheckpoints(crianca, LocalDate.now()), IDADE_MAXIMA_MARCOS_MESES);
        if (marco.getIdadeMeses() > idadeMeses) {
            throw new RegraDominioException("Este marco ainda não pertence à faixa etária atual da criança.");
        }

        RegistroMarcoDesenvolvimento registro = registroRepositorio.buscarPorCriancaEMarco(comando.criancaId(), comando.marcoId())
                .map(existente -> existente.atualizar(comando.status(), comando.observacao()))
                .orElseGet(() -> RegistroMarcoDesenvolvimento.registrar(
                        comando.criancaId(),
                        comando.marcoId(),
                        comando.status(),
                        marco.getIdadeMeses() < idadeMeses
                                ? ModalidadeRegistroMarcoDesenvolvimento.RETROSPECTIVO
                                : ModalidadeRegistroMarcoDesenvolvimento.ACOMPANHAMENTO_ATUAL,
                        comando.observacao()
                ));

        return registroRepositorio.salvar(registro);
    }

}
