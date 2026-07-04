package br.com.pueria.pueria.usuarios.infraestrutura.seguranca;

import br.com.pueria.pueria.usuarios.aplicacao.CriptografiaSenha;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptCriptografiaSenha implements CriptografiaSenha {

    private final PasswordEncoder passwordEncoder;

    public BCryptCriptografiaSenha(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String criptografar(String senhaPura) {
        return passwordEncoder.encode(senhaPura);
    }

    @Override
    public boolean corresponde(String senhaPura, String senhaCriptografada) {
        return passwordEncoder.matches(senhaPura, senhaCriptografada);
    }
}
