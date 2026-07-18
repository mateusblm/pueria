# Sessões com refresh token

O Pueria usa um access token JWT curto, mantido apenas na memória do navegador, e um refresh token opaco em cookie `HttpOnly`. O valor persistido no banco é somente o hash SHA-256 do refresh token.

Cada renovação revoga o token anterior e emite outro. Logout e redefinição de senha revogam as sessões ativas. Em produção, configure:

```text
PUERIA_JWT_EXPIRACAO_SEGUNDOS=900
PUERIA_REFRESH_TOKEN_EXPIRACAO_DIAS=30
PUERIA_REFRESH_TOKEN_COOKIE_SECURE=true
PUERIA_CORS_ALLOWED_ORIGINS=https://pueria.vercel.app
```

Como frontend e API estão em domínios diferentes, o cookie usa `SameSite=None; Secure` e o CORS permite credenciais apenas para as origens configuradas. Para compatibilidade máxima com navegadores que bloqueiam cookies de terceiros, a evolução recomendada é publicar a API em um subdomínio próprio do Pueria, como `api.pueria.com.br`.
