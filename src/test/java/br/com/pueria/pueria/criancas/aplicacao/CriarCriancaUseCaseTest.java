package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CriarCriancaUseCaseTest {

    @Test
    void deveCriarESalvarCrianca() {
        CriancaRepositorioEmMemoria repositorio = new CriancaRepositorioEmMemoria();
        CriarCriancaUseCase useCase = new CriarCriancaUseCase(repositorio);

        Crianca crianca = useCase.executar(new CriarCriancaComando(
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200
        ));

        assertNotNull(crianca.getId());
        assertEquals("Ana", crianca.getNome());
        assertEquals(1, repositorio.quantidade());
    }

    private static class CriancaRepositorioEmMemoria implements CriancaRepositorio {

        private final Map<UUID, Crianca> criancas = new HashMap<>();

        @Override
        public Crianca salvar(Crianca crianca) {
            criancas.put(crianca.getId(), crianca);
            return crianca;
        }

        @Override
        public Optional<Crianca> buscarPorId(UUID id) {
            return Optional.ofNullable(criancas.get(id));
        }

        int quantidade() {
            return criancas.size();
        }
    }
}
