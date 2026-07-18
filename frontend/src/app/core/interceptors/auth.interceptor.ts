import { HttpContextToken, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthService } from '../auth/auth.service';

declare global {
  interface Window {
    __PUERIA_CONFIG__?: {
      apiUrl?: string;
    };
  }
}

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const requestComApi = request.clone({
    url: aplicarApiUrl(request.url)
  });
  const requisicaoAutenticada = adicionarToken(requestComApi, authService.token);

  return next(requisicaoAutenticada).pipe(
    catchError((erro: unknown) => {
      if (deveRenovar(erro, requestComApi)) {
        return authService.renovarSessao().pipe(
          switchMap(() => next(adicionarToken(
            requestComApi.clone({ context: requestComApi.context.set(JA_TENTOU_RENOVAR, true) }),
            authService.token
          ))),
          catchError((erroRenovacao: unknown) => {
            encerrarSessao(authService, router);
            return throwError(() => erroRenovacao);
          })
        );
      }

      if (erro instanceof HttpErrorResponse && erro.status === 401 && !ehRotaAutenticacao(requestComApi.url)) {
        encerrarSessao(authService, router);
      }

      return throwError(() => erro);
    })
  );
};

const JA_TENTOU_RENOVAR = new HttpContextToken<boolean>(() => false);

function adicionarToken(request: Parameters<HttpInterceptorFn>[0], token: string | null) {
  if (!token || ehRotaAutenticacao(request.url)) {
    return request;
  }

  return request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}

function deveRenovar(erro: unknown, request: Parameters<HttpInterceptorFn>[0]): boolean {
  return erro instanceof HttpErrorResponse
    && erro.status === 401
    && !ehRotaAutenticacao(request.url)
    && !request.context.get(JA_TENTOU_RENOVAR);
}

function ehRotaAutenticacao(url: string): boolean {
  return url.includes('/api/auth/');
}

function encerrarSessao(authService: AuthService, router: Router): void {
  const retorno = router.url.startsWith('/') ? router.url : '/acompanhamento';
  authService.limparSessaoLocal();
  void router.navigate(['/login'], { queryParams: { sessao: 'expirada', retorno } });
}

function aplicarApiUrl(url: string): string {
  const apiUrl = window.__PUERIA_CONFIG__?.apiUrl?.replace(/\/+$/, '') ?? '';
  const devePrefixar = apiUrl && (url.startsWith('/api') || url.startsWith('/actuator'));
  return devePrefixar ? `${apiUrl}${url}` : url;
}
