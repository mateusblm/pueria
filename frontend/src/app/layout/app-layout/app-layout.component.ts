import { Component, OnInit, computed, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { BrandMarkComponent } from '../../shared/components/brand-mark/brand-mark.component';
import { AppIconComponent } from '../../shared/components/app-icon/app-icon.component';
import { CriancasService } from '../../features/criancas/criancas.service';
import { Crianca } from '../../shared/models/crianca.model';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, BrandMarkComponent, AppIconComponent],
  templateUrl: './app-layout.component.html',
  styleUrl: './app-layout.component.scss'
})
export class AppLayoutComponent implements OnInit {
  menuAberto = false;
  readonly criancas = signal<Crianca[]>([]);
  readonly criancaEmFocoId = signal<string | null>(localStorage.getItem('pueria.criancaEmFocoId'));
  readonly seletorCriancaAberto = signal(false);
  readonly criancaEmFoco = computed(() => {
    const criancas = this.criancas();
    return criancas.find((crianca) => crianca.id === this.criancaEmFocoId()) ?? criancas[0] ?? null;
  });

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly criancasService: CriancasService
  ) {}

  ngOnInit(): void {
    this.carregarCriancas();
    this.sincronizarCriancaDaRota(this.router.url);
    this.router.events
      .pipe(filter((evento): evento is NavigationEnd => evento instanceof NavigationEnd))
      .subscribe((evento) => this.sincronizarCriancaDaRota(evento.urlAfterRedirects));
  }

  abrirMenu(): void {
    this.menuAberto = true;
  }

  fecharMenu(): void {
    this.menuAberto = false;
  }

  alternarSeletorCrianca(): void {
    this.seletorCriancaAberto.update((aberto) => !aberto);
  }

  fecharSeletorCrianca(): void {
    this.seletorCriancaAberto.set(false);
  }

  selecionarCrianca(crianca: Crianca): void {
    this.criancaEmFocoId.set(crianca.id);
    localStorage.setItem('pueria.criancaEmFocoId', crianca.id);
    this.fecharSeletorCrianca();
    this.fecharMenu();
    void this.router.navigateByUrl(this.rotaDaCriancaSelecionada(crianca.id));
  }

  idadeDaCrianca(crianca: Crianca): string {
    const nascimento = new Date(`${crianca.dataNascimento}T00:00:00`);
    const hoje = new Date();
    let anos = hoje.getFullYear() - nascimento.getFullYear();
    let meses = hoje.getMonth() - nascimento.getMonth();
    if (hoje.getDate() < nascimento.getDate()) meses -= 1;
    if (meses < 0) { anos -= 1; meses += 12; }
    return anos <= 0 ? `${meses} ${meses === 1 ? 'mês' : 'meses'}` : `${anos}a ${meses}m`;
  }

  sair(): void {
    this.authService.sair();
    void this.router.navigateByUrl('/login');
  }

  irParaConsulta(): void {
    this.irParaAreaDaCrianca('para-a-consulta');
  }

  irParaMarcos(): void {
    this.irParaAreaDaCrianca('desenvolvimento');
  }

  irParaAcompanhamento(): void {
    this.irParaAreaDaCrianca('observacoes');
  }

  consultaEstaAtiva(): boolean {
    return this.router.url.includes('/para-a-consulta');
  }

  marcosEstaAtivo(): boolean {
    return this.router.url.includes('/desenvolvimento');
  }

  acompanhamentoEstaAtivo(): boolean {
    return this.router.url.includes('/observacoes');
  }

  private irParaAreaDaCrianca(area: string): void {
    this.fecharMenu();
    const criancaId = localStorage.getItem('pueria.criancaEmFocoId');
    void this.router.navigateByUrl(criancaId ? `/criancas/${criancaId}/${area}` : '/criancas');
  }

  private carregarCriancas(): void {
    this.criancasService.listar().subscribe({
      next: (criancas) => {
        this.criancas.set(criancas);
        if (!this.criancaEmFocoId() && criancas.length > 0) {
          this.criancaEmFocoId.set(criancas[0].id);
          localStorage.setItem('pueria.criancaEmFocoId', criancas[0].id);
        }
      },
      error: () => this.criancas.set([])
    });
  }

  private sincronizarCriancaDaRota(url: string): void {
    const caminho = url.split('?')[0];
    const segmentos = caminho.split('/').filter(Boolean);
    if (segmentos[0] !== 'criancas' || segmentos.length < 3 || segmentos[1] === 'nova') return;
    this.criancaEmFocoId.set(segmentos[1]);
    localStorage.setItem('pueria.criancaEmFocoId', segmentos[1]);
  }

  private rotaDaCriancaSelecionada(criancaId: string): string {
    const caminho = this.router.url.split('?')[0];
    const segmentos = caminho.split('/').filter(Boolean);
    return segmentos[0] === 'criancas' && segmentos.length >= 3
      ? `/criancas/${criancaId}/${segmentos.slice(2).join('/')}`
      : `/acompanhamento?crianca=${encodeURIComponent(criancaId)}`;
  }
}
