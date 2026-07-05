package br.com.pueria.pueria.crescimento.dominio;

import br.com.pueria.pueria.criancas.dominio.Sexo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CurvaOmsCrescimentoService {

    public static final String FONTE_OMS = "WHO Child Growth Standards, expanded z-score LMS tables, 0-5 years";
    private static final String BASE = "oms/child-growth-standards/";

    private final Map<IndicadorCurvaCrescimento, Map<Sexo, ParametroLmsOms[]>> tabelas = new EnumMap<>(IndicadorCurvaCrescimento.class);

    public CurvaOmsCrescimentoService() {
        carregar(IndicadorCurvaCrescimento.PESO_IDADE, Sexo.MASCULINO, "peso_idade_meninos_0_5.csv");
        carregar(IndicadorCurvaCrescimento.PESO_IDADE, Sexo.FEMININO, "peso_idade_meninas_0_5.csv");
        carregar(IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, Sexo.MASCULINO, "comprimento_idade_meninos_0_5.csv");
        carregar(IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, Sexo.FEMININO, "comprimento_idade_meninas_0_5.csv");
        carregar(IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, Sexo.MASCULINO, "perimetro_cefalico_idade_meninos_0_5.csv");
        carregar(IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, Sexo.FEMININO, "perimetro_cefalico_idade_meninas_0_5.csv");
    }

    public Optional<ResultadoCurvaCrescimento> avaliar(IndicadorCurvaCrescimento indicador, Sexo sexo, int idadeDias, BigDecimal valor) {
        if (valor == null || sexo == null || sexo == Sexo.NAO_INFORMADO || idadeDias < 0) {
            return Optional.empty();
        }

        ParametroLmsOms parametro = Optional.ofNullable(tabelas.get(indicador))
                .map(porSexo -> porSexo.get(sexo))
                .filter(linhas -> idadeDias < linhas.length)
                .map(linhas -> linhas[idadeDias])
                .orElse(null);

        if (parametro == null) {
            return Optional.empty();
        }

        double zScore = calcularZScore(valor.doubleValue(), parametro);
        double percentil = normalAcumulada(zScore) * 100.0;
        return Optional.of(new ResultadoCurvaCrescimento(
                indicador,
                valor,
                indicador.getUnidade(),
                idadeDias,
                arredondar(zScore, 3),
                arredondar(percentil, 2),
                ClassificacaoCurvaCrescimento.porZScore(zScore),
                FONTE_OMS
        ));
    }

    private void carregar(IndicadorCurvaCrescimento indicador, Sexo sexo, String arquivo) {
        ParametroLmsOms[] parametros = lerArquivo(arquivo);
        tabelas.computeIfAbsent(indicador, chave -> new EnumMap<>(Sexo.class)).put(sexo, parametros);
    }

    private ParametroLmsOms[] lerArquivo(String arquivo) {
        ClassPathResource resource = new ClassPathResource(BASE + arquivo);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .filter(linha -> !linha.isBlank())
                    .map(this::parsearLinha)
                    .toArray(ParametroLmsOms[]::new);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível carregar a tabela OMS " + arquivo, exception);
        }
    }

    private ParametroLmsOms parsearLinha(String linha) {
        String[] partes = linha.split(",");
        if (partes.length != 4) {
            throw new IllegalStateException("Linha inválida na tabela OMS: " + linha);
        }
        return new ParametroLmsOms(
                Integer.parseInt(partes[0]),
                Double.parseDouble(partes[1]),
                Double.parseDouble(partes[2]),
                Double.parseDouble(partes[3])
        );
    }

    private double calcularZScore(double valor, ParametroLmsOms parametro) {
        if (Math.abs(parametro.l()) < 0.0000001) {
            return Math.log(valor / parametro.m()) / parametro.s();
        }
        return (Math.pow(valor / parametro.m(), parametro.l()) - 1.0) / (parametro.l() * parametro.s());
    }

    private double normalAcumulada(double z) {
        return 0.5 * (1.0 + erf(z / Math.sqrt(2.0)));
    }

    private double erf(double x) {
        double sinal = Math.signum(x);
        double valor = Math.abs(x);
        double t = 1.0 / (1.0 + 0.3275911 * valor);
        double y = 1.0 - (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t - 0.284496736) * t + 0.254829592) * t * Math.exp(-valor * valor);
        return sinal * y;
    }

    private double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }
}
