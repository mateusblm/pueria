package br.com.pueria.pueria.desenvolvimento.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.AreaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.EstimuloDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.EstimuloDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.IdadeReferenciaDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroEstimuloDesenvolvimento;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroEstimuloDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.RegistroMarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.MarcoDesenvolvimentoRepositorio;
import br.com.pueria.pueria.desenvolvimento.dominio.StatusMarcoDesenvolvimento;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GerenciarEstimulosDesenvolvimentoUseCase {
    private final CriancaRepositorio criancas;
    private final UsuarioRepositorio usuarios;
    private final VinculoResponsavelCriancaRepositorio vinculos;
    private final EstimuloDesenvolvimentoRepositorio estimulos;
    private final RegistroEstimuloDesenvolvimentoRepositorio registrosEstimulos;
    private final RegistroMarcoDesenvolvimentoRepositorio registrosMarcos;
    private final MarcoDesenvolvimentoRepositorio marcos;

    public GerenciarEstimulosDesenvolvimentoUseCase(CriancaRepositorio criancas, UsuarioRepositorio usuarios,
            VinculoResponsavelCriancaRepositorio vinculos, EstimuloDesenvolvimentoRepositorio estimulos,
            RegistroEstimuloDesenvolvimentoRepositorio registrosEstimulos, RegistroMarcoDesenvolvimentoRepositorio registrosMarcos,
            MarcoDesenvolvimentoRepositorio marcos) {
        this.criancas = criancas; this.usuarios = usuarios; this.vinculos = vinculos; this.estimulos = estimulos;
        this.registrosEstimulos = registrosEstimulos; this.registrosMarcos = registrosMarcos; this.marcos = marcos;
    }

    @Transactional(readOnly = true)
    public List<EstimuloDesenvolvimentoResumo> listar(UUID criancaId, String email) {
        Crianca crianca = validarAcesso(criancaId, email);
        int idade = IdadeReferenciaDesenvolvimento.mesesParaCheckpoints(crianca, LocalDate.now());
        Set<AreaDesenvolvimento> areasPrioritarias = registrosMarcos.listarPorCrianca(criancaId).stream()
                .filter(registro -> registro.getStatus() == StatusMarcoDesenvolvimento.AINDA_NAO_OBSERVADO || registro.getStatus() == StatusMarcoDesenvolvimento.NAO_TENHO_CERTEZA)
                .map(registro -> marcos.buscarPorId(registro.getMarcoId()).map(marco -> marco.getArea()).orElse(null))
                .filter(area -> area != null).collect(Collectors.toSet());
        Map<UUID, RegistroEstimuloDesenvolvimento> registros = registrosEstimulos.listarPorCrianca(criancaId).stream()
                .collect(Collectors.toMap(RegistroEstimuloDesenvolvimento::estimuloId, Function.identity()));
        return estimulos.listarAtivosParaIdade(idade).stream()
                .sorted(Comparator.comparing((EstimuloDesenvolvimento item) -> !areasPrioritarias.contains(item.area())).thenComparing(EstimuloDesenvolvimento::area))
                .limit(2).map(item -> paraResumo(item, registros.get(item.id()))).toList();
    }

    @Transactional
    public RegistroEstimuloDesenvolvimento registrar(UUID criancaId, UUID estimuloId, String observacao, String email) {
        validarAcesso(criancaId, email);
        estimulos.buscarPorId(estimuloId).orElseThrow(() -> new RecursoNaoEncontradoException("Atividade não encontrada."));
        return registrosEstimulos.buscarPorCriancaEEstimulo(criancaId, estimuloId)
                .orElseGet(() -> registrosEstimulos.salvar(RegistroEstimuloDesenvolvimento.registrar(criancaId, estimuloId, observacao)));
    }

    private Crianca validarAcesso(UUID criancaId, String email) {
        Usuario usuario = usuarios.buscarPorEmail(email).filter(Usuario::isAtivo).orElseThrow(() -> new RecursoNaoEncontradoException("Responsável autenticado não encontrado."));
        if (!vinculos.usuarioPodeAcessarCrianca(usuario.getId(), criancaId)) throw new RecursoNaoEncontradoException("Criança não encontrada.");
        return criancas.buscarPorId(criancaId).orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));
    }

    private EstimuloDesenvolvimentoResumo paraResumo(EstimuloDesenvolvimento estimulo, RegistroEstimuloDesenvolvimento registro) {
        return new EstimuloDesenvolvimentoResumo(estimulo.id(), estimulo.area(), estimulo.titulo(), estimulo.descricao(), estimulo.cuidado(), estimulo.fonte(), estimulo.versaoCatalogo(), registro != null, registro == null ? null : registro.observacao(), registro == null ? null : registro.experimentadoEm());
    }
}
