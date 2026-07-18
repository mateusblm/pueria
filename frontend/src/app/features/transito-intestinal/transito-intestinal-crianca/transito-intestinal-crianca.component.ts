import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { ClassificacaoFezes, FacilidadeLimpezaFezes, RegistroTransitoIntestinal, SalvarRegistroTransitoIntestinalRequest, TipoFezesBristol } from '../../../shared/models/transito-intestinal.model';
import { CriancasService } from '../../criancas/criancas.service';
import { TransitoIntestinalService } from '../transito-intestinal.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { ToastService } from '../../../core/toast/toast.service';

type Opcao<T extends string> = { valor: T; label: string };
type BristolOpcao = {
  valor: TipoFezesBristol;
  numero: string;
  titulo: string;
  descricao: string;
  pista: string;
};

@Component({
  selector: 'app-transito-intestinal-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent],
  templateUrl: './transito-intestinal-crianca.component.html',
  styleUrl: './transito-intestinal-crianca.component.scss'
})
export class TransitoIntestinalCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly transitoService = inject(TransitoIntestinalService);
  private readonly toast = inject(ToastService);

  readonly crianca = signal<Crianca | null>(null);

  rotaRetorno(): string[] {
    return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento'
      ? ['/acompanhamento']
      : ['/criancas', this.route.snapshot.paramMap.get('id') ?? ''];
  }

  textoRetorno(): string {
    return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento' ? 'Acompanhamento' : 'Perfil';
  }
  readonly registros = signal<RegistroTransitoIntestinal[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly aviso = signal('');
  private readonly notificarErro = effect(() => {
    const mensagem = this.erro();
    if (mensagem) this.toast.erro(mensagem);
  });
  private readonly notificarSucesso = effect(() => {
    const mensagem = this.aviso();
    if (mensagem) this.toast.sucesso(mensagem);
  });
  readonly editandoId = signal('');
  readonly etapaRegistro = signal<1 | 2>(1);
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly tiposBristol: BristolOpcao[] = [
    { valor: 'TIPO_1', numero: '1', titulo: 'Bolinhas endurecidas', descricao: 'Pedaços duros e separados, difíceis de eliminar.', pista: 'Pode sugerir fezes muito ressecadas.' },
    { valor: 'TIPO_2', numero: '2', titulo: 'Alongada e endurecida', descricao: 'Formato alongado, mas com partes endurecidas e irregulares.', pista: 'Pode aparecer quando evacuar está difícil.' },
    { valor: 'TIPO_3', numero: '3', titulo: 'Alongada com fissuras', descricao: 'Formato alongado, com pequenas rachaduras na superfície.', pista: 'Costuma ficar dentro do esperado.' },
    { valor: 'TIPO_4', numero: '4', titulo: 'Macia e lisa', descricao: 'Formato alongado, macio e fácil de eliminar.', pista: 'Costuma ser uma consistência esperada.' },
    { valor: 'TIPO_5', numero: '5', titulo: 'Pedaços macios', descricao: 'Pedaços separados, macios e com bordas definidas.', pista: 'Pode ser normal, mas vale observar repetição.' },
    { valor: 'TIPO_6', numero: '6', titulo: 'Pastosa ou amolecida', descricao: 'Pedaços mais moles, com bordas irregulares.', pista: 'Pode indicar fezes mais soltas.' },
    { valor: 'TIPO_7', numero: '7', titulo: 'Líquida', descricao: 'Sem pedaços sólidos, inteiramente líquida.', pista: 'Merece atenção se persistir ou vier com outros sinais.' }
  ];

  readonly facilidadesLimpeza: Opcao<FacilidadeLimpezaFezes>[] = [
    { valor: 'NAO_INFORMADO', label: 'Não informado' },
    { valor: 'FACIL', label: 'Fácil de limpar' },
    { valor: 'DIFICIL', label: 'Difícil de limpar' }
  ];

  readonly form = this.fb.group({
    dataRegistro: ['', Validators.required],
    tipoFezes: this.fb.nonNullable.control<TipoFezesBristol>('NAO_INFORMADO', Validators.required),
    evacuacoesPorDia: [''],
    facilidadeLimpeza: this.fb.nonNullable.control<FacilidadeLimpezaFezes>('NAO_INFORMADO', Validators.required),
    muco: [false],
    restosAlimentares: [false],
    raiasSangue: [false],
    constipacao: [false],
    diarreia: [false],
    dorEvacuar: [false],
    escapeFecal: [false],
    assaduraFrequente: [false],
    assaduraVermelhidao: [false],
    assaduraPontosVermelhos: [false],
    preocupacaoFamilia: [false],
    observacao: ['', Validators.maxLength(1000)]
  });

  readonly registrosOrdenados = computed(() =>
    [...this.registros()].sort((a, b) => this.compararRegistrosRecentes(a, b))
  );
  readonly ultimoRegistro = computed(() => this.registrosOrdenados()[0] ?? null);

  ngOnInit(): void {
    this.form.patchValue({ dataRegistro: this.formatarEntradaData(this.dataMaximaIso) });
    this.carregar();
  }

  carregar(): void {
    const criancaId = this.route.snapshot.paramMap.get('id') ?? '';
    this.carregando.set(true);
    this.erro.set('');

    forkJoin({
      crianca: this.criancasService.buscarPorId(criancaId),
      registros: this.transitoService.listar(criancaId)
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ crianca, registros }) => {
          this.crianca.set(crianca);
          this.registros.set(registros);
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  selecionarTipo(tipo: TipoFezesBristol): void {
    this.form.controls.tipoFezes.setValue(tipo);
  }

  avancarEtapa(): void {
    this.erro.set('');
    try {
      this.lerData(this.form.controls.dataRegistro.value);
      this.etapaRegistro.set(2);
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise os dados do dia.');
    }
  }

  voltarEtapa(): void {
    this.erro.set('');
    this.etapaRegistro.set(1);
  }

  salvar(): void {
    this.erro.set('');
    this.aviso.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro.set('Revise o registro antes de salvar.');
      return;
    }

    let request: SalvarRegistroTransitoIntestinalRequest;
    try {
      request = this.criarRequest();
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro intestinal.');
      return;
    }

    const criancaId = this.crianca()?.id;
    if (!criancaId) {
      this.erro.set('Não foi possível identificar a criança.');
      return;
    }

    this.salvando.set(true);
    const editandoId = this.editandoId();
    const requisicao = editandoId
      ? this.transitoService.atualizar(criancaId, editandoId, request)
      : this.transitoService.registrar(criancaId, request);

    requisicao
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: (registro) => {
          this.registros.update((registros) => {
            const semAtual = registros.filter((item) => item.id !== registro.id);
            return [...semAtual, registro];
          });
          this.cancelarEdicao();
          this.aviso.set('Registro intestinal salvo.');
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  editar(registro: RegistroTransitoIntestinal): void {
    this.editandoId.set(registro.id);
    this.etapaRegistro.set(1);
    this.erro.set('');
    this.aviso.set('');
    this.form.patchValue({
      dataRegistro: this.formatarEntradaData(registro.dataRegistro),
      tipoFezes: registro.tipoFezes,
      evacuacoesPorDia: this.formatarInteiro(registro.evacuacoesPorDia),
      facilidadeLimpeza: registro.facilidadeLimpeza,
      muco: !!registro.muco,
      restosAlimentares: !!registro.restosAlimentares,
      raiasSangue: !!registro.raiasSangue,
      constipacao: !!registro.constipacao,
      diarreia: !!registro.diarreia,
      dorEvacuar: !!registro.dorEvacuar,
      escapeFecal: !!registro.escapeFecal,
      assaduraFrequente: !!registro.assaduraFrequente,
      assaduraVermelhidao: !!registro.assaduraVermelhidao,
      assaduraPontosVermelhos: !!registro.assaduraPontosVermelhos,
      preocupacaoFamilia: !!registro.preocupacaoFamilia,
      observacao: registro.observacao ?? ''
    });
  }

  cancelarEdicao(): void {
    this.editandoId.set('');
    this.etapaRegistro.set(1);
    this.form.reset({
      dataRegistro: this.formatarEntradaData(this.dataMaximaIso),
      tipoFezes: 'NAO_INFORMADO',
      evacuacoesPorDia: '',
      facilidadeLimpeza: 'NAO_INFORMADO',
      muco: false,
      restosAlimentares: false,
      raiasSangue: false,
      constipacao: false,
      diarreia: false,
      dorEvacuar: false,
      escapeFecal: false,
      assaduraFrequente: false,
      assaduraVermelhidao: false,
      assaduraPontosVermelhos: false,
      preocupacaoFamilia: false,
      observacao: ''
    });
  }

  tipoSelecionado(): TipoFezesBristol {
    return this.form.controls.tipoFezes.value;
  }

  opcaoSelecionada(): BristolOpcao | undefined {
    return this.tiposBristol.find((opcao) => opcao.valor === this.tipoSelecionado());
  }

  labelTipoFezes(valor: TipoFezesBristol): string {
    return this.tiposBristol.find((opcao) => opcao.valor === valor)?.titulo ?? 'Não informado';
  }

  labelFacilidade(valor: FacilidadeLimpezaFezes): string {
    return this.facilidadesLimpeza.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  labelClassificacaoFezes(classificacao: ClassificacaoFezes): string {
    const labels: Record<ClassificacaoFezes, string> = {
      ENDURECIDA: 'Mais endurecida',
      ESPERADA: 'Faixa esperada',
      MAIS_MACIA: 'Mais macia',
      LIQUIDA: 'Líquida',
      SEM_DADOS: 'Aguardando registro do aspecto'
    };
    return labels[classificacao];
  }

  possuiAlgumaAnalise(registro: RegistroTransitoIntestinal | null): boolean {
    if (!registro) {
      return false;
    }
    return registro.analise.rotina.length > 0 || registro.analise.conversaConsulta.length > 0 || registro.analise.habitosApoio.length > 0;
  }

  formatarData(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`));
  }

  private criarRequest(): SalvarRegistroTransitoIntestinalRequest {
    const valor = this.form.getRawValue();
    return {
      dataRegistro: this.lerData(valor.dataRegistro),
      tipoFezes: valor.tipoFezes,
      evacuacoesPorDia: this.lerInteiro(valor.evacuacoesPorDia, 'evacuações por dia', 0, 30),
      facilidadeLimpeza: valor.facilidadeLimpeza,
      muco: valor.muco,
      restosAlimentares: valor.restosAlimentares,
      raiasSangue: valor.raiasSangue,
      constipacao: valor.constipacao,
      diarreia: valor.diarreia,
      dorEvacuar: valor.dorEvacuar,
      escapeFecal: valor.escapeFecal,
      assaduraFrequente: valor.assaduraFrequente,
      assaduraVermelhidao: valor.assaduraVermelhidao,
      assaduraPontosVermelhos: valor.assaduraPontosVermelhos,
      preocupacaoFamilia: valor.preocupacaoFamilia,
      observacao: valor.observacao?.trim() || null
    };
  }

  private lerData(valor: string | null | undefined): string {
    const texto = (valor ?? '').trim();
    const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(texto);
    if (!partes) {
      throw new Error('Informe a data no formato dd/mm/aaaa.');
    }
    const dia = Number(partes[1]);
    const mes = Number(partes[2]);
    const ano = Number(partes[3]);
    const data = new Date(Date.UTC(ano, mes - 1, dia));
    const dataExiste = data.getUTCFullYear() === ano && data.getUTCMonth() === mes - 1 && data.getUTCDate() === dia;
    if (!dataExiste) {
      throw new Error('Informe uma data válida.');
    }
    const iso = `${ano.toString().padStart(4, '0')}-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}`;
    if (iso > this.dataMaximaIso) {
      throw new Error('A data não pode estar no futuro.');
    }
    return iso;
  }

  private lerInteiro(valor: string | null | undefined, label: string, minimo: number, maximo: number): number | null {
    const texto = (valor ?? '').trim();
    if (!texto) {
      return null;
    }
    if (!/^\d+$/.test(texto)) {
      throw new Error(`Informe ${label} usando números inteiros.`);
    }
    const numero = Number(texto);
    if (numero < minimo || numero > maximo) {
      throw new Error(`O campo ${label} está fora do limite esperado.`);
    }
    return numero;
  }

  private formatarInteiro(valor?: number | null): string {
    return valor === null || valor === undefined ? '' : String(valor);
  }

  private formatarEntradaData(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  private compararRegistrosRecentes(a: RegistroTransitoIntestinal, b: RegistroTransitoIntestinal): number {
    const porData = b.dataRegistro.localeCompare(a.dataRegistro);
    if (porData !== 0) {
      return porData;
    }
    const momentoA = a.atualizadoEm ?? a.criadoEm;
    const momentoB = b.atualizadoEm ?? b.criadoEm;
    return momentoB.localeCompare(momentoA);
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar o trânsito intestinal agora.';
  }
}
