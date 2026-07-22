import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { Usuario } from '../../../shared/models/usuario.model';

@Component({
  selector: 'app-minha-conta',
  imports: [RouterLink, AppIconComponent],
  templateUrl: './minha-conta.component.html',
  styleUrl: './minha-conta.component.scss'
})
export class MinhaContaComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  readonly usuario = signal<Usuario | null>(null);

  ngOnInit(): void {
    this.authService.usuarioAtual().pipe(catchError(() => of(null))).subscribe((usuario) => this.usuario.set(usuario));
  }

  iniciais(nome: string): string {
    return nome.trim().split(/\s+/).slice(0, 2).map((parte) => parte[0]).join('').toUpperCase() || 'P';
  }

  emBreve(): void {
    this.toast.sucesso('Esta funcionalidade estará disponível em breve.');
  }
}
