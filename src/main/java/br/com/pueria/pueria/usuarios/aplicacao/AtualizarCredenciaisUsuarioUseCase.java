package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;

@Service
public class AtualizarCredenciaisUsuarioUseCase {
    private final UsuarioRepositorio usuarios;
    private final CriptografiaSenha criptografia;

    public AtualizarCredenciaisUsuarioUseCase(UsuarioRepositorio usuarios, CriptografiaSenha criptografia) { this.usuarios = usuarios; this.criptografia = criptografia; }

    public void email(String emailAtual, String novoEmail, String senhaAtual) {
        Usuario usuario = autenticar(emailAtual, senhaAtual);
        if (!usuario.getEmail().equalsIgnoreCase(novoEmail) && usuarios.existePorEmail(novoEmail)) throw new RegraDominioException("Já existe um usuário cadastrado com este e-mail");
        usuarios.salvar(usuario.comEmail(novoEmail));
    }

    public void senha(String emailAtual, String senhaAtual, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 8 || novaSenha.length() > 72) throw new RegraDominioException("A nova senha deve possuir entre 8 e 72 caracteres");
        Usuario usuario = autenticar(emailAtual, senhaAtual);
        usuarios.salvar(usuario.comSenhaCriptografada(criptografia.criptografar(novaSenha)));
    }

    private Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarios.buscarPorEmail(email).orElseThrow(() -> new RegraDominioException("Não foi possível atualizar suas credenciais"));
        if (!criptografia.corresponde(senha, usuario.getSenhaCriptografada())) throw new RegraDominioException("A senha atual não confere");
        return usuario;
    }
}
