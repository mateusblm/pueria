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
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.saude.aplicacao.GerenciarRegistrosSaudeUseCase;
import br.com.pueria.pueria.saude.dominio.TipoRegistroSaude;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumoConsultaPdfService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CriancaRepositorio criancas;
    private final UsuarioRepositorio usuarios;
    private final VinculoResponsavelCriancaRepositorio vinculos;
    private final ListarMarcosDesenvolvimentoUseCase marcos;
    private final GerenciarRelatosDesenvolvimentoUseCase relatos;
    private final GerenciarEstimulosDesenvolvimentoUseCase estimulos;
    private final ListarAvaliacoesCurvaCrescimentoUseCase crescimento;
    private final MedidaCrescimentoRepositorio medidas;
    private final HistoricoRespostaMarcoDesenvolvimentoRepositorio historico;
    private final MarcoDesenvolvimentoRepositorio catalogoMarcos;
    private final GerenciarRegistrosSaudeUseCase saude;

    public ResumoConsultaPdfService(
            CriancaRepositorio criancas,
            UsuarioRepositorio usuarios,
            VinculoResponsavelCriancaRepositorio vinculos,
            ListarMarcosDesenvolvimentoUseCase marcos,
            GerenciarRelatosDesenvolvimentoUseCase relatos,
            GerenciarEstimulosDesenvolvimentoUseCase estimulos,
            ListarAvaliacoesCurvaCrescimentoUseCase crescimento,
            MedidaCrescimentoRepositorio medidas,
            HistoricoRespostaMarcoDesenvolvimentoRepositorio historico,
            MarcoDesenvolvimentoRepositorio catalogoMarcos,
            GerenciarRegistrosSaudeUseCase saude
    ) {
        this.criancas = criancas;
        this.usuarios = usuarios;
        this.vinculos = vinculos;
        this.marcos = marcos;
        this.relatos = relatos;
        this.estimulos = estimulos;
        this.crescimento = crescimento;
        this.medidas = medidas;
        this.historico = historico;
        this.catalogoMarcos = catalogoMarcos;
        this.saude = saude;
    }

    public byte[] gerar(UUID criancaId, String email) {
        Crianca crianca = validar(criancaId, email);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("IDENTIFICACAO", identificacao(crianca));
        parametros.put("GERADO_EM", "Gerado em " + LocalDate.now().format(DATA));

        List<SecaoRelatorio> secoes = List.of(
                new SecaoRelatorio("CRESCIMENTO - ÚLTIMAS MEDIDAS E REFERÊNCIA", crescimento(criancaId, email)),
                new SecaoRelatorio("DESENVOLVIMENTO - PONTOS PARA CONVERSAR", desenvolvimento(criancaId, email)),
                new SecaoRelatorio("NEURODESENVOLVIMENTO - OBSERVAÇÕES RECENTES", trajetoria(criancaId)),
                new SecaoRelatorio("RELATOS DA FAMÍLIA", contexto(criancaId, email)),
                new SecaoRelatorio("SAÚDE E CUIDADOS", saude(criancaId, email)),
                new SecaoRelatorio("ATIVIDADES EXPERIMENTADAS", atividades(criancaId, email))
        );

        try (InputStream in = new ClassPathResource("relatorios/resumo-consulta.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(in);
            return JasperExportManager.exportReportToPdf(
                    JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(secoes))
            );
        } catch (Exception erro) {
            throw new IllegalStateException("Não foi possível gerar o relatório para consulta.", erro);
        }
    }

    private String identificacao(Crianca crianca) {
        String texto = crianca.getNome()
                + "\nNascimento: " + crianca.getDataNascimento().format(DATA)
                + " | Sexo: " + crianca.getSexo();
        return crianca.isPrematura()
                ? texto + "\nPrematuridade: " + crianca.getSemanasGestacionais() + " semanas e " + crianca.getDiasGestacionais() + " dias"
                : texto;
    }

    private String crescimento(UUID criancaId, String email) {
        var avaliacoes = crescimento.executar(criancaId, email).stream()
                .collect(Collectors.toMap(avaliacao -> avaliacao.medidaId(), avaliacao -> avaliacao));

        String texto = medidas.listarPorCrianca(criancaId).stream()
                .sorted(Comparator.comparing(medida -> medida.getDataMedicao(), Comparator.reverseOrder()))
                .limit(3)
                .map(medida -> {
                    String valores = (medida.getPesoKg() == null ? "" : "Peso " + medida.getPesoKg() + " kg  ")
                            + (medida.getComprimentoCm() == null ? "" : "Comp. " + medida.getComprimentoCm() + " cm  ")
                            + (medida.getPerimetroCefalicoCm() == null ? "" : "PC " + medida.getPerimetroCefalicoCm() + " cm");
                    var avaliacao = avaliacoes.get(medida.getId());
                    String curva = avaliacao == null ? "" : avaliacao.resultados().stream()
                            .map(resultado -> resultado.indicador().name().replace('_', ' ')
                                    + " P" + Math.round(resultado.percentil())
                                    + " Z " + String.format(Locale.US, "%.2f", resultado.zScore()))
                            .collect(Collectors.joining(" | "));
                    return medida.getDataMedicao().format(DATA) + " - " + valores
                            + (avaliacao == null ? "" : "\nReferência: " + avaliacao.criterioIdade() + "\n" + curva);
                })
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem medidas registradas." : texto;
    }

    private String desenvolvimento(UUID criancaId, String email) {
        String texto = marcos.executar(criancaId, email).stream()
                .filter(marco -> marco.status() == StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO
                        || marco.status() == StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA)
                .limit(8)
                .map(marco -> marco.idadeMeses() + " meses | " + marco.area().name().replace('_', ' ')
                        + "\n" + marco.descricao() + " - "
                        + (marco.status() == StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA ? "Às vezes" : "Ainda não"))
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem pontos registrados nesta seção." : texto;
    }

    private String trajetoria(UUID criancaId) {
        String texto = historico.listarPorCrianca(criancaId).stream()
                .filter(evento -> evento.statusNovo() == StatusMarcoDesenvolvimento.OBSERVADO)
                .sorted(Comparator.comparing(evento -> evento.registradoEm(), Comparator.reverseOrder()))
                .limit(6)
                .map(evento -> catalogoMarcos.buscarPorId(evento.marcoId())
                        .map(marco -> evento.registradoEm().toLocalDate().format(DATA)
                                + " | " + rotuloTrajetoria(evento.statusAnterior())
                                + "\n" + marco.getDescricao())
                        .orElse(""))
                .filter(textoEvento -> !textoEvento.isBlank())
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Ainda não há observações de marcos nesta seção." : texto;
    }

    private String contexto(UUID criancaId, String email) {
        String texto = relatos.listar(criancaId, email).stream()
                .limit(5)
                .map(relato -> rotuloRelato(relato.getTipo().name()) + "\n" + relato.getDescricao())
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem relatos familiares registrados." : texto;
    }

    private String saude(UUID criancaId, String email) {
        String texto = saude.listar(criancaId, email).stream()
                .limit(6)
                .map(registro -> registro.getDataRegistro().format(DATA) + " | "
                        + (registro.getTipo() == TipoRegistroSaude.MEDICAMENTO_SUPLEMENTO
                        ? "Suplementos e medicamentos de uso diário"
                        : "Intercorrência clínica")
                        + "\n" + registro.getDescricao())
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem registros de saúde informados." : texto;
    }

    private String atividades(UUID criancaId, String email) {
        String texto = estimulos.listarHistorico(criancaId, email).stream()
                .limit(5)
                .map(estimulo -> estimulo.titulo() + (estimulo.observacao() == null ? "" : "\n" + estimulo.observacao()))
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem atividades marcadas como experimentadas." : texto;
    }

    private String rotuloTrajetoria(StatusMarcoDesenvolvimento anterior) {
        if (anterior == null) {
            return "Primeira observação registrada";
        }
        return anterior == StatusMarcoDesenvolvimento.OBSERVADO
                ? "Observado novamente"
                : "Passou a ser observado";
    }

    private String rotuloRelato(String tipo) {
        return "PERDA_HABILIDADE".equals(tipo) ? "Perda de habilidade relatada pela família" : "Preocupação da família";
    }

    private Crianca validar(UUID criancaId, String email) {
        var usuario = usuarios.buscarPorEmail(email).orElseThrow();
        if (!vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId)) {
            throw new IllegalArgumentException("Criança não encontrada.");
        }
        return criancas.buscarPorId(criancaId).orElseThrow();
    }

    public static final class SecaoRelatorio {
        private final String titulo;
        private final String conteudo;

        public SecaoRelatorio(String titulo, String conteudo) {
            this.titulo = titulo;
            this.conteudo = conteudo;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getConteudo() {
            return conteudo;
        }
    }
}
