package br.com.pueria.pueria.usuarios.infraestrutura.email;

import br.com.pueria.pueria.usuarios.aplicacao.NotificadorRedefinicaoSenha;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailRedefinicaoSenhaService implements NotificadorRedefinicaoSenha {
    private static final Logger LOG = LoggerFactory.getLogger(EmailRedefinicaoSenhaService.class);
    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean habilitado;
    private final String remetente;
    private final String frontendUrl;

    public EmailRedefinicaoSenhaService(ObjectProvider<JavaMailSender> mailSender,
            @Value("${pueria.email.habilitado:false}") boolean habilitado,
            @Value("${pueria.email.remetente:nao-responda@pueria.com.br}") String remetente,
            @Value("${pueria.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.mailSender = mailSender; this.habilitado = habilitado; this.remetente = remetente; this.frontendUrl = frontendUrl.replaceAll("/$", "");
    }

    @Override
    public void enviar(Usuario usuario, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;
        if (!habilitado) {
            LOG.info("Redefinição de senha solicitada para {}. Link de desenvolvimento: {}", usuario.getEmail(), link);
            return;
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) throw new IllegalStateException("O envio de e-mail está habilitado, mas o SMTP não foi configurado.");
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente); mensagem.setTo(usuario.getEmail()); mensagem.setSubject("Redefina sua senha no Pueria");
        mensagem.setText("Olá, " + usuario.getNome() + ",\n\nRecebemos um pedido para redefinir sua senha. Use o link abaixo em até 15 minutos:\n" + link + "\n\nSe não foi você, ignore este e-mail.");
        sender.send(mensagem);
    }
}
