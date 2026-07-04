package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import br.com.pueria.pueria.usuarios.dominio.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioJpaEntidade paraJpa(Usuario usuario) {
        return new UsuarioJpaEntidade(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenhaCriptografada(),
                usuario.getTipo(),
                usuario.isAtivo(),
                usuario.getCriadoEm(),
                usuario.getAtualizadoEm()
        );
    }

    public static Usuario paraDominio(UsuarioJpaEntidade entidade) {
        return Usuario.reconstruir(
                entidade.getId(),
                entidade.getNome(),
                entidade.getEmail(),
                entidade.getSenha(),
                entidade.getTipo(),
                entidade.isAtivo(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
