package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RecursoNaoEncontradoException;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BuscarCriancaUseCase {

    private final CriancaRepositorio criancaRepositorio;

    public BuscarCriancaUseCase(CriancaRepositorio criancaRepositorio) {
        this.criancaRepositorio = criancaRepositorio;
    }

    @Transactional(readOnly = true)
    public Crianca executar(UUID id) {
        return criancaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Criança não encontrada."));
    }
}
