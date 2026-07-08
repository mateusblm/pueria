# Deploy do Pueria

Este repositório está preparado para:

- Front-end Angular no Vercel, usando a pasta `frontend` como root do projeto.
- Back-end Spring Boot no Railway, usando o `Dockerfile` da raiz.
- PostgreSQL no Railway, com migrations pelo Flyway.

## Back-end no Railway

1. Crie um projeto no Railway a partir do repositório GitHub.
2. Escolha a raiz do repositório como serviço do back-end.
3. Adicione um serviço PostgreSQL no mesmo projeto.
4. Configure as variáveis do serviço Spring Boot:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
PUERIA_JWT_SECRET=troque-por-um-segredo-longo-e-aleatorio-com-pelo-menos-32-caracteres
PUERIA_CORS_ALLOWED_ORIGINS=https://seu-front.vercel.app
PUERIA_HIBERNATE_FORMAT_SQL=false
```

O Railway define `PORT` automaticamente. A aplicação usa `/actuator/health` como healthcheck.

Depois do deploy, gere o domínio público do serviço no Railway. Ele será usado no Vercel como `PUERIA_API_URL`.

## Front-end no Vercel

1. Crie um projeto no Vercel a partir do mesmo repositório.
2. Configure o Root Directory como `frontend`.
3. O arquivo `frontend/vercel.json` já define:
   - build: `npm run build`
   - install: `npm ci`
   - output: `dist/frontend/browser`
   - fallback de rotas para SPA Angular
4. Configure a variável de ambiente:

```env
PUERIA_API_URL=https://seu-backend.up.railway.app
```

Não inclua `/api` no final. O front já chama `/api/...` e prefixa a URL do Railway automaticamente.

## Ordem recomendada

1. Deploy do back-end no Railway.
2. Criar domínio público no Railway.
3. Copiar a URL para `PUERIA_API_URL` no Vercel.
4. Deploy do front-end no Vercel.
5. Atualizar `PUERIA_CORS_ALLOWED_ORIGINS` no Railway com o domínio final do Vercel.
6. Redeploy do back-end.

## Validação rápida

Back-end:

```bash
curl https://seu-backend.up.railway.app/actuator/health
curl https://seu-backend.up.railway.app/api/status
```

Front-end:

```bash
curl https://seu-front.vercel.app/runtime-config.js
```

O arquivo deve conter a URL do Railway em `apiUrl`.
