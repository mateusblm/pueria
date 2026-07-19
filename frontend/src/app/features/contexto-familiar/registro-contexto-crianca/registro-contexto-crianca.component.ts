import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { RegistroSaude, SalvarRegistroSaudeRequest, TipoRegistroSaude } from '../../../shared/models/saude.model';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { CriancasService } from '../../criancas/criancas.service';
import { SaudeService } from '../../saude/saude.service';

type Contexto = 'humor' | 'observacoes';

@Component({
  selector: 'app-registro-contexto-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent],
  templateUrl: './registro-contexto-crianca.component.html',
  styleUrl: './registro-contexto-crianca.component.scss'
})
export class RegistroContextoCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly saudeService = inject(SaudeService);
  readonly contexto = (this.route.snapshot.data['contexto'] as Contexto) ?? 'observacoes';
  readonly tipo = this.contexto === 'humor' ? 'HUMOR_COMPORTAMENTO' as const : 'OBSERVACAO_EVENTO_MARCANTE' as const;
  readonly crianca = signal<Crianca | null>(null);
  readonly registros = signal<RegistroSaude[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly editandoId = signal('');
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);
  readonly titulo = this.contexto === 'humor' ? 'Humor e comportamento' : 'Observações e eventos marcantes';
  readonly descricao = this.contexto === 'humor'
    ? 'Registre uma visão do período com palavras simples. Isso ajuda a lembrar do contexto, sem transformar comportamentos em diagnóstico.'
    : 'Guarde fatos que podem ajudar a contextualizar a rotina e uma conversa futura com o pediatra.';
  readonly form = this.fb.group({ dataRegistro: ['', Validators.required], descricao: ['', [Validators.required, Validators.maxLength(4000)]] });
  readonly registrosOrdenados = computed(() => [...this.registros()].sort((a, b) => b.dataRegistro.localeCompare(a.dataRegistro)));

  ngOnInit(): void {
    this.redefinir();
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    forkJoin({ crianca: this.criancasService.buscarPorId(id), registros: this.saudeService.listar(id) })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({ next: ({ crianca, registros }) => { this.crianca.set(crianca); this.registros.set(registros.filter((registro) => registro.tipo === this.tipo)); }, error: (erro: HttpErrorResponse) => this.erro.set(this.mensagemErro(erro)) });
  }

  rotaRetorno(): string[] { return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento' ? ['/acompanhamento'] : ['/criancas', this.route.snapshot.paramMap.get('id') ?? '']; }
  textoRetorno(): string { return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento' ? 'Acompanhamento' : 'Perfil'; }
  formatarData(data: string): string { return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`)); }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); this.erro.set('Preencha a data e escreva uma observação antes de salvar.'); return; }
    const crianca = this.crianca();
    if (!crianca) return;
    let request: SalvarRegistroSaudeRequest;
    try { request = { tipo: this.tipo, dataRegistro: this.lerData(this.form.controls.dataRegistro.value), descricao: this.form.controls.descricao.value?.trim() ?? '' }; } catch (erro) { this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro.'); return; }
    this.salvando.set(true);
    const operacao = this.editandoId() ? this.saudeService.atualizar(crianca.id, this.editandoId(), request) : this.saudeService.registrar(crianca.id, request);
    operacao.pipe(finalize(() => this.salvando.set(false))).subscribe({ next: (registro) => { this.registros.update((itens) => [registro, ...itens.filter((item) => item.id !== registro.id)]); this.redefinir(); }, error: (erro: HttpErrorResponse) => this.erro.set(this.mensagemErro(erro)) });
  }

  editar(registro: RegistroSaude): void { this.editandoId.set(registro.id); this.form.setValue({ dataRegistro: this.paraEntrada(registro.dataRegistro), descricao: registro.descricao }); }
  cancelarEdicao(): void { this.redefinir(); }

  private redefinir(): void { this.editandoId.set(''); this.form.reset({ dataRegistro: this.paraEntrada(this.dataMaximaIso), descricao: '' }); }
  private paraEntrada(data: string): string { const [ano, mes, dia] = data.split('-'); return `${dia}/${mes}/${ano}`; }
  private lerData(valor: string | null): string { const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec((valor ?? '').trim()); if (!partes) throw new Error('Informe a data no formato dd/mm/aaaa.'); const iso = `${partes[3]}-${partes[2]}-${partes[1]}`; if (iso > this.dataMaximaIso) throw new Error('A data não pode estar no futuro.'); return iso; }
  private mensagemErro(erro: HttpErrorResponse): string { const mensagens = erro.error?.mensagens; return Array.isArray(mensagens) && mensagens.length ? mensagens[0] : 'Não foi possível salvar este registro agora.'; }
}
