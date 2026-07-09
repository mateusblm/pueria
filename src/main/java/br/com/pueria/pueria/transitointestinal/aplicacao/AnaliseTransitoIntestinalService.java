package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.transitointestinal.dominio.RegistroTransitoIntestinal;
import br.com.pueria.pueria.transitointestinal.dominio.TipoFezesBristol;
import br.com.pueria.pueria.transitointestinal.dominio.FacilidadeLimpezaFezes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnaliseTransitoIntestinalService {

    public AnaliseTransitoIntestinal analisar(RegistroTransitoIntestinal registro) {
        List<String> rotina = new ArrayList<>();
        List<String> conversaConsulta = new ArrayList<>();
        List<String> habitosApoio = new ArrayList<>();

        String classificacao = classificacao(registro.getTipoFezes());
        if (registro.getTipoFezes() != TipoFezesBristol.NAO_INFORMADO) {
            rotina.add(descricaoTipo(registro.getTipoFezes()));
        }

        if (registro.getEvacuacoesPorDia() != null) {
            rotina.add("Evacuações registradas no dia: " + registro.getEvacuacoesPorDia() + ".");
        }
        if (Boolean.TRUE.equals(registro.getConstipacao()) || registro.getTipoFezes() == TipoFezesBristol.TIPO_1 || registro.getTipoFezes() == TipoFezesBristol.TIPO_2) {
            conversaConsulta.add("Fezes muito endurecidas ou evacuação difícil merecem acompanhamento, especialmente se persistirem.");
        }
        if (Boolean.TRUE.equals(registro.getDiarreia()) || registro.getTipoFezes() == TipoFezesBristol.TIPO_6 || registro.getTipoFezes() == TipoFezesBristol.TIPO_7) {
            conversaConsulta.add("Fezes muito líquidas ou diarreia recorrente devem ser conversadas com o pediatra, principalmente se houver piora do estado geral.");
        }
        if (Boolean.TRUE.equals(registro.getRaiasSangue())) {
            conversaConsulta.add("Raias de sangue nas fezes devem ser levadas ao pediatra para orientação.");
        }
        if (Boolean.TRUE.equals(registro.getMuco())) {
            conversaConsulta.add("Muco nas fezes pode ajudar a contextualizar a avaliação se aparecer de forma repetida.");
        }
        if (Boolean.TRUE.equals(registro.getDorEvacuar())) {
            conversaConsulta.add("Dor para evacuar merece atenção, principalmente se vier junto de fezes endurecidas ou retenção.");
        }
        if (Boolean.TRUE.equals(registro.getEscapeFecal())) {
            conversaConsulta.add("Escape de fezes deve ser registrado e conversado na consulta se ocorrer mais de uma vez.");
        }
        if (Boolean.TRUE.equals(registro.getAssaduraFrequente())
                || Boolean.TRUE.equals(registro.getAssaduraVermelhidao())
                || Boolean.TRUE.equals(registro.getAssaduraPontosVermelhos())) {
            conversaConsulta.add("Assaduras frequentes ou com pontos vermelhos ajudam a orientar a avaliação da pele e da rotina de troca.");
        }
        if (Boolean.TRUE.equals(registro.getRestosAlimentares())) {
            habitosApoio.add("Restos alimentares podem ser anotados junto do que foi consumido para observar repetição ao longo dos dias.");
        }
        if (registro.getFacilidadeLimpeza() == FacilidadeLimpezaFezes.DIFICIL) {
            habitosApoio.add("Fezes difíceis de limpar podem indicar mudança de consistência; observar junto de alimentos, hidratação e frequência.");
        }
        if (Boolean.TRUE.equals(registro.getPreocupacaoFamilia())) {
            conversaConsulta.add("A preocupação da família é um dado importante. Leve o registro de alguns dias para a consulta.");
        }

        String resumo = conversaConsulta.isEmpty()
                ? "Registro salvo para acompanhar o padrão intestinal ao longo dos dias."
                : "Há pontos registrados que podem ajudar a próxima conversa com o pediatra.";

        return new AnaliseTransitoIntestinal(
                "Trânsito intestinal",
                resumo,
                classificacao,
                List.copyOf(rotina),
                List.copyOf(conversaConsulta),
                List.copyOf(habitosApoio)
        );
    }

    private String classificacao(TipoFezesBristol tipo) {
        return switch (tipo) {
            case TIPO_1, TIPO_2 -> "ENDURECIDA";
            case TIPO_3, TIPO_4 -> "ESPERADA";
            case TIPO_5 -> "MAIS_MACIA";
            case TIPO_6, TIPO_7 -> "LIQUIDA";
            case NAO_INFORMADO -> "SEM_DADOS";
        };
    }

    private String descricaoTipo(TipoFezesBristol tipo) {
        return switch (tipo) {
            case TIPO_1 -> "Tipo 1 na escala de Bristol: bolinhas endurecidas, difíceis de eliminar.";
            case TIPO_2 -> "Tipo 2 na escala de Bristol: formato alongado, mas endurecido e irregular.";
            case TIPO_3 -> "Tipo 3 na escala de Bristol: formato alongado com pequenas fissuras.";
            case TIPO_4 -> "Tipo 4 na escala de Bristol: formato alongado, macio e liso.";
            case TIPO_5 -> "Tipo 5 na escala de Bristol: pedaços macios e separados.";
            case TIPO_6 -> "Tipo 6 na escala de Bristol: fezes pastosas ou muito amolecidas.";
            case TIPO_7 -> "Tipo 7 na escala de Bristol: fezes líquidas, sem pedaços sólidos.";
            case NAO_INFORMADO -> "Tipo de fezes não informado.";
        };
    }
}
