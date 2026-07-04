package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarCriancaUseCase {

    private final CriancaRepositorio criancaRepositorio;

    public CriarCriancaUseCase(CriancaRepositorio criancaRepositorio) {
        this.criancaRepositorio = criancaRepositorio;
    }

    @Transactional
    public Crianca executar(CriarCriancaComando comando) {
        Crianca crianca = Crianca.cadastrar(
                comando.nome(),
                comando.dataNascimento(),
                comando.sexo(),
                comando.prematura(),
                comando.semanasGestacionais(),
                comando.pesoNascimentoGramas()
        );

        return criancaRepositorio.salvar(crianca);
    }
}
