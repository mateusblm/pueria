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

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumoConsultaJrxmlTest {

    @Test
    void compilaEGeraPdf() {
        try (var in = new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()) {
            var report = JasperCompileManager.compileReport(in);
            var print = JasperFillManager.fillReport(report, parametrosDeExemplo(), new JREmptyDataSource());
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            assertTrue(pdf.length > 100);
            assertTrue(print.getPages().size() >= 1);
            salvarPreviaQuandoSolicitado(pdf);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void compilaEGeraResumoBreve() {
        try (var in = new ClassPathResource("relatorios/resumo-consulta-breve.jrxml").getInputStream()) {
            var report = JasperCompileManager.compileReport(in);
            var print = JasperFillManager.fillReport(report, parametrosDeExemplo(), new JREmptyDataSource());
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            assertTrue(pdf.length > 100);
            assertTrue(print.getPages().size() >= 1);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private Map<String, Object> parametrosDeExemplo() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("NOME_CRIANCA", "Helena Martins");
        parametros.put("IDENTIFICACAO", "Nascimento 10/03/2026 - Sexo feminino - Prematuridade: 35 semanas e 2 dias");
        parametros.put("GERADO_EM", "Gerado em 12/07/2026");
        parametros.put("CRESCIMENTO_DATA", "Aferido em 10/07/2026");
        parametros.put("PESO", "6,2 kg");
        parametros.put("COMPRIMENTO", "61 cm");
        parametros.put("PERIMETRO", "40,5 cm");
        parametros.put("CRESCIMENTO_TABELA", "Peso por idade - P48 - escore-z -0,05 - faixa esperada\nComprimento por idade - P52 - escore-z 0,04 - faixa esperada\nPerimetro cefalico por idade - P50 - escore-z 0,00 - faixa esperada");
        parametros.put("ALIMENTACAO", "10/07/2026\nLeite materno - 5 refeicoes/dia - frutas");
        parametros.put("SONO", "10/07/2026\ndorme 20:00 e acorda 07:00 - 2 cochilos");
        parametros.put("TRANSITO", "10/07/2026\ntipo 4 - 1-2 evacuacoes/dia");
        parametros.put("TELAS", "10/07/2026\n30 min em dias de semana - videos curtos");
        parametros.put("PONTOS_DESENVOLVIMENTO", "2 meses - Cognitivo\nPresta atencao a rostos - Ainda nao\n\n2 meses - Social emocional\nSorri ao ver o rosto de alguem - As vezes");
        parametros.put("OBSERVACOES", "12/07/2026 - Passou a ser observado\nAcalma-se no colo ou com a voz de quem cuida.\n\n10/07/2026 - Passou a ser observado\nFaz sons alem do choro.");
        parametros.put("RELATOS_FAMILIA", "Preocupacao da familia\nTem acordado mais vezes durante a noite.");
        parametros.put("SAUDE_CUIDADOS", "12/07/2026 - Intercorrencia clinica\nTeve vomitos por apenas um dia e nao fez uso de medicamentos.");
        parametros.put("ATIVIDADES", "- Conversas frente a frente\n- Passeio com palavras\n- Sequencia de faz de conta");
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
