import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of, take } from 'rxjs';

import { AuthService } from '../auth/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaAutenticado()) {
    return true;
  }

  return authService.renovarSessao().pipe(
    take(1),
    map(() => true),
    catchError(() => {
      authService.limparSessaoLocal();
      return of(router.parseUrl('/login'));
    })
  );
};
