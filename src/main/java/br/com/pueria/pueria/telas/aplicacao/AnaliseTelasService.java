package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.IdadeCrianca;
import br.com.pueria.pueria.telas.dominio.RegistroTelas;
import br.com.pueria.pueria.telas.dominio.TipoConteudoTela;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnaliseTelasService {

    public AnaliseTelas analisar(Crianca crianca, RegistroTelas registro) {
        IdadeCrianca idade = crianca.idadeEm(registro.getDataRegistro());
        ReferenciaTelas referencia = referencia(idade.mesesCompletos());
        Integer minutos = registro.minutosMediosDia();

        List<String> rotina = new ArrayList<>();
        List<String> conversaConsulta = new ArrayList<>();
        List<String> habitosApoio = new ArrayList<>();

        String classificacao = "SEM_DADOS";
        if (minutos == null) {
            rotina.add("Registre o tempo aproximado para entender se as telas aparecem como rotina ou apenas em momentos pontuais.");
        } else if (minutos > referencia.maximoMinutos()) {
            classificacao = "ACIMA_DA_REFERENCIA";
            if (referencia.maximoMinutos() == 0 && Boolean.TRUE.equals(registro.getVideochamadaFamilia())
                    && registro.getTipoConteudoPredominante() == TipoConteudoTela.VIDEOCHAMADA) {
                conversaConsulta.add("Videochamadas com familiares sao diferentes de consumo passivo de tela; ainda assim, vale registrar frequencia e contexto.");
            } else if (referencia.maximoMinutos() == 0) {
                conversaConsulta.add("Para menores de 2 anos, telas de rotina nao sao recomendadas. Se reduzir estiver dificil, leve o contexto para a consulta.");
            } else {
                conversaConsulta.add("O tempo medio ficou acima da referencia para a idade. Observe se compete com sono, refeicoes, brincadeira ativa e interacao familiar.");
            }
        } else {
            classificacao = "DENTRO_DA_REFERENCIA";
            rotina.add("O tempo medio informado ficou dentro da referencia usada para a idade.");
        }

        if (minutos != null) {
            rotina.add("Tempo medio registrado: " + formatarMinutos(minutos) + " por dia.");
        }
        if (Boolean.TRUE.equals(registro.getUsoAcompanhadoAdulto())) {
            rotina.add("O uso acompanhado por adulto ajuda a transformar a tela em conversa, nomeacao e interacao.");
        }
        if (Boolean.TRUE.equals(registro.getConteudoAdultoSupervisionado())) {
            rotina.add("O conteudo foi registrado como selecionado ou supervisionado por adulto.");
        }
        if (!registro.getContextosUso().isEmpty()) {
            rotina.add("Os aparelhos e os conteúdos mais comuns foram registrados para acompanhar a rotina.");
        }
        if (Boolean.TRUE.equals(registro.getCriancaEscolheConteudoLivremente())) {
            habitosApoio.add("Combinar previamente o que será visto pode deixar o uso mais previsível para a criança e a família.");
        }
        if (Boolean.TRUE.equals(registro.getLeituraBrincadeiraSemTela())) {
            rotina.add("A rotina inclui leitura, conversa ou brincadeira sem tela.");
        }
        if (Boolean.TRUE.equals(registro.getBrincaAoArLivre())) {
            rotina.add("A crianca teve brincadeira ativa ou ao ar livre registrada.");
        }
        if (Boolean.TRUE.equals(registro.getTelaDuranteRefeicoes())) {
            habitosApoio.add("Refeicoes sem tela favorecem percepcao de fome e saciedade, conversa e participacao da crianca.");
        }
        if (Boolean.TRUE.equals(registro.getTelaAntesDormir())) {
            habitosApoio.add("Evitar telas perto do sono pode ajudar a proteger o inicio do sono e a rotina noturna.");
        }
        if (Boolean.TRUE.equals(registro.getTelaParaAcalmar())) {
            habitosApoio.add("Quando a tela aparece para acalmar, vale observar os momentos mais dificeis e testar alternativas de acolhimento gradual.");
        }
        if (Boolean.TRUE.equals(registro.getTelaEmSegundoPlano())) {
            habitosApoio.add("Tela ligada ao fundo pode competir com brincadeira, linguagem e interacao, mesmo quando a crianca parece nao assistir.");
        }
        if (Boolean.TRUE.equals(registro.getTelaAoAcordar())) {
            habitosApoio.add("Comecar o dia sem tela pode abrir espaco para movimento, alimentacao e contato com a familia.");
        }
        if (Boolean.TRUE.equals(registro.getAutoplayAtivo()) || Boolean.TRUE.equals(registro.getNotificacoesAtivas())) {
            habitosApoio.add("Desativar autoplay e notificacoes ajuda a reduzir o uso por impulso e facilita combinados familiares.");
        }
        if (Boolean.TRUE.equals(registro.getDispositivoNoQuarto())) {
            habitosApoio.add("Manter dispositivos fora do quarto costuma facilitar limites e proteger a rotina de sono.");
        }
        if (Boolean.TRUE.equals(registro.getPreocupacaoFamilia())) {
            conversaConsulta.add("A preocupacao da familia e um dado importante. Leve o registro de rotina e contexto para conversar com o pediatra.");
        }

        String resumo = resumo(classificacao, referencia);
        return new AnaliseTelas(
                "Telas e rotina",
                resumo,
                minutos,
                referencia.maximoMinutos(),
                classificacao,
                List.copyOf(rotina),
                List.copyOf(conversaConsulta),
                List.copyOf(habitosApoio)
        );
    }

    private ReferenciaTelas referencia(int idadeMeses) {
        if (idadeMeses < 24) {
            return new ReferenciaTelas(0);
        }
        if (idadeMeses < 60) {
            return new ReferenciaTelas(60);
        }
        return new ReferenciaTelas(120);
    }

    private String resumo(String classificacao, ReferenciaTelas referencia) {
        return switch (classificacao) {
            case "ACIMA_DA_REFERENCIA" -> referencia.maximoMinutos() == 0
                    ? "O registro mostra tela de rotina em uma faixa etaria em que a recomendacao e evitar esse uso."
                    : "O tempo registrado ficou acima da referencia para a idade.";
            case "DENTRO_DA_REFERENCIA" -> "O tempo registrado ficou dentro da referencia para a idade.";
            default -> "Registro salvo. Com o tempo aproximado, o Pueria compara com a referencia por idade e observa o contexto de uso.";
        };
    }

    private String formatarMinutos(Integer minutos) {
        if (minutos == null) {
            return "nao informado";
        }
        if (minutos < 60) {
            return minutos + " min";
        }
        int horas = minutos / 60;
        int resto = minutos % 60;
        if (resto == 0) {
            return horas + "h";
        }
        return horas + "h" + resto + "min";
    }

    private record ReferenciaTelas(int maximoMinutos) {}
}
