package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.util.List;

@Converter
public class ListaDatasJsonConverter implements AttributeConverter<List<LocalDate>, String> {

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();
    private static final TypeReference<List<LocalDate>> TIPO_LISTA_DATAS = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<LocalDate> datas) {
        try {
            return MAPPER.writeValueAsString(datas == null ? List.of() : datas);
        } catch (Exception excecao) {
            throw new IllegalArgumentException("Nao foi possivel persistir as datas de reexposicao.", excecao);
        }
    }

    @Override
    public List<LocalDate> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return MAPPER.readValue(json, TIPO_LISTA_DATAS);
        } catch (Exception excecao) {
            throw new IllegalArgumentException("Nao foi possivel ler as datas de reexposicao.", excecao);
        }
    }
}
