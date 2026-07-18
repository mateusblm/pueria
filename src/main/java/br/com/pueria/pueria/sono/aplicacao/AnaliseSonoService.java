package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.IdadeCrianca;
import br.com.pueria.pueria.sono.dominio.RegistroSono;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnaliseSonoService {

    public AnaliseSono analisar(Crianca crianca, RegistroSono registro) {
        IdadeCrianca idade = crianca.idadeEm(registro.getDataRegistro());
        FaixaSono faixa = faixaEsperada(idade.mesesCompletos());
        Integer total = registro.minutosSonoTotal24h();

        List<String> rotina = new ArrayList<>();
        List<String> conversaConsulta = new ArrayList<>();
        List<String> habitosApoio = new ArrayList<>();

        String classificacao = "SEM_DADOS";
        if (total == null) {
            rotina.add("Registre horário de dormir, horário de acordar e cochilos para comparar o sono total em 24 horas.");
        } else if (total < faixa.minimoMinutos()) {
            classificacao = "ABAIXO_DA_FAIXA";
            conversaConsulta.add("O sono total registrado ficou abaixo da faixa esperada para a idade. Se esse padrão se repetir, vale levar para a consulta.");
        } else if (total > faixa.maximoMinutos()) {
            classificacao = "ACIMA_DA_FAIXA";
            conversaConsulta.add("O sono total registrado ficou acima da faixa usual para a idade. Observe se há sonolência, cansaço ou mudança de comportamento.");
        } else {
            classificacao = "FAIXA_ESPERADA";
            rotina.add("O sono total registrado ficou dentro da faixa esperada para a idade.");
        }

        if (registro.getHorarioDormiu() != null && registro.getHorarioAcordou() != null) {
            rotina.add("Sono noturno registrado: " + formatarHoras(registro.minutosSonoNoturno()) + ".");
        }
        if (registro.getMinutosCochilos() != null && registro.getMinutosCochilos() > 0) {
            rotina.add("Cochilos registrados: " + formatarHoras(registro.getMinutosCochilos()) + " ao longo do dia.");
        }
        if (registro.getDespertaresNoturnos() != null && registro.getDespertaresNoturnos() >= 3) {
            habitosApoio.add("Despertares frequentes podem ser acompanhados por alguns dias para entender padrão, horários e possíveis gatilhos.");
        }
        if (Boolean.TRUE.equals(registro.getDificuldadeIniciarSono())) {
            habitosApoio.add("Dificuldade para iniciar o sono pode melhorar com rotina previsível, ambiente calmo e horários consistentes.");
        }
        if (Boolean.FALSE.equals(registro.getRotinaSonoConsistente())) {
            habitosApoio.add("Uma sequência parecida antes de dormir ajuda a criança a reconhecer a transição para o sono.");
        }
        if (Boolean.TRUE.equals(registro.getTelasAntesDormir())) {
            habitosApoio.add("Telas perto do horário de dormir podem atrapalhar o início do sono; vale observar esse hábito na rotina.");
        }
        if (Boolean.TRUE.equals(registro.getRoncosFrequentes())) {
            conversaConsulta.add("Roncos frequentes devem ser comentados com o pediatra, especialmente se vierem com sono agitado ou cansaço durante o dia.");
        }
        if (Boolean.TRUE.equals(registro.getPausasRespiratoriasPercebidas())) {
            conversaConsulta.add("Pausas respiratórias percebidas durante o sono merecem avaliação com o pediatra.");
        }
        if (Boolean.TRUE.equals(registro.getSonolenciaDiurna()) || Boolean.TRUE.equals(registro.getDificilDeSerAcordado()) || Boolean.TRUE.equals(registro.getMalHumorado()) || Boolean.TRUE.equals(registro.getIrritado()) || Boolean.TRUE.equals(registro.getIrritabilidadeCansaco())) {
            conversaConsulta.add("Sonolência ou mudanças de humor durante o dia ajudam a contextualizar a qualidade do sono na consulta.");
        }
        if (Boolean.TRUE.equals(registro.getPreocupacaoFamilia())) {
            conversaConsulta.add("A preocupação da família é um dado importante. Leve o registro de alguns dias para a próxima conversa com o pediatra.");
        }

        String resumo = total == null
                ? "Registro salvo. Com horários completos, o Pueria compara o sono total com a faixa esperada para a idade."
                : resumoPorClassificacao(classificacao);

        return new AnaliseSono(
                "Sono e rotina",
                resumo,
                total,
                faixa.minimoMinutos(),
                faixa.maximoMinutos(),
                classificacao,
                List.copyOf(rotina),
                List.copyOf(conversaConsulta),
                List.copyOf(habitosApoio)
        );
    }

    private FaixaSono faixaEsperada(int idadeMeses) {
        if (idadeMeses <= 3) {
            return new FaixaSono(14 * 60, 17 * 60);
        }
        if (idadeMeses <= 11) {
            return new FaixaSono(12 * 60, 16 * 60);
        }
        if (idadeMeses <= 23) {
            return new FaixaSono(11 * 60, 14 * 60);
        }
        if (idadeMeses <= 59) {
            return new FaixaSono(10 * 60, 13 * 60);
        }
        return new FaixaSono(9 * 60, 12 * 60);
    }

    private String resumoPorClassificacao(String classificacao) {
        return switch (classificacao) {
            case "ABAIXO_DA_FAIXA" -> "O registro ficou abaixo da faixa esperada para a idade. O mais importante é observar se isso se repete.";
            case "ACIMA_DA_FAIXA" -> "O registro ficou acima da faixa usual para a idade. Observe junto de energia, humor e rotina.";
            case "FAIXA_ESPERADA" -> "O registro ficou dentro da faixa esperada para a idade.";
            default -> "Registro de sono salvo.";
        };
    }

    private String formatarHoras(Integer minutos) {
        if (minutos == null) {
            return "não informado";
        }
        int horas = minutos / 60;
        int resto = minutos % 60;
        if (resto == 0) {
            return horas + "h";
        }
        return horas + "h" + resto + "min";
    }

    private record FaixaSono(int minimoMinutos, int maximoMinutos) {}
}
