package br.com.pueria.pueria.relatorios;
import net.sf.jasperreports.engine.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
class ResumoConsultaJrxmlTest { @Test void compilaEGeraPdf(){ try(var in=new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()){ var report=JasperCompileManager.compileReport(in); var parametros=new HashMap<String,Object>(); parametros.put("CONTEUDO","Resumo para consulta"); var print=JasperFillManager.fillReport(report,parametros,new JREmptyDataSource(1)); assertTrue(JasperExportManager.exportReportToPdf(print).length>100); } catch(Exception e){ throw new AssertionError(e); } } }
