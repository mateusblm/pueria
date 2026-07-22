import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicOnlyGuard } from './core/guards/public-only.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./features/inicio/pagina-inicial/pagina-inicial.component').then((m) => m.PaginaInicialComponent) },
  { path: 'privacidade', loadComponent: () => import('./features/privacidade/privacidade.component').then((m) => m.PrivacidadeComponent) },
  {
    path: '',
    canActivate: [publicOnlyGuard],
    loadComponent: () => import('./layout/auth-layout/auth-layout.component').then((m) => m.AuthLayoutComponent),
    children: [
      { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent) },
      { path: 'cadastro', loadComponent: () => import('./features/auth/cadastro/cadastro.component').then((m) => m.CadastroComponent) },
      { path: 'recuperar-senha', loadComponent: () => import('./features/auth/recuperar-senha/recuperar-senha.component').then((m) => m.RecuperarSenhaComponent) },
      { path: 'redefinir-senha', loadComponent: () => import('./features/auth/redefinir-senha/redefinir-senha.component').then((m) => m.RedefinirSenhaComponent) }
    ]
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/app-layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      { path: 'minha-conta', loadComponent: () => import('./features/conta/minha-conta/minha-conta.component').then((m) => m.MinhaContaComponent) },
      { path: 'acompanhamento', loadComponent: () => import('./features/acompanhamento/acompanhamento.component').then((m) => m.AcompanhamentoComponent) },
      { path: 'criancas', loadComponent: () => import('./features/criancas/minhas-criancas/minhas-criancas.component').then((m) => m.MinhasCriancasComponent) },
      { path: 'criancas/nova', loadComponent: () => import('./features/criancas/nova-crianca/nova-crianca.component').then((m) => m.NovaCriancaComponent) },
      { path: 'criancas/:id/editar', loadComponent: () => import('./features/criancas/editar-crianca/editar-crianca.component').then((m) => m.EditarCriancaComponent) },
      { path: 'criancas/:id/desenvolvimento', loadComponent: () => import('./features/desenvolvimento/marcos-crianca/marcos-crianca.component').then((m) => m.MarcosCriancaComponent) },
      { path: 'criancas/:id/observacoes', loadComponent: () => import('./features/acompanhamento/observacoes-crianca/observacoes-crianca.component').then((m) => m.ObservacoesCriancaComponent) },
      { path: 'criancas/:id/crescimento', loadComponent: () => import('./features/crescimento/crescimento-crianca/crescimento-crianca.component').then((m) => m.CrescimentoCriancaComponent) },
      { path: 'criancas/:id/alimentacao', loadComponent: () => import('./features/alimentacao/alimentacao-crianca/alimentacao-crianca.component').then((m) => m.AlimentacaoCriancaComponent) },
      { path: 'criancas/:id/transito-intestinal', loadComponent: () => import('./features/transito-intestinal/transito-intestinal-crianca/transito-intestinal-crianca.component').then((m) => m.TransitoIntestinalCriancaComponent) },
      { path: 'criancas/:id/humor-comportamento', loadComponent: () => import('./features/contexto-familiar/registro-contexto-crianca/registro-contexto-crianca.component').then((m) => m.RegistroContextoCriancaComponent), data: { contexto: 'humor' } },
      { path: 'criancas/:id/observacoes-eventos', loadComponent: () => import('./features/contexto-familiar/registro-contexto-crianca/registro-contexto-crianca.component').then((m) => m.RegistroContextoCriancaComponent), data: { contexto: 'observacoes' } },
      { path: 'criancas/:id/sono', loadComponent: () => import('./features/sono/sono-crianca/sono-crianca.component').then((m) => m.SonoCriancaComponent) },
      { path: 'criancas/:id/telas', loadComponent: () => import('./features/telas/telas-crianca/telas-crianca.component').then((m) => m.TelasCriancaComponent) },
      { path: 'criancas/:id/saude', loadComponent: () => import('./features/saude/saude-crianca/saude-crianca.component').then((m) => m.SaudeCriancaComponent) },
      { path: 'criancas/:id/para-a-consulta', loadComponent: () => import('./features/relatorios/relatorios-crianca/relatorios-crianca.component').then((m) => m.RelatoriosCriancaComponent) },
      { path: 'criancas/:id', loadComponent: () => import('./features/criancas/detalhe-crianca/detalhe-crianca.component').then((m) => m.DetalheCriancaComponent) },
      { path: 'app/criancas', pathMatch: 'full', redirectTo: 'criancas' },
      { path: 'app/criancas/nova', pathMatch: 'full', redirectTo: 'criancas/nova' },
      { path: 'app/criancas/:id/editar', redirectTo: 'criancas/:id/editar' },
      { path: 'app/criancas/:id/desenvolvimento', redirectTo: 'criancas/:id/desenvolvimento' },
      { path: 'app/criancas/:id/observacoes', redirectTo: 'criancas/:id/observacoes' },
      { path: 'app/criancas/:id/crescimento', redirectTo: 'criancas/:id/crescimento' },
      { path: 'app/criancas/:id/alimentacao', redirectTo: 'criancas/:id/alimentacao' },
      { path: 'app/criancas/:id/transito-intestinal', redirectTo: 'criancas/:id/transito-intestinal' },
      { path: 'app/criancas/:id/humor-comportamento', redirectTo: 'criancas/:id/humor-comportamento' },
      { path: 'app/criancas/:id/observacoes-eventos', redirectTo: 'criancas/:id/observacoes-eventos' },
      { path: 'app/criancas/:id/sono', redirectTo: 'criancas/:id/sono' },
      { path: 'app/criancas/:id/telas', redirectTo: 'criancas/:id/telas' },
      { path: 'app/criancas/:id/saude', redirectTo: 'criancas/:id/saude' },
      { path: 'app/criancas/:id', redirectTo: 'criancas/:id' },
      { path: 'app/acompanhamento', pathMatch: 'full', redirectTo: 'acompanhamento' },
      { path: 'desenvolvimento', pathMatch: 'full', redirectTo: 'acompanhamento' },
      { path: 'minhas-criancas', pathMatch: 'full', redirectTo: 'criancas' },
      { path: 'minhas-criancas/nova', pathMatch: 'full', redirectTo: 'criancas/nova' },
      { path: 'minhas-criancas/:id/editar', redirectTo: 'criancas/:id/editar' },
      { path: 'minhas-criancas/:id/desenvolvimento', redirectTo: 'criancas/:id/desenvolvimento' },
      { path: 'minhas-criancas/:id/observacoes', redirectTo: 'criancas/:id/observacoes' },
      { path: 'minhas-criancas/:id/crescimento', redirectTo: 'criancas/:id/crescimento' },
      { path: 'minhas-criancas/:id/alimentacao', redirectTo: 'criancas/:id/alimentacao' },
      { path: 'minhas-criancas/:id/transito-intestinal', redirectTo: 'criancas/:id/transito-intestinal' },
      { path: 'minhas-criancas/:id/humor-comportamento', redirectTo: 'criancas/:id/humor-comportamento' },
      { path: 'minhas-criancas/:id/observacoes-eventos', redirectTo: 'criancas/:id/observacoes-eventos' },
      { path: 'minhas-criancas/:id/sono', redirectTo: 'criancas/:id/sono' },
      { path: 'minhas-criancas/:id/telas', redirectTo: 'criancas/:id/telas' },
      { path: 'minhas-criancas/:id/saude', redirectTo: 'criancas/:id/saude' },
      { path: 'minhas-criancas/:id', redirectTo: 'criancas/:id' }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
