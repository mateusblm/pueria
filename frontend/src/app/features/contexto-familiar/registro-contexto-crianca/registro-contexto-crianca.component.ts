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
import { MENSAGEM_REGISTRO_SALVO, ToastService } from '../../../core/toast/toast.service';
import { mensagemErroHttp } from '../../../core/errors/mensagem-erro';

type Contexto = 'humor' | 'observacoes';
type HumorPredominante = 'DIFICIL' | 'NEUTRO' | 'TRANQUILO' | 'OTIMO';
type TipoEventoContexto = 'ROTINA' | 'FAMILIA' | 'SAUDE' | 'VIAGEM' | 'MARCO';

const OPCOES_HUMOR: ReadonlyArray<{ id: HumorPredominante; rotulo: string; emoji: string }> = [
  { id: 'DIFICIL', rotulo: 'Difícil', emoji: '😣' },
  { id: 'NEUTRO', rotulo: 'Neutro', emoji: '😐' },
  { id: 'TRANQUILO', rotulo: 'Tranquilo', emoji: '🙂' },
  { id: 'OTIMO', rotulo: 'Ótimo', emoji: '😄' }
];

const OBSERVACOES_HUMOR = ['Chorou fácil', 'Brincou bastante', 'Buscou colo', 'Se acalmou sozinho', 'Mais quieto'];
const TIPOS_EVENTO: ReadonlyArray<{ id: TipoEventoContexto; rotulo: string; simbolo: string }> = [
  { id: 'ROTINA', rotulo: 'Rotina', simbolo: '◆' },
  { id: 'FAMILIA', rotulo: 'Família', simbolo: '♡' },
  { id: 'SAUDE', rotulo: 'Saúde', simbolo: '✚' },
  { id: 'VIAGEM', rotulo: 'Viagem', simbolo: '⚑' },
  { id: 'MARCO', rotulo: 'Marco', simbolo: '◐' }
];

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
  private readonly toast = inject(ToastService);
  readonly contexto = (this.route.snapshot.data['contexto'] as Contexto) ?? 'observacoes';
  readonly tipo = this.contexto === 'humor' ? 'HUMOR_COMPORTAMENTO' as const : 'OBSERVACAO_EVENTO_MARCANTE' as const;
  readonly crianca = signal<Crianca | null>(null);
  readonly registros = signal<RegistroSaude[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly editandoId = signal('');
  readonly humorSelecionado = signal<HumorPredominante>('TRANQUILO');
  readonly observacoesSelecionadas = signal<string[]>([]);
  readonly contextoHistoricoCompleto = signal(false);
  readonly eventoSelecionado = signal<TipoEventoContexto>('ROTINA');
  readonly cadastroRapidoAberto = signal(false);
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);
  readonly titulo = this.contexto === 'humor' ? 'Humor e comportamento' : 'Observações e eventos marcantes';
  readonly descricao = this.contexto === 'humor'
    ? 'Registre uma visão do período com palavras simples. Isso ajuda a lembrar do contexto, sem transformar comportamentos em diagnóstico.'
    : 'Guarde fatos que podem ajudar a contextualizar a rotina e uma conversa futura com o pediatra.';
  readonly form = this.fb.group({ dataRegistro: ['', Validators.required], descricao: ['', [Validators.required, Validators.maxLength(4000)]] });
  readonly registrosOrdenados = computed(() => [...this.registros()].sort((a, b) => b.dataRegistro.localeCompare(a.dataRegistro)));
  readonly opcoesHumor = OPCOES_HUMOR;
  readonly observacoesHumor = OBSERVACOES_HUMOR;
  readonly tiposEvento = TIPOS_EVENTO;
  readonly semanaHumor = computed(() => {
    const registrosPorData = new Map(this.registros().map((registro) => [registro.dataRegistro, registro]));
    const hoje = new Date(`${this.dataMaximaIso}T12:00:00`);
    return Array.from({ length: 7 }, (_, indice) => {
      const data = new Date(hoje);
      data.setDate(hoje.getDate() - 6 + indice);
      const iso = `${data.getFullYear()}-${String(data.getMonth() + 1).padStart(2, '0')}-${String(data.getDate()).padStart(2, '0')}`;
      const registro = registrosPorData.get(iso);
      return {
        iso,
        dia: new Intl.DateTimeFormat('pt-BR', { weekday: 'short' }).format(data).replace('.', ''),
        humor: registro ? this.humorDoRegistro(registro.descricao) : null
      };
    });
  });

  ngOnInit(): void {
    this.redefinir();
    this.cadastroRapidoAberto.set(this.route.snapshot.queryParamMap.get('cadastro') === 'rapido');
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    forkJoin({ crianca: this.criancasService.buscarPorId(id), registros: this.saudeService.listar(id) })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({ next: ({ crianca, registros }) => { this.crianca.set(crianca); this.registros.set(registros.filter((registro) => registro.tipo === this.tipo)); }, error: (erro: HttpErrorResponse) => this.erro.set(mensagemErroHttp(erro, 'Não foi possível carregar este contexto agora.')) });
  }

  fecharCadastroRapido(): void { this.cadastroRapidoAberto.set(false); }

  rotaRetorno(): string[] { return ['/acompanhamento']; }
  textoRetorno(): string { return 'Acompanhamento'; }
  formatarData(data: string): string { return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`)); }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); this.erro.set('Preencha a data e escreva uma observação antes de salvar.'); return; }
    const crianca = this.crianca();
    if (!crianca) return;
    let request: SalvarRegistroSaudeRequest;
    try { request = { tipo: this.tipo, dataRegistro: this.lerData(this.form.controls.dataRegistro.value), descricao: this.descricaoParaSalvar() }; } catch (erro) { this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro.'); return; }
    this.salvando.set(true);
    const operacao = this.editandoId() ? this.saudeService.atualizar(crianca.id, this.editandoId(), request) : this.saudeService.registrar(crianca.id, request);
    operacao.pipe(finalize(() => this.salvando.set(false))).subscribe({ next: (registro) => { this.registros.update((itens) => [registro, ...itens.filter((item) => item.id !== registro.id)]); this.redefinir(); this.toast.sucesso(MENSAGEM_REGISTRO_SALVO); }, error: (erro: HttpErrorResponse) => this.erro.set(mensagemErroHttp(erro, 'Não foi possível salvar este registro agora.')) });
  }

  editar(registro: RegistroSaude): void {
    this.editandoId.set(registro.id);
    this.humorSelecionado.set(this.humorDoRegistro(registro.descricao));
    this.eventoSelecionado.set(this.eventoDoRegistro(registro.descricao));
    this.observacoesSelecionadas.set(this.observacoesDoRegistro(registro.descricao));
    this.form.setValue({ dataRegistro: this.paraEntrada(registro.dataRegistro), descricao: this.descricaoLivre(registro.descricao) });
  }
  cancelarEdicao(): void { this.redefinir(); }

  selecionarHumor(humor: HumorPredominante): void { this.humorSelecionado.set(humor); }
  selecionarEvento(evento: TipoEventoContexto): void { this.eventoSelecionado.set(evento); }
  alternarObservacao(observacao: string): void { this.observacoesSelecionadas.update((selecionadas) => selecionadas.includes(observacao) ? selecionadas.filter((item) => item !== observacao) : [...selecionadas, observacao]); }
  observacaoSelecionada(observacao: string): boolean { return this.observacoesSelecionadas().includes(observacao); }
  emojiHumor(humor: HumorPredominante | null): string { return OPCOES_HUMOR.find((opcao) => opcao.id === humor)?.emoji ?? '○'; }
  rotuloHumor(humor: HumorPredominante | null): string { return OPCOES_HUMOR.find((opcao) => opcao.id === humor)?.rotulo ?? 'Sem registro'; }
  descricaoParaLinhaDoTempo(descricao: string): string { return this.descricaoLivre(descricao) || `Humor predominante: ${this.rotuloHumor(this.humorDoRegistro(descricao))}.`; }
  eventoDoRegistro(descricao: string): TipoEventoContexto { return (TIPOS_EVENTO.find((tipo) => new RegExp(`Evento:\\s*${tipo.rotulo}`, 'i').test(descricao))?.id ?? 'ROTINA'); }
  rotuloEvento(evento: TipoEventoContexto): string { return TIPOS_EVENTO.find((tipo) => tipo.id === evento)?.rotulo ?? 'Rotina'; }
  simboloEvento(evento: TipoEventoContexto): string { return TIPOS_EVENTO.find((tipo) => tipo.id === evento)?.simbolo ?? '◆'; }
  descricaoParaExibicao(descricao: string): string { return this.descricaoLivre(descricao); }

  private redefinir(): void { this.editandoId.set(''); this.humorSelecionado.set('TRANQUILO'); this.eventoSelecionado.set('ROTINA'); this.observacoesSelecionadas.set([]); this.form.reset({ dataRegistro: this.paraEntrada(this.dataMaximaIso), descricao: '' }); }
  private descricaoParaSalvar(): string {
    const descricao = this.form.controls.descricao.value?.trim() ?? '';
    if (this.contexto !== 'humor') return [`Evento: ${this.rotuloEvento(this.eventoSelecionado())}.`, descricao].filter(Boolean).join('\n');
    const observacoes = this.observacoesSelecionadas();
    const contexto = [`Humor predominante: ${this.rotuloHumor(this.humorSelecionado())}.`, observacoes.length ? `Observações: ${observacoes.join(', ')}.` : ''].filter(Boolean).join(' ');
    return [contexto, descricao].filter(Boolean).join('\n');
  }
  humorDoRegistro(descricao: string): HumorPredominante {
    const encontrado = /Humor predominante:\s*(Difícil|Neutro|Tranquilo|Ótimo)/i.exec(descricao)?.[1]?.toLocaleUpperCase('pt-BR');
    const porRotulo: Record<string, HumorPredominante> = { 'DIFÍCIL': 'DIFICIL', 'NEUTRO': 'NEUTRO', 'TRANQUILO': 'TRANQUILO', 'ÓTIMO': 'OTIMO' };
    if (encontrado && porRotulo[encontrado]) return porRotulo[encontrado];
    const normalizado = descricao.toLocaleLowerCase('pt-BR');
    if (/irrit|chor|difícil|difcil/.test(normalizado)) return 'DIFICIL';
    if (/riu|ótimo|otimo|muito bem/.test(normalizado)) return 'OTIMO';
    if (/tranquil|calm/.test(normalizado)) return 'TRANQUILO';
    return 'NEUTRO';
  }
  private observacoesDoRegistro(descricao: string): string[] {
    const observacoes = /Observações:\s*([^\n.]+)\.?/i.exec(descricao)?.[1] ?? '';
    return OBSERVACOES_HUMOR.filter((item) => observacoes.includes(item));
  }
  private descricaoLivre(descricao: string): string { return descricao.replace(/^Humor predominante:[^\n]*\n?/i, '').replace(/^Observações:[^\n]*\n?/i, '').replace(/^Evento:[^\n]*\n?/i, '').trim(); }
  private paraEntrada(data: string): string { const [ano, mes, dia] = data.split('-'); return `${dia}/${mes}/${ano}`; }
  private lerData(valor: string | null): string { const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec((valor ?? '').trim()); if (!partes) throw new Error('Informe a data no formato dd/mm/aaaa.'); const iso = `${partes[3]}-${partes[2]}-${partes[1]}`; if (iso > this.dataMaximaIso) throw new Error('A data não pode estar no futuro.'); return iso; }
}
