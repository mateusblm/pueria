package br.com.pueria.pueria.usuarios.infraestrutura.web;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record AtualizarEmailRequest(@NotBlank @Email String email, @NotBlank String senhaAtual) { }
