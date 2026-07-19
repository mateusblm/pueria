package br.com.pueria.pueria.relatorios;

import br.com.pueria.pueria.alimentacao.aplicacao.ListarAlimentacaoUseCase;
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
import br.com.pueria.pueria.sono.aplicacao.ListarSonoUseCase;
import br.com.pueria.pueria.telas.aplicacao.ListarTelasUseCase;
import br.com.pueria.pueria.transitointestinal.aplicacao.ListarTransitoIntestinalUseCase;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
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
    private final ListarAlimentacaoUseCase alimentacao;
    private final ListarSonoUseCase sono;
    private final ListarTelasUseCase telas;
    private final ListarTransitoIntestinalUseCase transitoIntestinal;

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
            GerenciarRegistrosSaudeUseCase saude,
            ListarAlimentacaoUseCase alimentacao,
            ListarSonoUseCase sono,
            ListarTelasUseCase telas,
            ListarTransitoIntestinalUseCase transitoIntestinal
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
        this.alimentacao = alimentacao;
        this.sono = sono;
        this.telas = telas;
        this.transitoIntestinal = transitoIntestinal;
    }

    public byte[] gerar(UUID criancaId, String email, boolean detalhado) {
        Crianca crianca = validar(criancaId, email);
        Map<String, Object> parametros = parametros(crianca, criancaId, email);

        String modelo = detalhado ? "relatorios/resumo-consulta.jrxml" : "relatorios/resumo-consulta-breve.jrxml";
        try (InputStream in = new ClassPathResource(modelo).getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(in);
            return JasperExportManager.exportReportToPdf(
                    JasperFillManager.fillReport(report, parametros, new JREmptyDataSource())
            );
        } catch (Exception erro) {
            throw new IllegalStateException("Não foi possível gerar o relatório para consulta.", erro);
        }
    }

    private Map<String, Object> parametros(Crianca crianca, UUID criancaId, String email) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("NOME_CRIANCA", crianca.getNome());
        parametros.put("IDENTIFICACAO", identificacao(crianca));
        parametros.put("GERADO_EM", "Gerado em " + LocalDate.now().format(DATA));

        preencherCrescimento(parametros, criancaId, email);
        parametros.put("ALIMENTACAO", resumoAlimentacao(criancaId, email));
        parametros.put("SONO", resumoSono(criancaId, email));
        parametros.put("TRANSITO", resumoTransito(criancaId, email));
        parametros.put("TELAS", resumoTelas(criancaId, email));
        parametros.put("PONTOS_DESENVOLVIMENTO", desenvolvimento(criancaId, email));
        parametros.put("OBSERVACOES", trajetoria(criancaId));
        parametros.put("RELATOS_FAMILIA", contexto(criancaId, email));
        parametros.put("SAUDE_CUIDADOS", registrosSaude(criancaId, email));
        parametros.put("ATIVIDADES", atividades(criancaId, email));
        return parametros;
    }

    private String identificacao(Crianca crianca) {
        String texto = "Nascimento " + crianca.getDataNascimento().format(DATA)
                + " · Sexo " + rotuloSexo(crianca.getSexo().name());
        return crianca.isPrematura()
                ? texto + " · Prematuridade: " + crianca.getSemanasGestacionais() + " semanas e "
                + crianca.getDiasGestacionais() + " dias"
                : texto;
    }

    private void preencherCrescimento(Map<String, Object> parametros, UUID criancaId, String email) {
        var avaliacoes = crescimento.executar(criancaId, email).stream()
                .collect(Collectors.toMap(avaliacao -> avaliacao.medidaId(), avaliacao -> avaliacao));
        var ultima = medidas.listarPorCrianca(criancaId).stream()
                .max(Comparator.comparing(medida -> medida.getDataMedicao()));

        if (ultima.isEmpty()) {
            parametros.put("CRESCIMENTO_DATA", "Ainda sem medidas registradas");
            parametros.put("PESO", "Não informado");
            parametros.put("COMPRIMENTO", "Não informado");
            parametros.put("PERIMETRO", "Não informado");
            parametros.put("CRESCIMENTO_TABELA", "Quando houver uma medida registrada, os índices de referência aparecerão aqui.");
            return;
        }

        var medida = ultima.get();
        parametros.put("CRESCIMENTO_DATA", "Aferido em " + medida.getDataMedicao().format(DATA));
        parametros.put("PESO", formatarMedida(medida.getPesoKg(), "kg"));
        parametros.put("COMPRIMENTO", formatarMedida(medida.getComprimentoCm(), "cm"));
        parametros.put("PERIMETRO", formatarMedida(medida.getPerimetroCefalicoCm(), "cm"));

        var avaliacao = avaliacoes.get(medida.getId());
        String tabela = avaliacao == null ? "A referência para esta medida ainda não está disponível." : avaliacao.resultados().stream()
                .map(resultado -> rotuloIndicador(resultado.indicador().name())
                        + " · P" + Math.round(resultado.percentil())
                        + " · escore-z " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", resultado.zScore())
                        + " · " + rotuloClassificacao(resultado.classificacao().name()))
                .collect(Collectors.joining("\n"));
        parametros.put("CRESCIMENTO_TABELA", tabela);
    }

    private String resumoAlimentacao(UUID criancaId, String email) {
        return alimentacao.executar(criancaId, email).stream().findFirst()
                .map(detalhado -> {
                    var registro = detalhado.registro();
                    List<String> itens = new java.util.ArrayList<>();
                    if (registro.getTipoLeite() != null) itens.add(rotuloEnum(registro.getTipoLeite().name()));
                    if (registro.getRefeicoesPorDia() != null) itens.add(registro.getRefeicoesPorDia() + " refeições/dia");
                    if (Boolean.TRUE.equals(registro.getConsomeFrutas())) itens.add("frutas");
                    if (Boolean.TRUE.equals(registro.getConsomeLegumesVerduras())) itens.add("legumes e verduras");
                    return resumoComData(registro.getDataRegistro(), itens, "Registro alimentar salvo.");
                })
                .orElse("Ainda sem registro alimentar.");
    }

    private String resumoSono(UUID criancaId, String email) {
        return sono.executar(criancaId, email).stream().findFirst()
                .map(detalhado -> {
                    var registro = detalhado.registro();
                    List<String> itens = new java.util.ArrayList<>();
                    if (registro.getHorarioDormiu() != null && registro.getHorarioAcordou() != null) {
                        itens.add("dorme " + registro.getHorarioDormiu() + " e acorda " + registro.getHorarioAcordou());
                    }
                    if (registro.getQuantidadeCochilos() != null) itens.add(registro.getQuantidadeCochilos() + " cochilos");
                    if (registro.getDespertaresNoturnos() != null) itens.add(registro.getDespertaresNoturnos() + " despertares noturnos");
                    return resumoComData(registro.getDataRegistro(), itens, "Registro de sono salvo.");
                })
                .orElse("Ainda sem registro de sono.");
    }

    private String resumoTransito(UUID criancaId, String email) {
        return transitoIntestinal.executar(criancaId, email).stream().findFirst()
                .map(detalhado -> {
                    var registro = detalhado.registro();
                    List<String> itens = new java.util.ArrayList<>();
                    if (registro.getTipoFezes() != null) itens.add(registro.getTipoFezes().descricaoParaResumo());
                    if (registro.getEvacuacoesPorDia() != null) itens.add(registro.getEvacuacoesPorDia() + " evacuações/dia");
                    return resumoComData(registro.getDataRegistro(), itens, "Registro intestinal salvo.");
                })
                .orElse("Ainda sem registro intestinal.");
    }

    private String resumoTelas(UUID criancaId, String email) {
        return telas.executar(criancaId, email).stream().findFirst()
                .map(detalhado -> {
                    var registro = detalhado.registro();
                    List<String> itens = new java.util.ArrayList<>();
                    if (registro.getMinutosDiaSemana() != null) itens.add(formatarMinutos(registro.getMinutosDiaSemana()) + " em dias de semana");
                    if (registro.getTipoConteudoPredominante() != null) itens.add(rotuloEnum(registro.getTipoConteudoPredominante().name()));
                    return resumoComData(registro.getDataRegistro(), itens, "Registro de telas salvo.");
                })
                .orElse("Ainda sem registro de telas.");
    }

    private String desenvolvimento(UUID criancaId, String email) {
        String texto = marcos.executar(criancaId, email).stream()
                .filter(marco -> marco.status() == StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO
                        || marco.status() == StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA)
                .limit(8)
                .map(marco -> marco.idadeMeses() + " meses · " + rotuloEnum(marco.area().name())
                        + "\n" + marco.descricao() + " · "
                        + (marco.status() == StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA ? "Às vezes" : "Ainda não"))
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Nenhum ponto de desenvolvimento em observação nesta avaliação." : texto;
    }

    private String trajetoria(UUID criancaId) {
        String texto = historico.listarPorCrianca(criancaId).stream()
                .filter(evento -> evento.statusNovo() == StatusMarcoDesenvolvimento.OBSERVADO)
                .sorted(Comparator.comparing(evento -> evento.registradoEm(), Comparator.reverseOrder()))
                .limit(6)
                .map(evento -> catalogoMarcos.buscarPorId(evento.marcoId())
                        .map(marco -> evento.registradoEm().toLocalDate().format(DATA)
                                + " · " + rotuloTrajetoria(evento.statusAnterior()) + "\n" + marco.getDescricao())
                        .orElse(""))
                .filter(textoEvento -> !textoEvento.isBlank())
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Ainda não há observações recentes de marcos registradas." : texto;
    }

    private String contexto(UUID criancaId, String email) {
        String texto = relatos.listar(criancaId, email).stream()
                .limit(4)
                .map(relato -> rotuloRelato(relato.getTipo().name()) + "\n" + relato.getDescricao())
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem relatos familiares no período." : texto;
    }

    private String registrosSaude(UUID criancaId, String email) {
        String texto = saude.listar(criancaId, email).stream()
                .limit(5)
                .map(registro -> registro.getDataRegistro().format(DATA) + " · "
                        + (registro.getTipo() == TipoRegistroSaude.MEDICAMENTO_SUPLEMENTO
                        ? "Uso diário" : "Intercorrência clínica") + "\n" + registro.getDescricao())
                .collect(Collectors.joining("\n\n"));
        return texto.isBlank() ? "Sem registros de saúde e cuidados no período." : texto;
    }

    private String atividades(UUID criancaId, String email) {
        String texto = estimulos.listarHistorico(criancaId, email).stream()
                .limit(6)
                .map(estimulo -> "• " + estimulo.titulo()
                        + (estimulo.observacao() == null || estimulo.observacao().isBlank() ? "" : " — " + estimulo.observacao()))
                .collect(Collectors.joining("\n"));
        return texto.isBlank() ? "Sem atividades marcadas como experimentadas." : texto;
    }

    private String resumoComData(LocalDate data, List<String> itens, String padrao) {
        return data.format(DATA) + "\n" + (itens.isEmpty() ? padrao : String.join(" · ", itens));
    }

    private String formatarMedida(BigDecimal valor, String unidade) {
        return valor == null ? "Não informado" : valor.stripTrailingZeros().toPlainString().replace('.', ',') + " " + unidade;
    }

    private String formatarMinutos(Integer minutos) {
        int horas = minutos / 60;
        int restante = minutos % 60;
        return horas > 0 ? horas + "h" + (restante > 0 ? " " + restante + "min" : "") : restante + " min";
    }

    private String rotuloSexo(String sexo) {
        return "MASCULINO".equals(sexo) ? "masculino" : "feminino";
    }

    private String rotuloIndicador(String indicador) {
        return switch (indicador) {
            case "PESO_POR_IDADE" -> "Peso por idade";
            case "COMPRIMENTO_POR_IDADE" -> "Comprimento por idade";
            case "PERIMETRO_CEFALICO_POR_IDADE" -> "Perímetro cefálico por idade";
            case "PESO_POR_COMPRIMENTO" -> "Peso por comprimento";
            case "IMC_POR_IDADE" -> "IMC por idade";
            default -> rotuloEnum(indicador);
        };
    }

    private String rotuloClassificacao(String classificacao) {
        return switch (classificacao) {
            case "DENTRO_DA_FAIXA_ESPERADA" -> "faixa esperada";
            case "FORA_DA_FAIXA_ESPERADA" -> "conversar na consulta";
            default -> rotuloEnum(classificacao);
        };
    }

    private String rotuloEnum(String valor) {
        String texto = valor.toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private String rotuloTrajetoria(StatusMarcoDesenvolvimento anterior) {
        if (anterior == null) return "Primeira observação registrada";
        return anterior == StatusMarcoDesenvolvimento.OBSERVADO ? "Observado novamente" : "Passou a ser observado";
    }

    private String rotuloRelato(String tipo) {
        return "PERDA_HABILIDADE".equals(tipo) ? "Perda de habilidade relatada" : "Preocupação da família";
    }

    private Crianca validar(UUID criancaId, String email) {
        var usuario = usuarios.buscarPorEmail(email).orElseThrow();
        if (!vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId)) {
            throw new IllegalArgumentException("Criança não encontrada.");
        }
        return criancas.buscarPorId(criancaId).orElseThrow();
    }
}
