package br.com.pueria.pueria.usuarios.infraestrutura.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 150, message = "nome deve possuir no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        @Size(max = 150, message = "email deve possuir no máximo 150 caracteres")
        String email,

        @NotBlank(message = "senha é obrigatória")
        @Size(min = 8, max = 72, message = "senha deve possuir entre 8 e 72 caracteres")
        String senha
) {
}
