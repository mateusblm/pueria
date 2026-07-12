package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;
import jakarta.validation.constraints.Size;
public record RegistrarEstimuloDesenvolvimentoRequest(@Size(max = 500) String observacao) { }
