package br.com.pueria.pueria.comum.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorExcecoes {

    @ExceptionHandler(RegraDominioException.class)
    public ResponseEntity<ErroApi> tratarRegraDominio(RegraDominioException ex) {
        return ResponseEntity.badRequest().body(
                ErroApi.criar(HttpStatus.BAD_REQUEST.value(), "Regra de domínio violada", List.of(ex.getMessage()))
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
}
