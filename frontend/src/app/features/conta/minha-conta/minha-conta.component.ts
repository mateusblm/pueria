import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { catchError, finalize, of } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { Usuario } from '../../../shared/models/usuario.model';
import { PwaInstallService } from '../../../core/pwa/pwa-install.service';
import { Crianca } from '../../../shared/models/crianca.model';
import { Cuidador } from '../../../shared/models/cuidador.model';
import { CriancasService } from '../../criancas/criancas.service';
import { CuidadoresService } from '../../criancas/cuidadores.service';

@Component({
  selector: 'app-minha-conta',
  imports: [RouterLink, AppIconComponent, ReactiveFormsModule],
  templateUrl: './minha-conta.component.html',
  styleUrl: './minha-conta.component.scss'
})
export class MinhaContaComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly cuidadoresService = inject(CuidadoresService);
  readonly pwaInstall = inject(PwaInstallService);

  readonly usuario = signal<Usuario | null>(null);
  readonly crianca = signal<Crianca | null>(null);
  readonly cuidadores = signal<Cuidador[]>([]);
  readonly modal = signal<'email' | 'senha' | 'cuidador' | null>(null);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly emailForm = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]], senhaAtual: ['', Validators.required] });
  readonly senhaForm = this.fb.nonNullable.group({ senhaAtual: ['', Validators.required], novaSenha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]], confirmarSenha: ['', Validators.required] });
  readonly cuidadorForm = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]] });

  ngOnInit(): void {
    this.authService.usuarioAtual().pipe(catchError(() => of(null))).subscribe((usuario) => this.usuario.set(usuario));
    this.criancasService.listar().pipe(catchError(() => of([]))).subscribe((criancas) => {
      const emFoco = localStorage.getItem('pueria.criancaEmFocoId');
      const crianca = criancas.find((item) => item.id === emFoco) ?? criancas[0] ?? null;
      this.crianca.set(crianca);
      if (crianca) this.carregarCuidadores(crianca.id);
    });
  }

  iniciais(nome: string): string {
    return nome.trim().split(/\s+/).slice(0, 2).map((parte) => parte[0]).join('').toUpperCase() || 'P';
  }

  souResponsavelPrincipal(): boolean {
    const email = this.usuario()?.email;
    return !!email && this.cuidadores().some((cuidador) => cuidador.principal && cuidador.email === email);
  }

  emBreve(): void {
    this.toast.sucesso('Esta funcionalidade estará disponível em breve.');
  }
  sairDaConta(): void { this.authService.sair(); void this.router.navigateByUrl('/login'); }
  async instalarApp(): Promise<void> { if (await this.pwaInstall.instalar()) this.toast.sucesso('Pueria instalado neste dispositivo.'); else this.toast.sucesso(this.pwaInstall.instrucaoManual()); }

  abrirModal(tipo: 'email' | 'senha' | 'cuidador'): void { this.erro.set(''); this.modal.set(tipo); if (tipo === 'email') this.emailForm.patchValue({ email: this.usuario()?.email ?? '' }); if (tipo === 'cuidador') this.cuidadorForm.reset(); }
  fecharModal(): void { if (!this.salvando()) this.modal.set(null); }
  salvarEmail(): void { if (this.emailForm.invalid) { this.emailForm.markAllAsTouched(); return; } const v = this.emailForm.getRawValue(); this.enviar(this.authService.atualizarEmail(v.email, v.senhaAtual), 'E-mail atualizado. Entre novamente com seu novo e-mail.'); }
  salvarSenha(): void { const v = this.senhaForm.getRawValue(); if (this.senhaForm.invalid || v.novaSenha !== v.confirmarSenha) { this.erro.set(v.novaSenha !== v.confirmarSenha ? 'As novas senhas não coincidem.' : 'Revise os campos obrigatórios.'); return; } this.enviar(this.authService.atualizarSenha(v.senhaAtual, v.novaSenha), 'Senha atualizada. Entre novamente para continuar.'); }
  convidarCuidador(): void { const crianca = this.crianca(); if (!crianca) { this.erro.set('Selecione uma criança para convidar um cuidador.'); return; } if (this.cuidadorForm.invalid) { this.cuidadorForm.markAllAsTouched(); return; } this.erro.set(''); this.salvando.set(true); this.cuidadoresService.convidar(crianca.id, { email: this.cuidadorForm.getRawValue().email, parentesco: 'OUTRO' }).pipe(finalize(() => this.salvando.set(false))).subscribe({ next: (cuidador) => { this.cuidadores.update((itens) => [...itens, cuidador]); this.modal.set(null); this.toast.sucesso(`${cuidador.nome} agora acompanha ${crianca.nome}.`); }, error: (e: { error?: { mensagem?: string } }) => this.erro.set(e.error?.mensagem ?? 'Não foi possível convidar agora.') }); }
  removerCuidador(cuidador: Cuidador): void { const crianca = this.crianca(); if (!crianca || cuidador.principal || !confirm(`Remover ${cuidador.nome} de quem acompanha ${crianca.nome}?`)) return; this.cuidadoresService.remover(crianca.id, cuidador.id).subscribe({ next: () => { this.cuidadores.update((itens) => itens.filter((item) => item.id !== cuidador.id)); this.toast.sucesso('Cuidador removido.'); }, error: () => this.toast.erro('Não foi possível remover este cuidador.') }); }
  private carregarCuidadores(criancaId: string): void { this.cuidadoresService.listar(criancaId).pipe(catchError(() => of([]))).subscribe((itens) => this.cuidadores.set(itens)); }
  private enviar(operacao: ReturnType<AuthService['atualizarEmail']>, mensagem: string): void { this.erro.set(''); this.salvando.set(true); operacao.pipe(finalize(() => this.salvando.set(false))).subscribe({ next: () => { this.authService.sair(); this.toast.sucesso(mensagem); void this.router.navigateByUrl('/login'); }, error: (e: { error?: { mensagem?: string } }) => this.erro.set(e.error?.mensagem ?? 'Não foi possível salvar agora.') }); }
}
