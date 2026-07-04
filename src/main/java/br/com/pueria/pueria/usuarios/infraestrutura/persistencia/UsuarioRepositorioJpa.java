package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositorioJpa implements UsuarioRepositorio {

    private final UsuarioJpaRepository repository;

    public UsuarioRepositorioJpa(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return UsuarioMapper.paraDominio(repository.save(UsuarioMapper.paraJpa(usuario)));
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id).map(UsuarioMapper::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(normalizarEmail(email)).map(UsuarioMapper::paraDominio);
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(normalizarEmail(email));
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
