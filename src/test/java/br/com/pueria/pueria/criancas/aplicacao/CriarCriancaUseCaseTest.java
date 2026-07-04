package br.com.pueria.pueria.criancas.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.consentimentos.dominio.Consentimento;
import br.com.pueria.pueria.consentimentos.dominio.ConsentimentoRepositorio;
import br.com.pueria.pueria.consentimentos.dominio.TipoConsentimento;
import br.com.pueria.pueria.criancas.dominio.Crianca;
import br.com.pueria.pueria.criancas.dominio.CriancaRepositorio;
import br.com.pueria.pueria.criancas.dominio.Sexo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCrianca;
import br.com.pueria.pueria.responsaveis.dominio.VinculoResponsavelCriancaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriarCriancaUseCaseTest {

    @Test
    void deveCriarCriancaComResponsavelEConsentimento() {
        CriancaRepositorioEmMemoria criancas = new CriancaRepositorioEmMemoria();
        UsuarioRepositorioEmMemoria usuarios = new UsuarioRepositorioEmMemoria();
        VinculoRepositorioEmMemoria vinculos = new VinculoRepositorioEmMemoria();
        ConsentimentoRepositorioEmMemoria consentimentos = new ConsentimentoRepositorioEmMemoria();

        Usuario responsavel = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "senha-criptografada");
        usuarios.salvar(responsavel);

        CriarCriancaUseCase useCase = new CriarCriancaUseCase(criancas, usuarios, vinculos, consentimentos);

        Crianca crianca = useCase.executar(new CriarCriancaComando(
                "mateus@email.com",
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200,
                Parentesco.PAI,
                true,
                "2026.07"
        ));

        assertNotNull(crianca.getId());
        assertEquals("Ana", crianca.getNome());
        assertEquals(1, criancas.quantidade());
        assertTrue(vinculos.usuarioPodeAcessarCrianca(responsavel.getId(), crianca.getId()));
        assertTrue(consentimentos.existeAceite(
                responsavel.getId(),
                crianca.getId(),
                TipoConsentimento.ACOMPANHAMENTO_DESENVOLVIMENTO_INFANTIL
        ));
    }

    @Test
    void naoDeveCriarCriancaSemConsentimentoAceito() {
        CriancaRepositorioEmMemoria criancas = new CriancaRepositorioEmMemoria();
        UsuarioRepositorioEmMemoria usuarios = new UsuarioRepositorioEmMemoria();
        VinculoRepositorioEmMemoria vinculos = new VinculoRepositorioEmMemoria();
        ConsentimentoRepositorioEmMemoria consentimentos = new ConsentimentoRepositorioEmMemoria();

        Usuario responsavel = Usuario.cadastrarResponsavel("Mateus", "mateus@email.com", "senha-criptografada");
        usuarios.salvar(responsavel);

        CriarCriancaUseCase useCase = new CriarCriancaUseCase(criancas, usuarios, vinculos, consentimentos);

        assertThrows(RegraDominioException.class, () -> useCase.executar(new CriarCriancaComando(
                "mateus@email.com",
                "Ana",
                LocalDate.of(2024, 1, 10),
                Sexo.FEMININO,
                false,
                39,
                3200,
                Parentesco.PAI,
                false,
                "2026.07"
        )));
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

    private static class UsuarioRepositorioEmMemoria implements UsuarioRepositorio {
        private final Map<UUID, Usuario> usuariosPorId = new HashMap<>();
        private final Map<String, Usuario> usuariosPorEmail = new HashMap<>();

        @Override
        public Usuario salvar(Usuario usuario) {
            usuariosPorId.put(usuario.getId(), usuario);
            usuariosPorEmail.put(usuario.getEmail(), usuario);
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return Optional.ofNullable(usuariosPorId.get(id));
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.ofNullable(usuariosPorEmail.get(email));
        }

        @Override
        public boolean existePorEmail(String email) {
            return usuariosPorEmail.containsKey(email);
        }
    }

    private static class VinculoRepositorioEmMemoria implements VinculoResponsavelCriancaRepositorio {
        private final Map<UUID, VinculoResponsavelCrianca> vinculos = new HashMap<>();

        @Override
        public VinculoResponsavelCrianca salvar(VinculoResponsavelCrianca vinculo) {
            vinculos.put(vinculo.getId(), vinculo);
            return vinculo;
        }

        @Override
        public Optional<VinculoResponsavelCrianca> buscarPorId(UUID id) {
            return Optional.ofNullable(vinculos.get(id));
        }

        @Override
        public boolean existeResponsavelPrincipal(UUID criancaId) {
            return vinculos.values().stream()
                    .anyMatch(vinculo -> vinculo.getCriancaId().equals(criancaId) && vinculo.isPrincipal());
        }

        @Override
        public boolean usuarioPodeAcessarCrianca(UUID usuarioId, UUID criancaId) {
            return vinculos.values().stream()
                    .anyMatch(vinculo -> vinculo.getUsuarioId().equals(usuarioId) && vinculo.getCriancaId().equals(criancaId));
        }
    }

    private static class ConsentimentoRepositorioEmMemoria implements ConsentimentoRepositorio {
        private final Map<UUID, Consentimento> consentimentos = new HashMap<>();

        @Override
        public Consentimento salvar(Consentimento consentimento) {
            consentimentos.put(consentimento.getId(), consentimento);
            return consentimento;
        }

        @Override
        public Optional<Consentimento> buscarPorId(UUID id) {
            return Optional.ofNullable(consentimentos.get(id));
        }

        @Override
        public boolean existeAceite(UUID usuarioId, UUID criancaId, TipoConsentimento tipo) {
            return consentimentos.values().stream()
                    .anyMatch(consentimento -> consentimento.getUsuarioId().equals(usuarioId)
                            && consentimento.getCriancaId().equals(criancaId)
                            && consentimento.getTipo() == tipo
                            && consentimento.isAceito());
        }
    }
}
