package br.com.pueria.pueria.responsaveis.infraestrutura.web;

import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConvidarCuidadorRequest(@NotBlank @Email String email, Parentesco parentesco) { }
