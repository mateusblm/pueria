package br.com.pueria.pueria.usuarios.infraestrutura.web;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record AtualizarSenhaRequest(@NotBlank String senhaAtual, @NotBlank @Size(min = 8, max = 72) String novaSenha) { }
