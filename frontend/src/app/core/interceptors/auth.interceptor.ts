import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

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
  const token = authService.token;
  const requestComApi = request.clone({
    url: aplicarApiUrl(request.url)
  });

  if (!token) {
    return next(requestComApi);
  }

  const requisicaoAutenticada = requestComApi.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(requisicaoAutenticada).pipe(
    catchError((erro: unknown) => {
      if (erro instanceof HttpErrorResponse && erro.status === 401 && !request.url.includes('/api/auth/')) {
        const retorno = router.url.startsWith('/') ? router.url : '/acompanhamento';
        authService.sair();
        void router.navigate(['/login'], { queryParams: { sessao: 'expirada', retorno } });
      }
      return throwError(() => erro);
    })
  );
};

function aplicarApiUrl(url: string): string {
  const apiUrl = window.__PUERIA_CONFIG__?.apiUrl?.replace(/\/+$/, '') ?? '';
  const devePrefixar = apiUrl && (url.startsWith('/api') || url.startsWith('/actuator'));
  return devePrefixar ? `${apiUrl}${url}` : url;
}
