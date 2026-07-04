import { Component, HostListener, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { BrandMarkComponent } from '../../shared/components/brand-mark/brand-mark.component';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, BrandMarkComponent],
  templateUrl: './app-layout.component.html',
  styleUrl: './app-layout.component.scss'
})
export class AppLayoutComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  menuAberto = false;

  alternarMenu(): void {
    this.menuAberto = !this.menuAberto;
  }

  fecharMenu(): void {
    this.menuAberto = false;
  }

  fecharMenuSeMobile(): void {
    if (window.innerWidth < 980) {
      this.fecharMenu();
    }
  }

  @HostListener('window:keydown.escape')
  aoPressionarEscape(): void {
    this.fecharMenu();
  }

  @HostListener('window:resize')
  aoRedimensionar(): void {
    if (window.innerWidth >= 980) {
      this.fecharMenu();
    }
  }

  sair(): void {
    this.authService.sair();
    this.fecharMenu();
    void this.router.navigateByUrl('/login');
  }
}
