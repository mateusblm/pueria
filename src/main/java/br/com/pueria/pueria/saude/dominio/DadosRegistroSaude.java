package br.com.pueria.pueria.saude.dominio;

import java.time.LocalDate;

public record DadosRegistroSaude(TipoRegistroSaude tipo, LocalDate dataRegistro, String descricao) { }
