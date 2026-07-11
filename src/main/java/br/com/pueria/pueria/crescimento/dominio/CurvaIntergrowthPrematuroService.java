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
public class CurvaIntergrowthPrematuroService {

    public static final String FONTE_INTERGROWTH = "INTERGROWTH-21st Postnatal Growth Standards for Preterm Infants (Villar et al., 2015)";
    private static final String BASE = "intergrowth/preterm-postnatal/";
    private final Map<IndicadorCurvaCrescimento, Map<Sexo, PontoZ[]>> tabelas = new EnumMap<>(IndicadorCurvaCrescimento.class);

    public CurvaIntergrowthPrematuroService() {
        carregar(IndicadorCurvaCrescimento.PESO_IDADE, Sexo.MASCULINO, "peso_meninos.csv");
        carregar(IndicadorCurvaCrescimento.PESO_IDADE, Sexo.FEMININO, "peso_meninas.csv");
        carregar(IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, Sexo.MASCULINO, "comprimento_meninos.csv");
        carregar(IndicadorCurvaCrescimento.COMPRIMENTO_IDADE, Sexo.FEMININO, "comprimento_meninas.csv");
        carregar(IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, Sexo.MASCULINO, "perimetro_cefalico_meninos.csv");
        carregar(IndicadorCurvaCrescimento.PERIMETRO_CEFALICO_IDADE, Sexo.FEMININO, "perimetro_cefalico_meninas.csv");
    }

    public Optional<ResultadoCurvaCrescimento> avaliar(IndicadorCurvaCrescimento indicador, Sexo sexo, int idadePosMenstrualDias, BigDecimal valor) {
        if (valor == null || sexo == null || sexo == Sexo.NAO_INFORMADO || idadePosMenstrualDias < 27 * 7 || idadePosMenstrualDias >= 64 * 7) return Optional.empty();
        PontoZ[] pontos = tabelas.get(indicador).get(sexo);
        double semana = idadePosMenstrualDias / 7.0;
        int base = (int) Math.floor(semana);
        PontoZ inferior = pontos[base - 27];
        PontoZ superior = pontos[Math.min(base + 1, 64) - 27];
        double fracao = semana - base;
        double[] valores = new double[7];
        for (int i = 0; i < 7; i++) valores[i] = inferior.z[i] + (superior.z[i] - inferior.z[i]) * fracao;
        double zScore = zPorValor(valor.doubleValue(), valores);
        return Optional.of(new ResultadoCurvaCrescimento(indicador, valor, indicador.getUnidade(), idadePosMenstrualDias,
                arredondar(zScore, 3), arredondar(normalAcumulada(zScore) * 100, 2), ClassificacaoCurvaCrescimento.porZScore(zScore), FONTE_INTERGROWTH));
    }

    private double zPorValor(double valor, double[] valores) {
        if (valor <= valores[0]) return -3 + (valor - valores[0]) / Math.max(valores[1] - valores[0], 0.0001);
        if (valor >= valores[6]) return 3 + (valor - valores[6]) / Math.max(valores[6] - valores[5], 0.0001);
        for (int i = 0; i < 6; i++) if (valor <= valores[i + 1]) return (i - 3) + (valor - valores[i]) / (valores[i + 1] - valores[i]);
        return 0;
    }

    private void carregar(IndicadorCurvaCrescimento indicador, Sexo sexo, String arquivo) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(BASE + arquivo).getInputStream(), StandardCharsets.UTF_8))) {
            PontoZ[] pontos = reader.lines().skip(1).map(linha -> {
                String[] p = linha.split(","); double[] z = new double[7]; for (int i = 0; i < 7; i++) z[i] = Double.parseDouble(p[i + 1]); return new PontoZ(Integer.parseInt(p[0]), z);
            }).toArray(PontoZ[]::new);
            if (pontos.length != 38) throw new IllegalStateException("Tabela INTERGROWTH incompleta: " + arquivo);
            tabelas.computeIfAbsent(indicador, chave -> new EnumMap<>(Sexo.class)).put(sexo, pontos);
        } catch (IOException e) { throw new IllegalStateException("Não foi possível carregar tabela INTERGROWTH " + arquivo, e); }
    }

    private double normalAcumulada(double z) { return 0.5 * (1 + erf(z / Math.sqrt(2))); }
    private double erf(double x) { double s = Math.signum(x), a = Math.abs(x), t = 1 / (1 + .3275911 * a); return s * (1 - (((((1.061405429*t-1.453152027)*t)+1.421413741)*t-.284496736)*t+.254829592)*t*Math.exp(-a*a)); }
    private double arredondar(double valor, int casas) { double f = Math.pow(10, casas); return Math.round(valor * f) / f; }
    private record PontoZ(int semana, double[] z) {}
}
