package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CriptografiaSenha criptografiaSenha;

    public CadastrarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, CriptografiaSenha criptografiaSenha) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.criptografiaSenha = criptografiaSenha;
    }

    @Transactional
    public UsuarioResumo executar(CadastrarUsuarioComando comando) {
        validarSenhaPura(comando.senha());

        if (usuarioRepositorio.existePorEmail(comando.email())) {
            throw new RegraDominioException("Já existe um usuário cadastrado com este e-mail");
        }

        String senhaCriptografada = criptografiaSenha.criptografar(comando.senha());
        Usuario usuario = Usuario.cadastrarResponsavel(comando.nome(), comando.email(), senhaCriptografada);
        Usuario usuarioSalvo = usuarioRepositorio.salvar(usuario);

        return UsuarioResumo.de(usuarioSalvo);
    }

    private void validarSenhaPura(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new RegraDominioException("A senha é obrigatória");
        }
        if (senha.length() < 8) {
            throw new RegraDominioException("A senha deve possuir ao menos 8 caracteres");
        }
        if (senha.length() > 72) {
            throw new RegraDominioException("A senha deve possuir no máximo 72 caracteres");
        }
    }
}
