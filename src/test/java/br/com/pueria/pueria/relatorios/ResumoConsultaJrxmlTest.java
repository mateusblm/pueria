package br.com.pueria.pueria.relatorios;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import static org.junit.jupiter.api.Assertions.assertNotNull;
class ResumoConsultaJrxmlTest { @Test void compilaTemplateDoResumo(){ try(var in=new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()){ assertNotNull(JasperCompileManager.compileReport(in)); } catch(Exception e){ throw new AssertionError(e); } } }
