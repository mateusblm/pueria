package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.consentimentos.dominio.Consentimento;
import br.com.pueria.pueria.consentimentos.dominio.ConsentimentoRepositorio;
import br.com.pueria.pueria.consentimentos.dominio.TipoConsentimento;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.TipoUsuario;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarCriancaUseCase {

    private final CriancaRepositorio criancaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final VinculoResponsavelCriancaRepositorio vinculoRepositorio;
    private final ConsentimentoRepositorio consentimentoRepositorio;

    public CriarCriancaUseCase(
            CriancaRepositorio criancaRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            VinculoResponsavelCriancaRepositorio vinculoRepositorio,
            ConsentimentoRepositorio consentimentoRepositorio
    ) {
        this.criancaRepositorio = criancaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.vinculoRepositorio = vinculoRepositorio;
        this.consentimentoRepositorio = consentimentoRepositorio;
    }

    @Transactional
    public Crianca executar(CriarCriancaComando comando) {
        Usuario responsavel = usuarioRepositorio.buscarPorEmail(comando.emailResponsavel())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));

        if (responsavel.getTipo() != TipoUsuario.RESPONSAVEL) {
            throw new RegraDominioException("Somente usuários responsáveis podem cadastrar crianças.");
        }

        Crianca crianca = Crianca.cadastrar(
                comando.nome(),
                comando.dataNascimento(),
                comando.sexo(),
                comando.prematura(),
                comando.semanasGestacionais(),
                comando.pesoNascimentoGramas()
        );

        Crianca criancaSalva = criancaRepositorio.salvar(crianca);

        if (vinculoRepositorio.existeResponsavelPrincipal(criancaSalva.getId())) {
            throw new RegraDominioException("A criança já possui responsável principal.");
        }

        VinculoResponsavelCrianca vinculo = VinculoResponsavelCrianca.criarPrincipal(
                responsavel.getId(),
                criancaSalva.getId(),
                comando.parentesco()
        );
        vinculoRepositorio.salvar(vinculo);

        Consentimento consentimento = Consentimento.registrarAceite(
                responsavel.getId(),
                criancaSalva.getId(),
                TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL,
                comando.versaoTermoConsentimento(),
                comando.aceiteConsentimento()
        );
        consentimentoRepositorio.salvar(consentimento);

        return criancaSalva;
    }
}
