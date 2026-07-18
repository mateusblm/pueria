package br.com.pueria.pueria.comum.excecao;

import br.com.pueria.pueria.comum.seguranca.LimiteRequisicoesExcedidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class TratadorExcecoes {

    @ExceptionHandler(LimiteRequisicoesExcedidoException.class)
    public ResponseEntity<ErroApi> tratarLimiteRequisicoes(LimiteRequisicoesExcedidoException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSegundos()))
                .body(ErroApi.criar(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "Limite de requisições excedido",
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(RegraDominioException.class)
    public ResponseEntity<ErroApi> tratarRegraDominio(RegraDominioException ex) {
        HttpStatus status = conflitoDeEmail(ex) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(
                ErroApi.criar(status.value(), "Regra de domínio violada", List.of(ex.getMessage()))
        );
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroApi> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErroApi.criar(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado", List.of(ex.getMessage()))
        );
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroApi> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErroApi.criar(HttpStatus.UNAUTHORIZED.value(), "Credenciais inválidas", List.of(ex.getMessage()))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApi> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> mensagens = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                ErroApi.criar(HttpStatus.BAD_REQUEST.value(), "Dados inválidos", mensagens)
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroApi> tratarViolacaoDeIntegridade(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErroApi.criar(
                        HttpStatus.BAD_REQUEST.value(),
                        "Não foi possível salvar o registro",
                        List.of("Revise os dados informados e tente novamente.")
                )
        );
    }

    private boolean conflitoDeEmail(RegraDominioException ex) {
        if (ex.getMessage() == null) {
            return false;
        }

        String mensagem = ex.getMessage().toLowerCase(Locale.ROOT);
        return mensagem.contains("usuário cadastrado") && mensagem.contains("e-mail");
    }
}
