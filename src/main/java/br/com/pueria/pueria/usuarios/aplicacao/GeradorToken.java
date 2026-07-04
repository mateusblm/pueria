package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.usuarios.dominio.Usuario;

public interface GeradorToken {

    String gerar(Usuario usuario);

    long expiracaoEmSegundos();
}
