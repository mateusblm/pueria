import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { MENSAGEM_REGISTRO_SALVO, ToastService } from '../../../core/toast/toast.service';
import { Crianca } from '../../../shared/models/crianca.model';
import { RegistroSaude, SalvarRegistroSaudeRequest, TipoRegistroSaude } from '../../../shared/models/saude.model';
import { CriancasService } from '../../criancas/criancas.service';
import { SaudeService } from '../saude.service';
import { mensagemErroHttp } from '../../../core/errors/mensagem-erro';

@Component({
  selector: 'app-saude-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent],
  templateUrl: './saude-crianca.component.html',
  styleUrl: './saude-crianca.component.scss'
})
export class SaudeCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly saudeService = inject(SaudeService);
  private readonly toast = inject(ToastService);

  readonly crianca = signal<Crianca | null>(null);
  readonly registros = signal<RegistroSaude[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly removendoId = signal('');
  readonly confirmandoRemocaoId = signal('');
  readonly editandoId = signal('');
  readonly erro = signal('');
  readonly aviso = signal('');
  readonly entendaAberto = signal(false);
  private readonly notificarErro = effect(() => {
    const mensagem = this.erro();
    if (mensagem) this.toast.erro(mensagem);
  });
  private readonly notificarSucesso = effect(() => {
    const mensagem = this.aviso();
    if (mensagem) this.toast.sucesso(mensagem);
  });
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);
  readonly formularioAberto = signal(false);

  readonly form = this.fb.group({
    tipo: this.fb.nonNullable.control<TipoRegistroSaude>('MEDICAMENTO_SUPLEMENTO', Validators.required),
    dataRegistro: ['', Validators.required],
    descricao: ['', [Validators.required, Validators.maxLength(4000)]]
  });

  readonly registrosMedicamentos = computed(() => this.registros().filter((registro) => registro.tipo === 'MEDICAMENTO_SUPLEMENTO'));
  readonly intercorrencias = computed(() => this.registros().filter((registro) => registro.tipo === 'INTERCORRENCIA_CLINICA'));

  abrirEntenda(): void {
    this.entendaAberto.set(true);
  }

  fecharEntenda(): void {
    this.entendaAberto.set(false);
  }

  rotaRetorno(): string[] {
    return ['/acompanhamento'];
  }

  textoRetorno(): string {
    return 'Acompanhamento';
  }

  ngOnInit(): void {
    this.redefinirFormulario();
    const criancaId = this.route.snapshot.paramMap.get('id') ?? '';
    this.carregando.set(true);
    forkJoin({ crianca: this.criancasService.buscarPorId(criancaId), registros: this.saudeService.listar(criancaId) })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ crianca, registros }) => { this.crianca.set(crianca); this.registros.set(registros); },
        error: (erro: HttpErrorResponse) => this.erro.set(mensagemErroHttp(erro, 'Não foi possível carregar os registros de saúde agora.'))
      });
  }

  selecionarTipo(tipo: TipoRegistroSaude): void {
    this.form.controls.tipo.setValue(tipo);
  }

  salvar(): void {
    this.erro.set('');
    this.aviso.set('');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro.set('Preencha a data e escreva o registro antes de salvar.');
      return;
    }
    let request: SalvarRegistroSaudeRequest;
    try { request = this.criarRequest(); } catch (erro) { this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro antes de salvar.'); return; }
    const criancaId = this.crianca()?.id;
    if (!criancaId) { this.erro.set('Não foi possível identificar a criança.'); return; }
    this.salvando.set(true);
    const requisicao = this.editandoId()
      ? this.saudeService.atualizar(criancaId, this.editandoId(), request)
      : this.saudeService.registrar(criancaId, request);
    requisicao.pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: (registro) => {
        this.registros.update((itens) => [registro, ...itens.filter((item) => item.id !== registro.id)].sort(this.ordenarRegistros));
        this.redefinirFormulario();
        this.formularioAberto.set(false);
        this.aviso.set(MENSAGEM_REGISTRO_SALVO);
      },
      error: (erro: HttpErrorResponse) => this.erro.set(mensagemErroHttp(erro, 'Não foi possível carregar os registros de saúde agora.'))
    });
  }

  editar(registro: RegistroSaude): void {
    this.formularioAberto.set(true);
    this.editandoId.set(registro.id);
    this.confirmandoRemocaoId.set('');
    this.erro.set('');
    this.aviso.set('');
    this.form.setValue({ tipo: registro.tipo, dataRegistro: this.formatarEntradaData(registro.dataRegistro), descricao: registro.descricao });
  }

  cancelarEdicao(): void { this.redefinirFormulario(); this.formularioAberto.set(false); }

  abrirFormulario(): void { this.formularioAberto.set(true); }

  confirmarRemocao(registroId: string): void { this.confirmandoRemocaoId.set(registroId); }

  cancelarRemocao(): void { this.confirmandoRemocaoId.set(''); }

  remover(registro: RegistroSaude): void {
    const criancaId = this.crianca()?.id;
    if (!criancaId) return;
    this.removendoId.set(registro.id);
    this.saudeService.remover(criancaId, registro.id).pipe(finalize(() => this.removendoId.set(''))).subscribe({
      next: () => { this.registros.update((itens) => itens.filter((item) => item.id !== registro.id)); this.confirmandoRemocaoId.set(''); this.aviso.set('Registro removido.'); },
      error: (erro: HttpErrorResponse) => this.erro.set(mensagemErroHttp(erro, 'Não foi possível carregar os registros de saúde agora.'))
    });
  }

  tituloTipo(tipo: TipoRegistroSaude): string { return tipo === 'MEDICAMENTO_SUPLEMENTO' ? 'Suplementos e medicamentos de uso diário' : 'Intercorrência clínica'; }

  descricaoFormulario(): string {
    return this.form.controls.tipo.value === 'MEDICAMENTO_SUPLEMENTO'
      ? 'Registre apenas o que a criança usa todos os dias. Medicamentos usados por causa de uma intercorrência devem ser registrados na própria intercorrência.'
      : 'Registre o que aconteceu, o atendimento ou a orientação recebidos e os medicamentos usados somente nesta intercorrência.';
  }

  formatarData(data: string): string { return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`)); }

  private redefinirFormulario(): void {
    this.editandoId.set('');
    this.form.reset({ tipo: 'MEDICAMENTO_SUPLEMENTO', dataRegistro: this.formatarEntradaData(this.dataMaximaIso), descricao: '' });
  }

  private criarRequest(): SalvarRegistroSaudeRequest {
    const valor = this.form.getRawValue();
    return { tipo: valor.tipo, dataRegistro: this.lerData(valor.dataRegistro), descricao: valor.descricao?.trim() ?? '' };
  }

  private lerData(valor: string | null | undefined): string {
    const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec((valor ?? '').trim());
    if (!partes) throw new Error('Informe a data no formato dd/mm/aaaa.');
    const dia = Number(partes[1]); const mes = Number(partes[2]); const ano = Number(partes[3]);
    const data = new Date(Date.UTC(ano, mes - 1, dia));
    if (data.getUTCFullYear() !== ano || data.getUTCMonth() !== mes - 1 || data.getUTCDate() !== dia) throw new Error('Informe uma data válida.');
    const iso = `${ano.toString().padStart(4, '0')}-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}`;
    if (iso > this.dataMaximaIso) throw new Error('A data não pode estar no futuro.');
    return iso;
  }

  private formatarEntradaData(dataIso: string): string { const [ano, mes, dia] = dataIso.split('-'); return `${dia}/${mes}/${ano}`; }

  private ordenarRegistros = (a: RegistroSaude, b: RegistroSaude): number => {
    const porData = b.dataRegistro.localeCompare(a.dataRegistro);
    return porData !== 0 ? porData : (b.atualizadoEm ?? b.criadoEm).localeCompare(a.atualizadoEm ?? a.criadoEm);
  };

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    return Array.isArray(mensagens) && mensagens.length > 0 ? mensagens[0] : 'Não foi possível carregar os registros de saúde agora.';
  }
}
