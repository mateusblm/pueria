import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { AlimentacaoService } from '../../alimentacao/alimentacao.service';
import { CrescimentoService } from '../../crescimento/crescimento.service';
import { DesenvolvimentoService } from '../../desenvolvimento/desenvolvimento.service';
import { SaudeService } from '../../saude/saude.service';
import { SonoService } from '../../sono/sono.service';
import { TelasService } from '../../telas/telas.service';
import { TransitoIntestinalService } from '../../transito-intestinal/transito-intestinal.service';
import { AppIconComponent, AppIconName } from '../../../shared/components/app-icon/app-icon.component';

interface SecaoDocumento {
  nome: string;
  icone: AppIconName;
  tom: 'verde' | 'roxo' | 'lilas' | 'amarelo' | 'terracota' | 'azul' | 'rosa';
  quantidade: number;
  unidade: string;
}

@Component({
  selector: 'app-relatorios-crianca',
  imports: [AppIconComponent, RouterLink],
  templateUrl: './relatorios-crianca.component.html',
  styleUrl: './relatorios-crianca.component.scss'
})
export class RelatoriosCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly desenvolvimento = inject(DesenvolvimentoService);
  private readonly crescimento = inject(CrescimentoService);
  private readonly sono = inject(SonoService);
  private readonly alimentacao = inject(AlimentacaoService);
  private readonly transito = inject(TransitoIntestinalService);
  private readonly telas = inject(TelasService);
  private readonly saude = inject(SaudeService);

  readonly criancaId = this.route.snapshot.paramMap.get('id') ?? '';
  readonly gerando = signal(false);
  readonly erro = signal('');
  readonly carregando = signal(true);
  readonly secoes = signal<SecaoDocumento[]>(this.criarSecoes());

  ngOnInit(): void {
    forkJoin({
      crescimento: this.crescimento.listar(this.criancaId).pipe(catchError(() => of([]))),
      marcos: this.desenvolvimento.listarMarcos(this.criancaId).pipe(catchError(() => of([]))),
      sono: this.sono.listar(this.criancaId).pipe(catchError(() => of([]))),
      alimentacao: this.alimentacao.listar(this.criancaId).pipe(catchError(() => of([]))),
      eliminacoes: this.transito.listar(this.criancaId).pipe(catchError(() => of([]))),
      telas: this.telas.listar(this.criancaId).pipe(catchError(() => of([]))),
      saude: this.saude.listar(this.criancaId).pipe(catchError(() => of([])))
    }).subscribe(({ crescimento, marcos, sono, alimentacao, eliminacoes, telas, saude }) => {
      this.secoes.set([
        { nome: 'Crescimento', icone: 'chart', tom: 'verde', quantidade: crescimento.length, unidade: 'medida' },
        { nome: 'Neurodesenvolvimento', icone: 'brain', tom: 'roxo', quantidade: marcos.filter((marco) => marco.status !== 'NAO_AVALIADO').length, unidade: 'marco respondido' },
        { nome: 'Sono', icone: 'moon', tom: 'lilas', quantidade: sono.length, unidade: 'registro' },
        { nome: 'Alimentação', icone: 'salad', tom: 'amarelo', quantidade: alimentacao.length, unidade: 'registro' },
        { nome: 'Eliminações', icone: 'toilet', tom: 'terracota', quantidade: eliminacoes.length, unidade: 'registro' },
        { nome: 'Telas', icone: 'smartphone', tom: 'azul', quantidade: telas.length, unidade: 'registro' },
        { nome: 'Saúde', icone: 'heartPulse', tom: 'rosa', quantidade: saude.length, unidade: 'registro' }
      ]);
      this.carregando.set(false);
    });
  }

  secoesComRegistros(): number {
    return this.secoes().filter((secao) => secao.quantidade > 0).length;
  }

  detalhe(secao: SecaoDocumento): string {
    if (!secao.quantidade) return 'Sem registros';
    const unidade = secao.quantidade === 1 ? secao.unidade : `${secao.unidade}s`;
    return `${secao.quantidade} ${unidade}`;
  }

  gerar(detalhado = false): void {
    this.gerando.set(true);
    this.desenvolvimento.gerarResumoConsulta(this.criancaId, detalhado).subscribe({
      next: (pdf) => {
        this.gerando.set(false);
        window.open(URL.createObjectURL(pdf), '_blank');
      },
      error: () => {
        this.gerando.set(false);
        this.erro.set('Não foi possível gerar o documento agora.');
      }
    });
  }

  private criarSecoes(): SecaoDocumento[] {
    return [
      { nome: 'Crescimento', icone: 'chart', tom: 'verde', quantidade: 0, unidade: 'medida' },
      { nome: 'Neurodesenvolvimento', icone: 'brain', tom: 'roxo', quantidade: 0, unidade: 'marco respondido' },
      { nome: 'Sono', icone: 'moon', tom: 'lilas', quantidade: 0, unidade: 'registro' },
      { nome: 'Alimentação', icone: 'salad', tom: 'amarelo', quantidade: 0, unidade: 'registro' },
      { nome: 'Eliminações', icone: 'toilet', tom: 'terracota', quantidade: 0, unidade: 'registro' },
      { nome: 'Telas', icone: 'smartphone', tom: 'azul', quantidade: 0, unidade: 'registro' },
      { nome: 'Saúde', icone: 'heartPulse', tom: 'rosa', quantidade: 0, unidade: 'registro' }
    ];
  }
}
