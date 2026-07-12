package br.com.pueria.pueria.usuarios.dominio;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class Usuario {

    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private final UUID id;
    private final String nome;
    private final String email;
    private final String senhaCriptografada;
    private final TipoUsuario tipo;
    private final boolean ativo;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private Usuario(
            UUID id,
            String nome,
            String email,
            String senhaCriptografada,
            TipoUsuario tipo,
            boolean ativo,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        this.id = validarId(id);
        this.nome = validarNome(nome);
        this.email = normalizarEmail(email);
        this.senhaCriptografada = validarSenhaCriptografada(senhaCriptografada);
        this.tipo = validarTipo(tipo);
        this.ativo = ativo;
        this.criadoEm = validarCriadoEm(criadoEm);
        this.atualizadoEm = atualizadoEm;
    }

    public static Usuario cadastrarResponsavel(String nome, String email, String senhaCriptografada) {
        return new Usuario(
                UUID.randomUUID(),
                nome,
                email,
                senhaCriptografada,
                TipoUsuario.RESPONSAVEL,
                true,
                LocalDateTime.now(),
                null
        );
    }

    public static Usuario reconstruir(
            UUID id,
            String nome,
            String email,
            String senhaCriptografada,
            TipoUsuario tipo,
            boolean ativo,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm
    ) {
        return new Usuario(id, nome, email, senhaCriptografada, tipo, ativo, criadoEm, atualizadoEm);
    }

    public Usuario comSenhaCriptografada(String novaSenhaCriptografada) {
        return new Usuario(id, nome, email, novaSenhaCriptografada, tipo, ativo, criadoEm, LocalDateTime.now());
    }

    private static UUID validarId(UUID id) {
        if (id == null) {
            throw new RegraDominioException("O usuário deve possuir identificador");
        }
        return id;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDominioException("O nome do usuário é obrigatório");
        }
        String nomeNormalizado = nome.trim();
        if (nomeNormalizado.length() < 2) {
            throw new RegraDominioException("O nome do usuário deve possuir ao menos 2 caracteres");
        }
        if (nomeNormalizado.length() > 150) {
            throw new RegraDominioException("O nome do usuário deve possuir no máximo 150 caracteres");
        }
        return nomeNormalizado;
    }

    private static String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraDominioException("O e-mail do usuário é obrigatório");
        }
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        if (emailNormalizado.length() > 150) {
            throw new RegraDominioException("O e-mail do usuário deve possuir no máximo 150 caracteres");
        }
        if (!EMAIL_VALIDO.matcher(emailNormalizado).matches()) {
            throw new RegraDominioException("O e-mail do usuário é inválido");
        }
        return emailNormalizado;
    }

    private static String validarSenhaCriptografada(String senhaCriptografada) {
        if (senhaCriptografada == null || senhaCriptografada.isBlank()) {
            throw new RegraDominioException("A senha criptografada do usuário é obrigatória");
        }
        return senhaCriptografada;
    }

    private static TipoUsuario validarTipo(TipoUsuario tipo) {
        if (tipo == null) {
            throw new RegraDominioException("O tipo do usuário é obrigatório");
        }
        return tipo;
    }

    private static LocalDateTime validarCriadoEm(LocalDateTime criadoEm) {
        if (criadoEm == null) {
            throw new RegraDominioException("A data de criação do usuário é obrigatória");
        }
        return criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaCriptografada() {
        return senhaCriptografada;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
