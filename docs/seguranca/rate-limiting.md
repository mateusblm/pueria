# Rate limiting da API

## Politicas ativas

| Rota | Chave | Limite |
| --- | --- | --- |
| `POST /api/auth/login` | IP | 5 por minuto |
| `POST /api/auth/cadastro` e futuro `POST /api/usuarios` | IP | 5 por hora |
| `POST /api/auth/recuperar-senha` | IP e e-mail | 3 por hora |
| `POST /api/auth/redefinir-senha` | IP | 3 por hora |
| Futuro `POST /api/auth/refresh` | sessao (token Bearer) | 30 por minuto |
| Demais rotas autenticadas em `/api/**` | usuario autenticado | 200 por minuto |

As respostas bloqueadas retornam HTTP `429`, um corpo padrao de erro e o cabecalho `Retry-After` em segundos. As respostas permitidas nas rotas controladas retornam `RateLimit-Limit` e `RateLimit-Remaining`.

## Railway

Configure no servico de backend:

```text
PUERIA_RATE_LIMIT_HABILITADO=true
PUERIA_RATE_LIMIT_CONFIAR_CABECALHOS_ENCAMINHADOS=true
```

O segundo parametro so deve ser ativado atras de um proxy confiavel, como o proxy de entrada do Railway, pois ele permite utilizar o IP original encaminhado no `X-Forwarded-For`.

## Escalabilidade

O contador atual fica em memoria no processo, adequado para a instancia unica atual. Antes de aumentar o backend para duas ou mais replicas, migre o armazenamento dos contadores para Redis ou aplique os mesmos limites no gateway/proxy. Caso contrario, cada replica tera sua propria janela de limite.
