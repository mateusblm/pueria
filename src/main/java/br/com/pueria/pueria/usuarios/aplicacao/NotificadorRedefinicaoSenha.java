package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.usuarios.dominio.Usuario;

public interface NotificadorRedefinicaoSenha {
    void enviar(Usuario usuario, String token);
}
