package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.EstagioAlimentar;
import br.com.pueria.pueria.alimentacao.dominio.GrupoAlimento;
import br.com.pueria.pueria.alimentacao.dominio.RegistroAlimentacao;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.IdadeCrianca;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnaliseAlimentacaoService {

    public AnaliseAlimentacao analisar(Crianca crianca, RegistroAlimentacao registro) {
        IdadeCrianca idade = crianca.idadeEm(registro.getDataRegistro());
        int meses = idade.mesesCompletos();

        List<String> rotina = new ArrayList<>();
        List<String> conversaConsulta = new ArrayList<>();
        List<String> habitosApoio = new ArrayList<>();

        if (meses < 6) {
            rotina.add("Nesta idade, o foco costuma ser leite materno e/ou fórmula infantil, conforme orientação da equipe de saúde.");
            if (registro.getEstagioAlimentar() != EstagioAlimentar.APENAS_LEITE && registro.getEstagioAlimentar() != EstagioAlimentar.NAO_INFORMADO) {
                conversaConsulta.add("Como a criança ainda tem menos de 6 meses, vale alinhar qualquer oferta além de leite com o pediatra.");
            }
        } else if (meses < 24) {
            rotina.add("A partir de cerca de 6 meses, a alimentação complementar entra junto com leite materno e/ou fórmula, respeitando prontidão e segurança.");
            if (registro.getEstagioAlimentar() == EstagioAlimentar.APENAS_LEITE) {
                conversaConsulta.add("Se a alimentação complementar ainda não começou, leve esse ponto à próxima consulta.");
            }
            if (Boolean.FALSE.equals(registro.getConsomeFrutas())
                    || Boolean.FALSE.equals(registro.getConsomeLegumes())
                    || Boolean.FALSE.equals(registro.getConsomeVerduras())
                    || Boolean.FALSE.equals(registro.getConsomeCarnesOvos())) {
                habitosApoio.add("Observar variedade de grupos alimentares ajuda a planejar próximas ofertas com mais clareza.");
            }
        } else {
            rotina.add("Nesta fase, a rotina familiar, a variedade e o ambiente das refeições costumam ter papel importante na relação com a comida.");
            if (Boolean.TRUE.equals(registro.getSeletividadeAlimentar()) || Boolean.TRUE.equals(registro.getRecusaPersistente())) {
                conversaConsulta.add("Se a seletividade ou a recusa estiverem persistentes, registre exemplos e converse na consulta.");
            }
        }

        if (Boolean.TRUE.equals(registro.getEngasgosFrequentes())) {
            conversaConsulta.add("Engasgos frequentes devem ser conversados com o pediatra, especialmente se forem recorrentes ou associados a tosse, vômitos ou desconforto.");
        }
        if (Boolean.TRUE.equals(registro.getVomitosRecorrentes())) {
            conversaConsulta.add("Vômitos recorrentes merecem ser levados à consulta, principalmente se persistirem ou vierem com perda de peso percebida.");
        }
        if (Boolean.TRUE.equals(registro.getConstipacao())) {
            conversaConsulta.add("Constipação frequente pode ser registrada para discutir rotina, hidratação e alimentação na consulta.");
        }
        if (Boolean.TRUE.equals(registro.getDiarreiaRecorrente())) {
            conversaConsulta.add("Diarreia recorrente deve ser acompanhada com o pediatra, especialmente se houver sinais de desidratação ou piora do estado geral.");
        }
        if (Boolean.TRUE.equals(registro.getDificuldadeGanhoPesoPercebida()) || Boolean.TRUE.equals(registro.getPreocupacaoFamilia())) {
            conversaConsulta.add("A preocupação da família é um dado importante. Leve o histórico alimentar e de crescimento para a consulta.");
        }

        if (Boolean.TRUE.equals(registro.getUltraprocessadosFrequentes())) {
            habitosApoio.add("Reduzir ultraprocessados pode ajudar a proteger a qualidade da alimentação de rotina.");
        }
        if (Boolean.TRUE.equals(registro.getBebidasAdocadas()) || Boolean.TRUE.equals(registro.getAcucarAdicionado())) {
            habitosApoio.add("Bebidas adoçadas e açúcar adicionado são pontos úteis para revisar na rotina alimentar.");
        }
        if (Boolean.TRUE.equals(registro.getTelasDuranteRefeicoes())) {
            habitosApoio.add("Refeições sem telas favorecem atenção aos sinais de fome, saciedade e interação familiar.");
        }
        if (Boolean.FALSE.equals(registro.getRotinaAlimentarRegular())) {
            habitosApoio.add("Local e horários previsíveis para as refeições podem facilitar a aceitação gradual dos alimentos.");
        }
        if (Boolean.TRUE.equals(registro.getRefeicoesEmFamilia())) {
            rotina.add("Refeições em família foram registradas como parte da rotina.");
        }
        if (!registro.getAlimentosOferecidos().isEmpty()) {
            rotina.add("Foram marcados " + registro.getAlimentosOferecidos().size() + " alimento(s) neste registro.");
            long grupos = registro.getAlimentosOferecidos().stream()
                    .map(alimento -> alimento.grupo())
                    .collect(Collectors.toSet())
                    .size();
            if (grupos >= 3) {
                habitosApoio.add("O registro mostra contato com diferentes grupos de alimentos, o que ajuda a acompanhar variedade sem pressa e sem pressão.");
            }
            boolean temFruta = temGrupo(registro, GrupoAlimento.FRUTA);
            boolean temLegumeOuVerdura = temGrupo(registro, GrupoAlimento.LEGUME_HORTALICA_FRUTO)
                    || temGrupo(registro, GrupoAlimento.VERDURA_FOLHA);
            if (!temFruta || !temLegumeOuVerdura) {
                habitosApoio.add("Registrar frutas, legumes e verduras separadamente ajuda a perceber quais alimentos já entraram na rotina e quais podem ser oferecidos aos poucos.");
            }
        }

        String resumo = conversaConsulta.isEmpty()
                ? "Registro alimentar salvo para acompanhar a rotina e observar mudanças ao longo do tempo."
                : "Há pontos registrados que podem ajudar a orientar a próxima conversa com o pediatra.";

        return new AnaliseAlimentacao(
                "Alimentação e rotina",
                resumo,
                List.copyOf(rotina),
                List.copyOf(conversaConsulta),
                List.copyOf(habitosApoio)
        );
    }

    private boolean temGrupo(RegistroAlimentacao registro, GrupoAlimento grupo) {
        return registro.getAlimentosOferecidos().stream().anyMatch(alimento -> alimento.grupo() == grupo);
    }
}
