package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;
import br.com.pueria.pueria.desenvolvimento.dominio.*;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository public class EstimuloDesenvolvimentoRepositorioJpa implements EstimuloDesenvolvimentoRepositorio {
    private final EstimuloDesenvolvimentoJpaRepository repository;
    public EstimuloDesenvolvimentoRepositorioJpa(EstimuloDesenvolvimentoJpaRepository repository){this.repository=repository;}
    public List<EstimuloDesenvolvimento> listarAtivosParaIdade(int idade){return repository.findByAtivoTrueAndIdadeInicialMesesLessThanEqualAndIdadeFinalMesesGreaterThanEqualOrderByAreaAsc(idade, idade).stream().map(this::paraDominio).toList();}
    public Optional<EstimuloDesenvolvimento> buscarAtivoParaMarco(UUID marcoId){return repository.findAtivoByMarcoId(marcoId).map(this::paraDominio);}
    public Optional<EstimuloDesenvolvimento> buscarPorId(UUID id){return repository.findById(id).map(this::paraDominio);}
    private EstimuloDesenvolvimento paraDominio(EstimuloDesenvolvimentoJpaEntidade e){return new EstimuloDesenvolvimento(e.getId(),e.getIdadeInicialMeses(),e.getIdadeFinalMeses(),e.getArea(),e.getTitulo(),e.getDescricao(),e.getCuidado(),e.getFonte(),e.getVersaoCatalogo(),e.isAtivo());}
}
