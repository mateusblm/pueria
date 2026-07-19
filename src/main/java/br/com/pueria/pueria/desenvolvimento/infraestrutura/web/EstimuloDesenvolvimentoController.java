package br.com.pueria.pueria.desenvolvimento.infraestrutura.web;
import br.com.pueria.pueria.desenvolvimento.aplicacao.GerenciarEstimulosDesenvolvimentoUseCase;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/criancas/{criancaId}/desenvolvimento/estimulos") public class EstimuloDesenvolvimentoController {
 private final GerenciarEstimulosDesenvolvimentoUseCase useCase; public EstimuloDesenvolvimentoController(GerenciarEstimulosDesenvolvimentoUseCase useCase){this.useCase=useCase;}
 @GetMapping public List<EstimuloDesenvolvimentoResponse> listar(@PathVariable UUID criancaId,Authentication auth){return useCase.listar(criancaId,auth.getName()).stream().map(EstimuloDesenvolvimentoResponse::de).toList();}
 @GetMapping("/recomendacoes") public List<EstimuloDesenvolvimentoResponse> recomendacoes(@PathVariable UUID criancaId,@RequestParam(required=false) Integer idadeMeses,Authentication auth){return useCase.listarRecomendacoes(criancaId,auth.getName(),idadeMeses).stream().map(EstimuloDesenvolvimentoResponse::de).toList();}
 @GetMapping("/marcos/{marcoId}") public EstimuloDesenvolvimentoResponse buscarParaMarco(@PathVariable UUID criancaId,@PathVariable UUID marcoId,Authentication auth){return EstimuloDesenvolvimentoResponse.de(useCase.buscarParaMarco(criancaId,marcoId,auth.getName()));}
 @GetMapping("/historico") public List<EstimuloDesenvolvimentoResponse> historico(@PathVariable UUID criancaId,Authentication auth){return useCase.listarHistorico(criancaId,auth.getName()).stream().map(EstimuloDesenvolvimentoResponse::de).toList();}
 @PutMapping("/{estimuloId}") public void registrar(@PathVariable UUID criancaId,@PathVariable UUID estimuloId,@Valid @RequestBody RegistrarEstimuloDesenvolvimentoRequest request,Authentication auth){useCase.registrar(criancaId,estimuloId,request.observacao(),auth.getName());}
}
