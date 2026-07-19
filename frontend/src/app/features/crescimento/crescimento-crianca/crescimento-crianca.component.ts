import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { CriancasService } from '../../criancas/criancas.service';
import { Crianca } from '../../../shared/models/crianca.model';
import { AvaliacaoCurvaCrescimento, ClassificacaoCurvaCrescimento, MedidaCrescimento, OrigemMedidaCrescimento, ResponsavelMedicaoCrescimento, ResultadoCurvaCrescimento, SalvarMedidaCrescimentoRequest } from '../../../shared/models/crescimento.model';
import { CrescimentoService } from '../crescimento.service';
import { AppIconComponent, AppIconName } from '../../../shared/components/app-icon/app-icon.component';
import { ToastService } from '../../../core/toast/toast.service';
import { RegistroRapidoComponent } from '../../../shared/components/registro-rapido/registro-rapido.component';

type PontoGraficoCrescimento = {
  label: string;
  valor: string;
  zScore: number;
  percentil: number;
  x: number;
  y: number;
  cor: string;
};

type GraficoCrescimento = {
  indicador: string;
  icone: AppIconName;
  tema: string;
  titulo: string;
  resumo: string;
  classe: string;
  situacao: SituacaoCurva;
  corTrajetoria: string;
  valorInicial: string;
  dataInicial: string;
  valorAtual: string;
  dataAtual: string;
  marcador: number;
  pontos: PontoGraficoCrescimento[];
  linhaTrajetoria: string;
  ariaGrafico: string;
  tecnico: {
    percentil: string;
    zScore: string;
    classificacao: string;
    fonte: string;
    criterioIdade: string;
  };
};

type SituacaoCurva = 'esperada' | 'abaixo' | 'acima';

type DetalheIndicadorCrescimento = {
  acompanha: string;
  interpretacao: string;
  consulta: string;
};

@Component({
  selector: 'app-crescimento-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent, RegistroRapidoComponent],
  templateUrl: './crescimento-crianca.component.html',
  styleUrl: './crescimento-crianca.component.scss'
})
export class CrescimentoCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  rotaRetorno(): string[] {
    return this.route.snapshot.queryParamMap.get('origem') === 'perfil'
      ? ['/criancas', this.route.snapshot.paramMap.get('id') ?? '']
      : ['/acompanhamento'];
  }

  textoRetorno(): string {
    return this.route.snapshot.queryParamMap.get('origem') === 'perfil' ? 'Perfil' : 'Acompanhamento';
  }
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly crescimentoService = inject(CrescimentoService);
  private readonly toast = inject(ToastService);

  readonly crianca = signal<Crianca | null>(null);
  readonly medidas = signal<MedidaCrescimento[]>([]);
  readonly avaliacoesCurva = signal<AvaliacaoCurvaCrescimento[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly removendoId = signal('');
  readonly confirmandoRemocaoId = signal('');
  readonly editandoId = signal('');
  readonly registroAberto = signal(false);
  readonly etapaRegistro = signal<1 | 2>(1);
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
  readonly detalheAbertoIndicador = signal('');
  readonly entendaAberto = signal(false);
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly form = this.fb.group({
    dataMedicao: ['', Validators.required],
    pesoKg: [''],
    comprimentoCm: [''],
    perimetroCefalicoCm: [''],
    origem: this.fb.nonNullable.control<OrigemMedidaCrescimento>('CONSULTORIO', Validators.required),
    responsavelMedicao: this.fb.nonNullable.control<ResponsavelMedicaoCrescimento>('NAO_INFORMADO', Validators.required),
    observacao: ['', Validators.maxLength(500)]
  });

  readonly medidasOrdenadas = computed(() =>
    [...this.medidas()].sort((a, b) => this.compararMedidasRecentes(a, b))
  );

  readonly ultimaMedida = computed(() => this.medidasOrdenadas()[0] ?? null);
  readonly medidaAnterior = computed(() => this.medidasOrdenadas()[1] ?? null);
  readonly avaliacoesPorMedida = computed(() =>
    new Map(this.avaliacoesCurva().map((avaliacao) => [avaliacao.medidaId, avaliacao]))
  );
  readonly ultimaAvaliacaoCurva = computed(() => {
    const ultima = this.ultimaMedida();
    return ultima ? this.avaliacoesPorMedida().get(ultima.id) ?? null : null;
  });
  readonly referenciaAtual = computed(() => this.ultimaAvaliacaoCurva()?.resultados[0]?.fonte ?? 'Referência de crescimento');
  readonly contextoIdadeAtual = computed(() => this.textoContextoIdade(this.ultimaAvaliacaoCurva()));
  readonly graficosCurva = computed(() => this.montarGraficosCurva());
  readonly graficoDetalhado = computed(() =>
    this.graficosCurva().find((grafico) => grafico.indicador === this.detalheAbertoIndicador()) ?? null
  );

  ngOnInit(): void {
    this.form.patchValue({ dataMedicao: this.formatarEntradaData(this.dataMaximaIso) });
    this.carregar();
  }

  carregar(): void {
    const criancaId = this.route.snapshot.paramMap.get('id') ?? '';
    this.carregando.set(true);
    this.erro.set('');

    forkJoin({
      crianca: this.criancasService.buscarPorId(criancaId),
      medidas: this.crescimentoService.listar(criancaId),
      curvas: this.crescimentoService.listarCurvas(criancaId)
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ crianca, medidas, curvas }) => {
          this.crianca.set(crianca);
          this.medidas.set(medidas);
          this.avaliacoesCurva.set(curvas);
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  salvar(): void {
    this.erro.set('');
    this.aviso.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro.set('Revise os dados informados antes de salvar.');
      return;
    }

    let request: SalvarMedidaCrescimentoRequest | null;
    try {
      request = this.criarRequest();
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise as medidas informadas.');
      return;
    }

    if (!request) {
      this.erro.set('Informe pelo menos uma medida para acompanhar o crescimento.');
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
      ? this.crescimentoService.atualizar(criancaId, editandoId, request)
      : this.crescimentoService.registrar(criancaId, request);

    requisicao
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: (medida) => {
          this.medidas.update((medidas) => {
            const semAtual = medidas.filter((item) => item.id !== medida.id);
            return [...semAtual, medida];
          });
          this.recarregarCurvas();
          this.cancelarEdicao();
          this.registroAberto.set(false);
          this.aviso.set('Medida salva no histórico de crescimento.');
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  editar(medida: MedidaCrescimento): void {
    this.editandoId.set(medida.id);
    this.confirmandoRemocaoId.set('');
    this.aviso.set('');
    this.erro.set('');
    this.etapaRegistro.set(1);
    this.registroAberto.set(true);
    this.form.patchValue({
      dataMedicao: this.formatarEntradaData(medida.dataMedicao),
      pesoKg: this.formatarEntradaDecimal(medida.pesoKg),
      comprimentoCm: this.formatarEntradaDecimal(medida.comprimentoCm),
      perimetroCefalicoCm: this.formatarEntradaDecimal(medida.perimetroCefalicoCm),
      origem: medida.origem,
      responsavelMedicao: medida.responsavelMedicao,
      observacao: medida.observacao ?? ''
    });
  }

  cancelarEdicao(): void {
    this.editandoId.set('');
    this.form.reset({
      dataMedicao: this.formatarEntradaData(this.dataMaximaIso),
      pesoKg: '',
      comprimentoCm: '',
      perimetroCefalicoCm: '',
      origem: 'CONSULTORIO',
      responsavelMedicao: 'NAO_INFORMADO',
      observacao: ''
    });
  }

  abrirRegistro(): void {
    this.erro.set('');
    this.aviso.set('');
    this.etapaRegistro.set(1);
    this.registroAberto.set(true);
  }

  fecharRegistro(): void {
    this.registroAberto.set(false);
    this.cancelarEdicao();
  }

  avancarEtapa(): void {
    try {
      this.lerDataMedicao(this.form.controls.dataMedicao.value);
      this.erro.set('');
      this.etapaRegistro.set(2);
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise a data da medição.');
    }
  }

  voltarEtapa(): void {
    this.etapaRegistro.set(1);
  }

  pedirRemocao(medidaId: string): void {
    this.confirmandoRemocaoId.set(medidaId);
    this.aviso.set('');
    this.erro.set('');
  }

  cancelarRemocao(): void {
    this.confirmandoRemocaoId.set('');
  }

  remover(medidaId: string): void {
    const criancaId = this.crianca()?.id;
    if (!criancaId) {
      return;
    }

    this.removendoId.set(medidaId);
    this.crescimentoService.remover(criancaId, medidaId)
      .pipe(finalize(() => this.removendoId.set('')))
      .subscribe({
        next: () => {
          this.medidas.update((medidas) => medidas.filter((medida) => medida.id !== medidaId));
          this.confirmandoRemocaoId.set('');
          this.recarregarCurvas();
          this.aviso.set('Medida removida do histórico.');
          if (this.editandoId() === medidaId) {
            this.cancelarEdicao();
          }
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  resultadosDaMedida(medidaId: string): ResultadoCurvaCrescimento[] {
    return this.avaliacoesPorMedida().get(medidaId)?.resultados ?? [];
  }

  classeResultado(resultado: ResultadoCurvaCrescimento): string {
    return `crescimento-curva__badge--${this.situacaoCurva(resultado.classificacao)}`;
  }

  formatarZScore(valor: number): string {
    const sinal = valor > 0 ? '+' : '';
    return `${sinal}${new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(valor)} DP`;
  }

  formatarPercentil(valor: number): string {
    return `P${new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 1 }).format(valor)}`;
  }

  abrirDetalhes(grafico: GraficoCrescimento): void {
    this.detalheAbertoIndicador.set(grafico.indicador);
  }

  abrirEntenda(): void {
    this.entendaAberto.set(true);
  }

  fecharEntenda(): void {
    this.entendaAberto.set(false);
  }

  fecharDetalhes(): void {
    this.detalheAbertoIndicador.set('');
  }

  detalheIndicador(indicador: string): DetalheIndicadorCrescimento {
    const detalhes: Record<string, DetalheIndicadorCrescimento> = {
      PESO_IDADE: {
        acompanha: 'Ajuda a observar ganho de peso, reservas de energia e resposta à alimentação ao longo do tempo.',
        interpretacao: 'Uma medida isolada pode variar por hidratação, horário, roupa ou balança. A trajetória repetida é mais importante do que um ponto sozinho.',
        consulta: 'Converse com o pediatra se houver queda ou aumento persistente de faixa, dificuldade alimentar, vômitos frequentes ou perda de peso.'
      },
      COMPRIMENTO_IDADE: {
        acompanha: 'Mostra o crescimento linear, que costuma mudar mais devagar do que o peso e precisa ser visto em sequência.',
        interpretacao: 'Pequenas diferenças podem acontecer pela forma de medir. O mais importante é se a criança mantém uma linha de crescimento coerente.',
        consulta: 'Leve para avaliação se houver desaceleração repetida, diferença importante em relação às medidas anteriores ou dúvida sobre a técnica de medição.'
      },
      PERIMETRO_CEFALICO_IDADE: {
        acompanha: 'Acompanha o crescimento da cabeça, especialmente relevante nos primeiros anos de vida.',
        interpretacao: 'A leitura depende muito de medidas bem feitas e da evolução ao longo das consultas. Um ponto isolado não define diagnóstico.',
        consulta: 'Converse com o pediatra se houver mudança rápida de faixa, medida muito diferente da anterior ou preocupação associada ao desenvolvimento.'
      },
      PESO_COMPRIMENTO: {
        acompanha: 'Relaciona o peso ao comprimento da criança, sem depender diretamente da idade. A OMS usa este indicador do nascimento aos 2 anos.',
        interpretacao: 'Ajuda a observar se peso e comprimento estão proporcionais. A trajetória e a qualidade da medida são mais úteis do que um ponto isolado.',
        consulta: 'Converse com o pediatra se a proporção mudar de forma persistente ou se houver preocupação com alimentação, perda de peso ou ganho acelerado.'
      },
      IMC_IDADE: {
        acompanha: 'Relaciona peso e comprimento e compara o resultado com crianças da mesma idade e sexo nas curvas da OMS.',
        interpretacao: 'Na primeira infância, o IMC muda naturalmente com a idade. Por isso, o valor deve ser interpretado na curva, nunca como um número adulto.',
        consulta: 'Leve para a consulta mudanças persistentes de faixa. Este indicador não deve orientar restrição alimentar sem avaliação profissional.'
      }
    };
    return detalhes[indicador] ?? {
      acompanha: 'Ajuda a acompanhar o crescimento ao longo do tempo.',
      interpretacao: 'A trajetória costuma ser mais útil do que uma medida isolada.',
      consulta: 'Leve dúvidas ou mudanças persistentes para a consulta.'
    };
  }

  textoFamilia(resultado: ResultadoCurvaCrescimento): string {
    const referencia = resultado.indicador === 'PESO_COMPRIMENTO'
      ? 'para o comprimento'
      : 'para a idade';
    if (resultado.classificacao === 'FAIXA_ESPERADA') {
      return `Está dentro da faixa esperada ${referencia}.`;
    }
    if (resultado.classificacao === 'ABAIXO' || resultado.classificacao === 'MUITO_ABAIXO') {
      return `Ficou abaixo da faixa esperada ${referencia}.`;
    }
    return `Ficou acima da faixa esperada ${referencia}.`;
  }

  private situacaoCurva(classificacao: ClassificacaoCurvaCrescimento): SituacaoCurva {
    if (classificacao === 'FAIXA_ESPERADA') {
      return 'esperada';
    }
    return classificacao === 'ABAIXO' || classificacao === 'MUITO_ABAIXO' ? 'abaixo' : 'acima';
  }

  private corSituacao(classificacao: ClassificacaoCurvaCrescimento): string {
    const cores: Record<SituacaoCurva, string> = {
      esperada: '#3d794d',
      abaixo: '#9a631d',
      acima: '#a63d5a'
    };
    return cores[this.situacaoCurva(classificacao)];
  }

  textoContextoIdade(avaliacao: AvaliacaoCurvaCrescimento | null): string {
    if (!avaliacao) {
      return '';
    }
    if (avaliacao.criterioIdade.startsWith('INTERGROWTH')) {
      return `Idade corrigida nesta avaliação: ${this.formatarIdade(Math.max(0, avaliacao.idadeDias - 40 * 7))}.`;
    }
    if (avaliacao.idadeCorrigida) {
      return `Idade corrigida usada nesta avaliação: ${this.formatarIdade(avaliacao.idadeDias)}.`;
    }
    return '';
  }

  private formatarIdade(dias: number): string {
    const meses = Math.floor(dias / 30);
    const diasRestantes = dias % 30;
    if (meses === 0) return `${diasRestantes} dias`;
    return `${meses} ${meses === 1 ? 'mês' : 'meses'}${diasRestantes > 0 ? ` e ${diasRestantes} ${diasRestantes === 1 ? 'dia' : 'dias'}` : ''}`;
  }

  formatarData(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`));
  }

  formatarNumero(valor?: number | null, unidade = ''): string {
    if (valor === null || valor === undefined) {
      return 'Não informado';
    }
    return `${new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(valor)}${unidade}`;
  }

  formatarEntradaDecimal(valor?: number | null): string {
    if (valor === null || valor === undefined) {
      return '';
    }
    return new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(valor);
  }

  formatarEntradaData(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  labelOrigem(origem: OrigemMedidaCrescimento): string {
      const labels: Record<OrigemMedidaCrescimento, string> = {
        CASA: 'Casa',
        CONSULTA: 'Consulta',
        CONSULTORIO: 'Consultório',
        POSTO_SAUDE: 'Posto de saúde',
        HOSPITAL: 'Hospital',
        OUTRO: 'Outro'
      };
    return labels[origem];
  }

  comparar(campo: 'pesoKg' | 'comprimentoCm' | 'perimetroCefalicoCm', unidade: string): string {
    const atual = this.ultimaMedida()?.[campo];
    const anterior = this.medidaAnterior()?.[campo];
    if (atual === null || atual === undefined) {
      return 'Sem medida recente';
    }
    if (anterior === null || anterior === undefined) {
      return 'Primeira medida registrada';
    }

    const diferenca = Number((atual - anterior).toFixed(2));
    if (diferenca === 0) {
      return 'Sem mudança desde o registro anterior';
    }

    const direcao = diferenca > 0 ? 'Aumentou' : 'Reduziu';
    return `${direcao} ${this.formatarNumero(Math.abs(diferenca), unidade)} desde o registro anterior`;
  }

  private compararMedidasRecentes(a: MedidaCrescimento, b: MedidaCrescimento): number {
    const porDataMedicao = b.dataMedicao.localeCompare(a.dataMedicao);
    if (porDataMedicao !== 0) {
      return porDataMedicao;
    }

    const momentoA = a.atualizadoEm ?? a.criadoEm;
    const momentoB = b.atualizadoEm ?? b.criadoEm;
    return momentoB.localeCompare(momentoA);
  }

  private montarGraficosCurva(): GraficoCrescimento[] {
    const indicadores = [
      'PESO_IDADE',
      'COMPRIMENTO_IDADE',
      'PERIMETRO_CEFALICO_IDADE',
      'PESO_COMPRIMENTO',
      'IMC_IDADE'
    ];

    return indicadores
      .map((indicador) => this.montarGrafico(indicador))
      .filter((grafico): grafico is GraficoCrescimento => grafico !== null);
  }

  private montarGrafico(indicador: string): GraficoCrescimento | null {
    const resultados = this.avaliacoesCurva()
      .flatMap((avaliacao) => avaliacao.resultados
        .filter((resultado) => resultado.indicador === indicador)
        .map((resultado) => ({ avaliacao, resultado }))
      )
      .sort((a, b) => a.avaliacao.idadeDias - b.avaliacao.idadeDias);

    if (resultados.length === 0) {
      return null;
    }

    const zMin = -3.5;
    const zMax = 3.5;
    const larguraGrafico = 320;
    const alturaGrafico = 160;
    const margemGrafico = 22;
    const posicaoPorZ = (zScore: number) => ((Math.max(zMin, Math.min(zMax, zScore)) - zMin) / (zMax - zMin)) * 100;
    const yPorZ = (zScore: number) => {
      const zLimitado = Math.max(zMin, Math.min(zMax, zScore));
      const areaUtil = alturaGrafico - margemGrafico * 2;
      return margemGrafico + ((zMax - zLimitado) / (zMax - zMin)) * areaUtil;
    };
    const xPorIndice = (indice: number, total: number) => {
      if (total === 1) {
        return larguraGrafico / 2;
      }
      const areaUtil = larguraGrafico - margemGrafico * 2;
      return margemGrafico + (indice / (total - 1)) * areaUtil;
    };

    const pontos = resultados.map(({ avaliacao, resultado }, indice) => {
      return {
        label: this.formatarData(avaliacao.dataMedicao),
        valor: `${this.formatarNumero(resultado.valor, ` ${resultado.unidade}`)}`,
        zScore: resultado.zScore,
        percentil: resultado.percentil,
        x: Number(xPorIndice(indice, resultados.length).toFixed(2)),
        y: Number(yPorZ(resultado.zScore).toFixed(2)),
        cor: this.corSituacao(resultado.classificacao)
      };
    });

    const itemRecente = resultados.at(-1);
    if (!itemRecente) {
      return null;
    }
    const recente = itemRecente.resultado;

    return {
      indicador,
      ...this.apresentacaoIndicador(indicador),
      titulo: this.tituloIndicador(indicador),
      resumo: this.textoFamilia(recente),
      classe: this.classeResultado(recente),
      situacao: this.situacaoCurva(recente.classificacao),
      corTrajetoria: this.corSituacao(recente.classificacao),
      valorInicial: pontos[0]?.valor ?? '',
      dataInicial: pontos[0]?.label ?? '',
      valorAtual: `${this.formatarNumero(recente.valor, ` ${recente.unidade}`)}`,
      dataAtual: pontos.at(-1)?.label ?? '',
      marcador: Number(posicaoPorZ(recente.zScore).toFixed(2)),
      pontos,
      linhaTrajetoria: pontos.map((ponto) => `${ponto.x},${ponto.y}`).join(' '),
      ariaGrafico: `Trajetória de ${this.tituloIndicador(indicador)} na curva OMS com ${pontos.length} medida${pontos.length === 1 ? '' : 's'}.`,
      tecnico: {
        percentil: this.formatarPercentil(recente.percentil),
        zScore: this.formatarZScore(recente.zScore),
        classificacao: recente.classificacaoTitulo,
        fonte: recente.fonte,
        criterioIdade: this.textoCriterioIdade(itemRecente.avaliacao)
      }
    };
  }

  private textoCriterioIdade(avaliacao: AvaliacaoCurvaCrescimento): string {
    const contexto = this.textoContextoIdade(avaliacao);
    return contexto || 'Idade cronológica na data da medição.';
  }

  private tituloIndicador(indicador: string): string {
    const titulos: Record<string, string> = {
      PESO_IDADE: 'Peso',
      COMPRIMENTO_IDADE: 'Comprimento/estatura',
      PERIMETRO_CEFALICO_IDADE: 'Perímetro cefálico',
      PESO_COMPRIMENTO: 'Peso por comprimento',
      IMC_IDADE: 'IMC por idade'
    };
    return titulos[indicador] ?? indicador;
  }

  private apresentacaoIndicador(indicador: string): Pick<GraficoCrescimento, 'icone' | 'tema'> {
    const apresentacoes: Record<string, Pick<GraficoCrescimento, 'icone' | 'tema'>> = {
      PESO_IDADE: { icone: 'chart', tema: 'peso' },
      COMPRIMENTO_IDADE: { icone: 'ruler', tema: 'comprimento' },
      PERIMETRO_CEFALICO_IDADE: { icone: 'brain', tema: 'perimetro' },
      PESO_COMPRIMENTO: { icone: 'heartPulse', tema: 'proporcao' },
      IMC_IDADE: { icone: 'sparkles', tema: 'imc' }
    };
    return apresentacoes[indicador] ?? { icone: 'chart', tema: 'peso' };
  }

  private recarregarCurvas(): void {
    const criancaId = this.crianca()?.id;
    if (!criancaId) {
      return;
    }

    this.crescimentoService.listarCurvas(criancaId)
      .subscribe({
        next: (curvas) => this.avaliacoesCurva.set(curvas),
        error: () => this.aviso.set('Medida salva, mas as curvas OMS não foram atualizadas agora.')
      });
  }

  private criarRequest(): SalvarMedidaCrescimentoRequest | null {
    const valor = this.form.getRawValue();
    const request: SalvarMedidaCrescimentoRequest = {
      dataMedicao: this.lerDataMedicao(valor.dataMedicao),
      pesoKg: this.lerMedida(valor.pesoKg, 'peso', 0.3, 80),
      comprimentoCm: this.lerMedida(valor.comprimentoCm, 'comprimento ou estatura', 20, 140),
      perimetroCefalicoCm: this.lerMedida(valor.perimetroCefalicoCm, 'perímetro cefálico', 20, 65),
      origem: valor.origem,
      responsavelMedicao: valor.responsavelMedicao,
      observacao: valor.observacao?.trim() || null
    };

    const possuiMedida = request.pesoKg !== null || request.comprimentoCm !== null || request.perimetroCefalicoCm !== null;
    return possuiMedida ? request : null;
  }

  private lerDataMedicao(valor: string | null | undefined): string {
    const texto = (valor ?? '').trim();
    const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(texto);
    if (!partes) {
      throw new Error('Informe a data da medição no formato dd/mm/aaaa.');
    }

    const dia = Number(partes[1]);
    const mes = Number(partes[2]);
    const ano = Number(partes[3]);
    const data = new Date(Date.UTC(ano, mes - 1, dia));
    const dataExiste = data.getUTCFullYear() === ano && data.getUTCMonth() === mes - 1 && data.getUTCDate() === dia;
    if (!dataExiste) {
      throw new Error('Informe uma data da medição válida.');
    }

    const iso = `${ano.toString().padStart(4, '0')}-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}`;
    if (iso > this.dataMaximaIso) {
      throw new Error('A data da medição não pode estar no futuro.');
    }
    return iso;
  }

  private lerMedida(valor: string | null | undefined, label: string, minimo: number, maximo: number): number | null {
    const texto = (valor ?? '').trim();
    if (!texto) {
      return null;
    }

    const normalizado = texto.replace(',', '.');
    if (!/^\d+(\.\d{1,2})?$/.test(normalizado)) {
      throw new Error(`Informe ${label} usando números, vírgula ou ponto decimal.`);
    }

    const numero = Number(normalizado);
    if (!Number.isFinite(numero) || numero < minimo || numero > maximo) {
      throw new Error(`A medida de ${label} está fora do limite esperado.`);
    }
    return numero;
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar o crescimento agora.';
  }
}
