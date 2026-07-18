# Autorização por objeto

Toda rota iniciada por `/api/criancas/{criancaId}` passa pela guarda `AutorizacaoCriancaInterceptor`.
Ela resolve o usuário autenticado e só permite a requisição quando existe vínculo ativo entre o usuário e a criança.

Para reduzir enumeração de registros, a ausência de vínculo retorna `404 Criança não encontrada`, igual a um identificador inexistente. Os casos de uso preservam sua própria validação de vínculo para manter a proteção quando forem chamados fora da camada HTTP.

Ao criar uma rota de recurso pertencente à criança, ela deve permanecer abaixo de `/api/criancas/{criancaId}`. Endpoints futuros para profissionais, compartilhamentos e administração precisam de uma política explícita de papel e vínculo antes de serem disponibilizados.
