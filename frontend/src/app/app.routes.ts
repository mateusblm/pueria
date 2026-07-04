import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicOnlyGuard } from './core/guards/public-only.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'criancas' },
  {
    path: '',
    canActivate: [publicOnlyGuard],
    loadComponent: () => import('./layout/auth-layout/auth-layout.component').then((m) => m.AuthLayoutComponent),
    children: [
      { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent) },
      { path: 'cadastro', loadComponent: () => import('./features/auth/cadastro/cadastro.component').then((m) => m.CadastroComponent) }
    ]
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/app-layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      { path: 'criancas', loadComponent: () => import('./features/criancas/minhas-criancas/minhas-criancas.component').then((m) => m.MinhasCriancasComponent) },
      { path: 'criancas/nova', loadComponent: () => import('./features/criancas/nova-crianca/nova-crianca.component').then((m) => m.NovaCriancaComponent) },
      { path: 'criancas/:id/editar', loadComponent: () => import('./features/criancas/editar-crianca/editar-crianca.component').then((m) => m.EditarCriancaComponent) },
      { path: 'criancas/:id/desenvolvimento', loadComponent: () => import('./features/desenvolvimento/marcos-crianca/marcos-crianca.component').then((m) => m.MarcosCriancaComponent) },
      { path: 'criancas/:id', loadComponent: () => import('./features/criancas/detalhe-crianca/detalhe-crianca.component').then((m) => m.DetalheCriancaComponent) },
      { path: 'app/criancas', pathMatch: 'full', redirectTo: 'criancas' },
      { path: 'app/criancas/nova', pathMatch: 'full', redirectTo: 'criancas/nova' },
      { path: 'app/criancas/:id/editar', redirectTo: 'criancas/:id/editar' },
      { path: 'app/criancas/:id/desenvolvimento', redirectTo: 'criancas/:id/desenvolvimento' },
      { path: 'app/criancas/:id', redirectTo: 'criancas/:id' },
      { path: 'minhas-criancas', pathMatch: 'full', redirectTo: 'criancas' },
      { path: 'minhas-criancas/nova', pathMatch: 'full', redirectTo: 'criancas/nova' },
      { path: 'minhas-criancas/:id/editar', redirectTo: 'criancas/:id/editar' },
      { path: 'minhas-criancas/:id/desenvolvimento', redirectTo: 'criancas/:id/desenvolvimento' },
      { path: 'minhas-criancas/:id', redirectTo: 'criancas/:id' }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
