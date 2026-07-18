import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { BrandMarkComponent } from '../../shared/components/brand-mark/brand-mark.component';
import { AppIconComponent } from '../../shared/components/app-icon/app-icon.component';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, BrandMarkComponent, AppIconComponent],
  templateUrl: './app-layout.component.html',
  styleUrl: './app-layout.component.scss'
})
export class AppLayoutComponent {
  menuAberto = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  abrirMenu(): void {
    this.menuAberto = true;
  }

  fecharMenu(): void {
    this.menuAberto = false;
  }

  sair(): void {
    this.authService.sair();
    void this.router.navigateByUrl('/login');
  }

  irParaConsulta(): void {
    this.fecharMenu();
    const criancaId = localStorage.getItem('pueria.criancaEmFocoId');
    void this.router.navigateByUrl(criancaId ? `/criancas/${criancaId}/para-a-consulta` : '/criancas');
  }

  consultaEstaAtiva(): boolean {
    return this.router.url.includes('/para-a-consulta');
  }
}
