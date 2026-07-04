import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { publicOnlyGuard } from './core/guards/public-only.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  {
    path: '',
    canActivate: [publicOnlyGuard],
    loadComponent: () => import('./layout/auth-layout/auth-layout.component').then((m) => m.AuthLayoutComponent),
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent)
      },
      {
        path: 'cadastro',
        loadComponent: () => import('./features/auth/cadastro/cadastro.component').then((m) => m.CadastroComponent)
      }
    ]
  },
  {
    path: 'app',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/app-layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'criancas'
      },
      {
        path: 'criancas',
        loadComponent: () => import('./features/criancas/minhas-criancas/minhas-criancas.component').then((m) => m.MinhasCriancasComponent)
      },
      {
        path: 'criancas/nova',
        loadComponent: () => import('./features/criancas/nova-crianca/nova-crianca.component').then((m) => m.NovaCriancaComponent)
      },
      {
        path: 'criancas/:id',
        loadComponent: () => import('./features/criancas/detalhe-crianca/detalhe-crianca.component').then((m) => m.DetalheCriancaComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
