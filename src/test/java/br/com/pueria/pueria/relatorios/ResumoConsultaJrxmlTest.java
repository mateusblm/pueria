package br.com.pueria.pueria.relatorios;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumoConsultaJrxmlTest {

    @Test
    void compilaEGeraPdf() {
        try (var in = new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()) {
            var report = JasperCompileManager.compileReport(in);
            var print = JasperFillManager.fillReport(report, parametrosDeExemplo(), new JRBeanCollectionDataSource(secoesDeExemplo()));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            assertTrue(pdf.length > 100);
            assertTrue(print.getPages().size() >= 1);
            salvarPreviaQuandoSolicitado(pdf);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private Map<String, Object> parametrosDeExemplo() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("IDENTIFICACAO", "Helena\nNascimento: 10/03/2026 | Sexo: Feminino\nPrematuridade: 35 semanas e 2 dias");
        parametros.put("GERADO_EM", "Gerado em 12/07/2026");
        return parametros;
    }

    private List<ResumoConsultaPdfService.SecaoRelatorio> secoesDeExemplo() {
        return List.of(
                new ResumoConsultaPdfService.SecaoRelatorio("CRESCIMENTO - ÚLTIMAS MEDIDAS E REFERÊNCIA", "10/07/2026 - Peso 6,2 kg  Comp. 61 cm  PC 40,5 cm\nReferência: OMS\nPESO IDADE P48 Z -0,05 | COMPRIMENTO IDADE P52 Z 0,04\n\n15/06/2026 - Peso 5,4 kg  Comp. 57 cm  PC 38,5 cm\nReferência: OMS\nPESO IDADE P46 Z -0,10 | COMPRIMENTO IDADE P50 Z 0,00"),
                new ResumoConsultaPdfService.SecaoRelatorio("DESENVOLVIMENTO - PONTOS PARA CONVERSAR", "2 meses | Cognição\nPresta atenção a rostos - Ainda não\n\n2 meses | Cognição\nSegue objetos com o olhar por curtos períodos - Às vezes\n\n2 meses | Social emocional\nAcalma-se no colo ou com a voz de quem cuida - Às vezes\n\n2 meses | Social emocional\nSorri ao ver o rosto de alguém - Às vezes\n\n4 meses | Cognição\nObserva as próprias mãos com interesse - Às vezes"),
                new ResumoConsultaPdfService.SecaoRelatorio("NEURODESENVOLVIMENTO - OBSERVAÇÕES RECENTES", "12/07/2026 | Primeira observação registrada\nAcalma-se no colo ou com a voz de quem cuida.\n\n12/07/2026 | Observado novamente\nSorri ao ver o rosto de alguém."),
                new ResumoConsultaPdfService.SecaoRelatorio("RELATOS DA FAMÍLIA", "Preocupação da família\nTem acordado mais vezes durante a noite."),
                new ResumoConsultaPdfService.SecaoRelatorio("SAÚDE E CUIDADOS", "Sem registros de saúde informados."),
                new ResumoConsultaPdfService.SecaoRelatorio("ATIVIDADES EXPERIMENTADAS", "Conversas frente a frente\nReagiu sorrindo em alguns momentos.")
        );
    }

    private void salvarPreviaQuandoSolicitado(byte[] pdf) throws Exception {
        if (Boolean.getBoolean("pueria.gerarPreviaPdf")) {
            Path destino = Path.of("target", "resumo-consulta-preview.pdf");
            Files.createDirectories(destino.getParent());
            Files.write(destino, pdf);
        }
    }
}
