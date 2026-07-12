package br.com.pueria.pueria.relatorios;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumoConsultaJrxmlTest {

    @Test
    void compilaEGeraPdf() {
        try (var in = new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()) {
            var report = JasperCompileManager.compileReport(in);
            var print = JasperFillManager.fillReport(report, parametrosDeExemplo(), new JREmptyDataSource(1));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            assertTrue(pdf.length > 100);
            assertEquals(1, print.getPages().size());
            salvarPreviaQuandoSolicitado(pdf);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private Map<String, Object> parametrosDeExemplo() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("IDENTIFICACAO", "Helena\nNascimento: 10/03/2026 | Sexo: Feminino\nPrematuridade: 35 semanas e 2 dias");
        parametros.put("CRESCIMENTO", "10/07/2026 - Peso 6,2 kg  Comp. 61 cm  PC 40,5 cm\nReferência: OMS\nPESO IDADE P48 Z -0,05 | COMPRIMENTO IDADE P52 Z 0,04\n\n15/06/2026 - Peso 5,4 kg  Comp. 57 cm  PC 38,5 cm\nReferência: OMS\nPESO IDADE P46 Z -0,10 | COMPRIMENTO IDADE P50 Z 0,00");
        parametros.put("DESENVOLVIMENTO", "2 meses | Cognição\nPresta atenção a rostos - Ainda não\n\n2 meses | Cognição\nSegue objetos com o olhar por curtos períodos - Às vezes\n\n2 meses | Social emocional\nAcalma-se no colo ou com a voz de quem cuida - Às vezes\n\n2 meses | Social emocional\nSorri ao ver o rosto de alguém - Às vezes\n\n4 meses | Cognição\nObserva as próprias mãos com interesse - Às vezes");
        parametros.put("TRAJETORIA", "12/07/2026 | Acalma-se no colo ou com a voz de quem cuida\nPrimeira resposta -> OBSERVADO\n\n12/07/2026 | Sorri ao ver o rosto de alguém\nPrimeira resposta -> NÃO TENHO CERTEZA\n\n12/07/2026 | Faz sons além do choro\nPrimeira resposta -> OBSERVADO\n\n12/07/2026 | Reage a sons altos\nPrimeira resposta -> OBSERVADO");
        parametros.put("CONTEXTO", "Relatos da família\nPreocupação da família\nTem acordado mais vezes durante a noite.\n\nAtividades experimentadas\nConversas frente a frente\nReagiu sorrindo em alguns momentos.");
        parametros.put("ATIVIDADES", "Conversas frente a frente");
        parametros.put("GERADO_EM", "Gerado em 12/07/2026");
        return parametros;
    }

    private void salvarPreviaQuandoSolicitado(byte[] pdf) throws Exception {
        if (Boolean.getBoolean("pueria.gerarPreviaPdf")) {
            Path destino = Path.of("target", "resumo-consulta-preview.pdf");
            Files.createDirectories(destino.getParent());
            Files.write(destino, pdf);
        }
    }
}
