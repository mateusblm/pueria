package br.com.pueria.pueria.home.aplicacao;

import br.com.pueria.pueria.alimentacao.aplicacao.ListarAlimentacaoUseCase;
import br.com.pueria.pueria.alimentacao.aplicacao.RegistroAlimentacaoDetalhado;
import br.com.pueria.pueria.crescimento.aplicacao.AvaliacaoCurvaCrescimento;
import br.com.pueria.pueria.crescimento.aplicacao.ListarAvaliacoesCurvaCrescimentoUseCase;
import br.com.pueria.pueria.crescimento.aplicacao.ListarMedidasCrescimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.aplicacao.GerenciarRelatosDesenvolvimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.aplicacao.ListarMarcosDesenvolvimentoUseCase;
import br.com.pueria.pueria.desenvolvimento.infraestrutura.web.ResumoHomeDesenvolvimentoResponse;
import br.com.pueria.pueria.home.infraestrutura.web.ResumoHomeResponse;
import br.com.pueria.pueria.saude.aplicacao.GerenciarRegistrosSaudeUseCase;
import br.com.pueria.pueria.saude.dominio.TipoRegistroSaude;
import br.com.pueria.pueria.sono.aplicacao.ListarSonoUseCase;
import br.com.pueria.pueria.sono.aplicacao.RegistroSonoDetalhado;
import br.com.pueria.pueria.telas.aplicacao.ListarTelasUseCase;
import br.com.pueria.pueria.telas.aplicacao.RegistroTelasDetalhado;
import br.com.pueria.pueria.transitointestinal.aplicacao.ListarTransitoIntestinalUseCase;
import br.com.pueria.pueria.transitointestinal.aplicacao.RegistroTransitoIntestinalDetalhado;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static br.com.pueria.pueria.home.infraestrutura.web.ResumoHomeResponse.*;

@Service
public class ResumoHomeService {
    private final ListarMarcosDesenvolvimentoUseCase marcos;
    private final GerenciarRelatosDesenvolvimentoUseCase relatos;
    private final ListarMedidasCrescimentoUseCase medidas;
    private final ListarAvaliacoesCurvaCrescimentoUseCase curvas;
    private final ListarSonoUseCase sono;
    private final ListarAlimentacaoUseCase alimentacao;
    private final ListarTelasUseCase telas;
    private final ListarTransitoIntestinalUseCase transito;
    private final GerenciarRegistrosSaudeUseCase saude;

    public ResumoHomeService(ListarMarcosDesenvolvimentoUseCase marcos, GerenciarRelatosDesenvolvimentoUseCase relatos,
                             ListarMedidasCrescimentoUseCase medidas, ListarAvaliacoesCurvaCrescimentoUseCase curvas,
                             ListarSonoUseCase sono, ListarAlimentacaoUseCase alimentacao, ListarTelasUseCase telas,
                             ListarTransitoIntestinalUseCase transito, GerenciarRegistrosSaudeUseCase saude) {
        this.marcos = marcos; this.relatos = relatos; this.medidas = medidas; this.curvas = curvas;
        this.sono = sono; this.alimentacao = alimentacao; this.telas = telas; this.transito = transito; this.saude = saude;
    }

    public ResumoHomeResponse executar(UUID criancaId, String email) {
        var desenvolvimento = ResumoHomeDesenvolvimentoResponse.de(marcos.executar(criancaId, email), relatos.listar(criancaId, email));
        var areas = List.of(
                crescimento(criancaId, email),
                desenvolvimento(desenvolvimento),
                sono(criancaId, email),
                alimentacao(criancaId, email),
                humor(criancaId, email),
                telas(criancaId, email),
                transito(criancaId, email),
                saude(criancaId, email)
        );
        Estado estado = areas.stream().anyMatch(area -> area.estado() == EstadoArea.ATENCAO) ? Estado.ATENCAO
                : desenvolvimento.estado() == ResumoHomeDesenvolvimentoResponse.Estado.INICIAL ? Estado.INICIAL
                : areas.stream().anyMatch(area -> area.estado() == EstadoArea.ACOMPANHAR) ? Estado.ACOMPANHAR
                : Estado.TRANQUILO;
        return new ResumoHomeResponse(estado, areas);
    }

    private Area crescimento(UUID id, String email) {
        int quantidade = medidas.executar(id, email).size();
        if (quantidade == 0) return semRegistros(Modulo.CRESCIMENTO);
        var ultima = curvas.executar(id, email).stream().max(Comparator.comparing(AvaliacaoCurvaCrescimento::dataMedicao));
        if (ultima.isEmpty() || ultima.get().resultados().isEmpty()) return new Area(Modulo.CRESCIMENTO, EstadoArea.ACOMPANHAR, "Medidas registradas", quantidade, 0, Acao.VER);
        boolean esperado = ultima.get().resultados().stream().allMatch(resultado -> resultado.classificacao().name().equals("FAIXA_ESPERADA"));
        return new Area(Modulo.CRESCIMENTO, esperado ? EstadoArea.EM_DIA : EstadoArea.ACOMPANHAR,
                esperado ? "Dentro da faixa esperada" : "Acompanhar curva", quantidade,
                esperado ? 0 : 1, Acao.VER);
    }

    private Area desenvolvimento(ResumoHomeDesenvolvimentoResponse resumo) {
        return switch (resumo.estado()) {
            case INICIAL -> new Area(Modulo.NEURODESENVOLVIMENTO, EstadoArea.INICIAL, "Ainda não iniciado", resumo.respondidos(), 0, Acao.COMECAR);
            case ATENCAO -> new Area(Modulo.NEURODESENVOLVIMENTO, EstadoArea.ATENCAO,
                    resumo.temPerdaHabilidade() ? "Há uma habilidade para conversar" : pontos(resumo.pontosAtencao(), "ponto em observação", "pontos em observação"),
                    resumo.respondidos(), resumo.pontosAtencao() + (resumo.temPerdaHabilidade() ? 1 : 0), Acao.VER);
            case TRANQUILO -> new Area(Modulo.NEURODESENVOLVIMENTO, EstadoArea.EM_DIA, "Fase acompanhada", resumo.respondidos(), 0, Acao.VER);
        };
    }

    private Area sono(UUID id, String email) {
        List<RegistroSonoDetalhado> registros = sono.executar(id, email);
        if (registros.isEmpty()) return semRegistros(Modulo.SONO);
        var analise = registros.getLast().analise();
        int pontos = analise.conversaConsulta().size();
        if (pontos > 0) return new Area(Modulo.SONO, EstadoArea.ATENCAO, "Vale conversar sobre o sono", registros.size(), pontos, Acao.VER);
        return new Area(Modulo.SONO, "FAIXA_ESPERADA".equals(analise.classificacaoDuracao()) ? EstadoArea.EM_DIA : EstadoArea.ACOMPANHAR,
                "FAIXA_ESPERADA".equals(analise.classificacaoDuracao()) ? "Rotina regular" : "Acompanhar rotina", registros.size(), 0, Acao.VER);
    }

    private Area alimentacao(UUID id, String email) {
        List<RegistroAlimentacaoDetalhado> registros = alimentacao.executar(id, email);
        if (registros.isEmpty()) return semRegistros(Modulo.ALIMENTACAO);
        var analise = registros.getLast().analise();
        int pontos = analise.conversaConsulta().size();
        return new Area(Modulo.ALIMENTACAO, pontos > 0 ? EstadoArea.ATENCAO : EstadoArea.EM_DIA,
                pontos > 0 ? "Pontos para conversar" : "Registro alimentar atualizado", registros.size(), pontos, Acao.VER);
    }

    private Area telas(UUID id, String email) {
        List<RegistroTelasDetalhado> registros = telas.executar(id, email);
        if (registros.isEmpty()) return semRegistros(Modulo.TELAS);
        var analise = registros.getLast().analise();
        int pontos = analise.conversaConsulta().size();
        boolean esperado = "DENTRO_DA_REFERENCIA".equals(analise.classificacaoTempo()) && pontos == 0;
        return new Area(Modulo.TELAS, esperado ? EstadoArea.EM_DIA : EstadoArea.ATENCAO,
                esperado ? "Dentro da referência" : "Rotina de telas para acompanhar", registros.size(), pontos, Acao.VER);
    }

    private Area transito(UUID id, String email) {
        List<RegistroTransitoIntestinalDetalhado> registros = transito.executar(id, email);
        if (registros.isEmpty()) return semRegistros(Modulo.TRANSITO_INTESTINAL);
        var analise = registros.getFirst().analise();
        int pontos = analise.conversaConsulta().size();
        boolean esperado = "ESPERADA".equals(analise.classificacaoFezes()) && pontos == 0;
        return new Area(Modulo.TRANSITO_INTESTINAL, esperado ? EstadoArea.EM_DIA : EstadoArea.ATENCAO,
                esperado ? "Rotina intestinal registrada" : "Pontos para acompanhar", registros.size(), pontos, Acao.VER);
    }

    private Area humor(UUID id, String email) {
        int quantidade = (int) saude.listar(id, email).stream().filter(registro -> registro.getTipo() == TipoRegistroSaude.HUMOR_COMPORTAMENTO).count();
        return quantidade == 0 ? semRegistros(Modulo.HUMOR) : new Area(Modulo.HUMOR, EstadoArea.ACOMPANHAR, pontos(quantidade, "registro de humor", "registros de humor"), quantidade, 0, Acao.VER);
    }

    private Area saude(UUID id, String email) {
        int quantidade = saude.listar(id, email).size();
        return quantidade == 0 ? semRegistros(Modulo.SAUDE) : new Area(Modulo.SAUDE, EstadoArea.ACOMPANHAR, pontos(quantidade, "registro de saúde", "registros de saúde"), quantidade, 0, Acao.VER);
    }

    private Area semRegistros(Modulo modulo) { return new Area(modulo, EstadoArea.SEM_REGISTROS, "Sem registros", 0, 0, Acao.REGISTRAR); }
    private String pontos(int quantidade, String singular, String plural) { return quantidade == 1 ? "1 " + singular : quantidade + " " + plural; }
}
