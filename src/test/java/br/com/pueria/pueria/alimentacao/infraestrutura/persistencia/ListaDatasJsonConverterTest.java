package br.com.pueria.pueria.alimentacao.infraestrutura.persistencia;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListaDatasJsonConverterTest {

    private final ListaDatasJsonConverter converter = new ListaDatasJsonConverter();

    @Test
    void devePreservarDatasAoConverterParaJsonERestaurar() {
        List<LocalDate> datas = List.of(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5));

        String json = converter.convertToDatabaseColumn(datas);

        assertEquals("[\"2026-07-01\",\"2026-07-05\"]", json);
        assertEquals(datas, converter.convertToEntityAttribute(json));
    }
}
