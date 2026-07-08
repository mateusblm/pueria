import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

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
  const token = authService.token;
  const requestComApi = request.clone({
    url: aplicarApiUrl(request.url)
  });

  if (!token) {
    return next(requestComApi);
  }

  return next(
    requestComApi.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  );
};

function aplicarApiUrl(url: string): string {
  const apiUrl = window.__PUERIA_CONFIG__?.apiUrl?.replace(/\/+$/, '') ?? '';
  const devePrefixar = apiUrl && (url.startsWith('/api') || url.startsWith('/actuator'));
  return devePrefixar ? `${apiUrl}${url}` : url;
}
