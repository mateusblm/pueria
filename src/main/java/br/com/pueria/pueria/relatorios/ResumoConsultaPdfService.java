package br.com.pueria.pueria.relatorios;

import br.com.pueria.pueria.crescimento.aplicacao.ListarAvaliacoesCurvaCrescimentoUseCase;
import br.com.pueria.pueria.crescimento.dominio.MedidaCrescimentoRepositorio;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.desenvolvimento.aplicacao.GerenciarEstimulosDesenvolvimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.aplicacao.GerenciarRelatosDesenvolvimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.aplicacao.ListarMarcosDesenvolvimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.dominio.HistoricoRespostaMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.saude.aplicacao.GerenciarRegistrosSaudeUseCase;
import br.com.pueria.pueria.saude.dominio.TipoRegistroSaude;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumoConsultaPdfService {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final CriancaRepositorio criancas; private final UsuarioRepositorio usuarios; private final VinculoResponsavelCriancaRepositorio vinculos;
    private final ListarMarcosDesenvolvimentoUseCase marcos; private final GerenciarRelatosDesenvolvimentoUseCase relatos;
    private final GerenciarEstimulosDesenvolvimentoUseCase estimulos; private final ListarAvaliacoesCurvaCrescimentoUseCase crescimento;
    private final MedidaCrescimentoRepositorio medidas; private final HistoricoRespostaMarcoDesenvolvimentoRepositorio historico; private final MarcoDesenvolvimentoRepositorio catalogoMarcos;
    private final GerenciarRegistrosSaudeUseCase saude;

    public ResumoConsultaPdfService(CriancaRepositorio criancas, UsuarioRepositorio usuarios, VinculoResponsavelCriancaRepositorio vinculos,
            ListarMarcosDesenvolvimentoUseCase marcos, GerenciarRelatosDesenvolvimentoUseCase relatos, GerenciarEstimulosDesenvolvimentoUseCase estimulos,
            ListarAvaliacoesCurvaCrescimentoUseCase crescimento, MedidaCrescimentoRepositorio medidas,
            HistoricoRespostaMarcoDesenvolvimentoRepositorio historico, MarcoDesenvolvimentoRepositorio catalogoMarcos, GerenciarRegistrosSaudeUseCase saude) {
        this.criancas=criancas; this.usuarios=usuarios; this.vinculos=vinculos; this.marcos=marcos; this.relatos=relatos; this.estimulos=estimulos;
        this.crescimento=crescimento; this.medidas=medidas; this.historico=historico; this.catalogoMarcos=catalogoMarcos; this.saude=saude;
    }

    public byte[] gerar(UUID criancaId, String email) {
        Crianca crianca = validar(criancaId, email);
        Map<String,Object> p = new HashMap<>();
        p.put("IDENTIFICACAO", identificacao(crianca)); p.put("CRESCIMENTO", crescimento(criancaId, email));
        p.put("DESENVOLVIMENTO", desenvolvimento(criancaId, email)); p.put("TRAJETORIA", trajetoria(criancaId));
        p.put("CONTEXTO", "Histórico de saúde relatado pela família\n" + saude(criancaId, email) + "\n\nRelatos da família\n" + contexto(criancaId, email) + "\n\nAtividades experimentadas\n" + atividades(criancaId, email)); p.put("ATIVIDADES", atividades(criancaId, email));
        p.put("GERADO_EM", "Gerado em " + LocalDate.now().format(DATA));
        try (InputStream in = new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(in);
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(report, p, new JREmptyDataSource(1)));
        } catch (Exception e) { throw new IllegalStateException("Não foi possível gerar o relatório para consulta.", e); }
    }

    private String identificacao(Crianca c) {
        String texto = c.getNome() + "\nNascimento: " + c.getDataNascimento().format(DATA) + " | Sexo: " + c.getSexo();
        return c.isPrematura() ? texto + "\nPrematuridade: " + c.getSemanasGestacionais() + " semanas e " + c.getDiasGestacionais() + " dias" : texto;
    }
    private String crescimento(UUID id, String email) {
        var avaliacoes = crescimento.executar(id,email).stream().collect(Collectors.toMap(a -> a.medidaId(), a -> a));
        String texto = medidas.listarPorCrianca(id).stream().sorted(Comparator.comparing(m -> m.getDataMedicao(), Comparator.reverseOrder())).limit(3).map(m -> {
            String valores = (m.getPesoKg()==null?"":"Peso " + m.getPesoKg()+" kg  ") + (m.getComprimentoCm()==null?"":"Comp. " + m.getComprimentoCm()+" cm  ") + (m.getPerimetroCefalicoCm()==null?"":"PC " + m.getPerimetroCefalicoCm()+" cm");
            var a = avaliacoes.get(m.getId()); String curva = a == null ? "" : a.resultados().stream().map(r -> r.indicador().name().replace('_',' ') + " P" + Math.round(r.percentil()) + " Z " + String.format(Locale.US,"%.2f",r.zScore())).collect(Collectors.joining(" | "));
            return m.getDataMedicao().format(DATA) + " - " + valores + "\n" + (a==null?"":"Referência: " + a.criterioIdade() + "\n" + curva);
        }).collect(Collectors.joining("\n\n"));
        return texto.isBlank()?"Sem medidas registradas.":texto;
    }
    private String desenvolvimento(UUID id, String email) {
        String texto = marcos.executar(id,email).stream().filter(m -> m.status().name().equals("AINDA_NAO_OBSERVADO") || m.status().name().equals("NAO_TENHO_CERTEZA")).limit(8)
                .map(m -> m.idadeMeses()+" meses | "+m.area().name().replace('_',' ')+"\n"+m.descricao()+" - "+(m.status().name().equals("NAO_TENHO_CERTEZA")?"Às vezes":"Ainda não")).collect(Collectors.joining("\n\n"));
        return texto.isBlank()?"Sem pontos registrados nesta seção.":texto;
    }
    private String trajetoria(UUID id) { String texto = historico.listarPorCrianca(id).stream().limit(6).map(h -> catalogoMarcos.buscarPorId(h.marcoId()).map(m -> h.registradoEm().toLocalDate().format(DATA)+" | "+m.getDescricao()+"\n"+(h.statusAnterior()==null?"Primeira resposta":h.statusAnterior().name().replace('_',' '))+" -> "+h.statusNovo().name().replace('_',' ')).orElse("")).filter(s -> !s.isBlank()).collect(Collectors.joining("\n\n")); return texto.isBlank()?"Ainda não há mudanças de resposta registradas.":texto; }
    private String contexto(UUID id,String email) { String texto=relatos.listar(id,email).stream().limit(5).map(r -> r.getTipo().name().replace('_',' ')+"\n"+r.getDescricao()).collect(Collectors.joining("\n\n")); return texto.isBlank()?"Sem relatos familiares registrados.":texto; }
    private String saude(UUID id,String email) { String texto=saude.listar(id,email).stream().limit(6).map(r -> r.getDataRegistro().format(DATA)+" | "+(r.getTipo()== TipoRegistroSaude.MEDICAMENTO_SUPLEMENTO ? "Suplementos e medicamentos de uso diário" : "Intercorrência clínica")+"\n"+r.getDescricao()).collect(Collectors.joining("\n\n")); return texto.isBlank()?"Sem registros de saúde informados.":texto; }
    private String atividades(UUID id,String email) { String texto=estimulos.listarHistorico(id,email).stream().limit(5).map(e -> e.titulo()+(e.observacao()==null?"":"\n"+e.observacao())).collect(Collectors.joining("\n\n")); return texto.isBlank()?"Sem atividades marcadas como experimentadas.":texto; }
    private Crianca validar(UUID id,String email){var u=usuarios.buscarPorEmail(email).orElseThrow();if(!vinculos.usuarioPodeAcessarCrianca(u.getId(),id))throw new IllegalArgumentException("Criança não encontrada.");return criancas.buscarPorId(id).orElseThrow();}
}
