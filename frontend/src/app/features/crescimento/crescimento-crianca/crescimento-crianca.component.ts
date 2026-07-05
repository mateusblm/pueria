import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { CriancasService } from '../../criancas/criancas.service';
import { Crianca } from '../../../shared/models/crianca.model';
import { AvaliacaoCurvaCrescimento, MedidaCrescimento, OrigemMedidaCrescimento, ResultadoCurvaCrescimento, SalvarMedidaCrescimentoRequest } from '../../../shared/models/crescimento.model';
import { CrescimentoService } from '../crescimento.service';

type PontoGraficoCrescimento = {
  label: string;
  valor: string;
  zScore: number;
  percentil: number;
};

type GraficoCrescimento = {
  indicador: string;
  titulo: string;
  resumo: string;
  classe: string;
  valorAtual: string;
  dataAtual: string;
  idadeLegenda: string;
  idadeCorrigida: boolean;
  marcador: number;
  pontos: PontoGraficoCrescimento[];
  tendencia: string;
  orientacao: string;
  tecnico: {
    percentil: string;
    zScore: string;
    classificacao: string;
    idadeUsada: string;
    idadeCronologica: string;
    criterioIdade: string;
    fonte: string;
  };
};

@Component({
  selector: 'app-crescimento-crianca',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './crescimento-crianca.component.html',
  styleUrl: './crescimento-crianca.component.scss'
})
export class CrescimentoCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly crescimentoService = inject(CrescimentoService);

  readonly crianca = signal<Crianca | null>(null);
  readonly medidas = signal<MedidaCrescimento[]>([]);
  readonly avaliacoesCurva = signal<AvaliacaoCurvaCrescimento[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly removendoId = signal('');
  readonly confirmandoRemocaoId = signal('');
  readonly editandoId = signal('');
  readonly erro = signal('');
  readonly aviso = signal('');
  readonly detalhesTecnicosAbertos = signal<Set<string>>(new Set());
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly form = this.fb.group({
    dataMedicao: ['', Validators.required],
    pesoKg: [''],
    comprimentoCm: [''],
    perimetroCefalicoCm: [''],
    origem: this.fb.nonNullable.control<OrigemMedidaCrescimento>('CONSULTA', Validators.required),
    observacao: ['', Validators.maxLength(500)]
  });

  readonly medidasOrdenadas = computed(() =>
    [...this.medidas()].sort((a, b) => b.dataMedicao.localeCompare(a.dataMedicao))
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
  readonly graficosCurva = computed(() => this.montarGraficosCurva());

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
    this.form.patchValue({
      dataMedicao: this.formatarEntradaData(medida.dataMedicao),
      pesoKg: this.formatarEntradaDecimal(medida.pesoKg),
      comprimentoCm: this.formatarEntradaDecimal(medida.comprimentoCm),
      perimetroCefalicoCm: this.formatarEntradaDecimal(medida.perimetroCefalicoCm),
      origem: medida.origem,
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
      origem: 'CONSULTA',
      observacao: ''
    });
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
    return resultado.classificacao === 'FAIXA_ESPERADA' ? 'crescimento-curva__badge--ok' : 'crescimento-curva__badge--atencao';
  }

  formatarZScore(valor: number): string {
    const sinal = valor > 0 ? '+' : '';
    return `${sinal}${new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(valor)} DP`;
  }

  formatarPercentil(valor: number): string {
    return `P${new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 1 }).format(valor)}`;
  }

  detalhesTecnicosAberto(indicador: string): boolean {
    return this.detalhesTecnicosAbertos().has(indicador);
  }

  alternarDetalhesTecnicos(indicador: string): void {
    this.detalhesTecnicosAbertos.update((atuais) => {
      const proximos = new Set(atuais);
      if (proximos.has(indicador)) {
        proximos.delete(indicador);
      } else {
        proximos.add(indicador);
      }
      return proximos;
    });
  }

  textoFamilia(resultado: ResultadoCurvaCrescimento): string {
    if (resultado.classificacao === 'FAIXA_ESPERADA') {
      return 'Está dentro da faixa esperada para a idade.';
    }
    if (resultado.classificacao === 'ABAIXO' || resultado.classificacao === 'MUITO_ABAIXO') {
      return 'Ficou abaixo da faixa esperada para a idade.';
    }
    return 'Ficou acima da faixa esperada para a idade.';
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
      ESCOLA_CRECHE: 'Escola ou creche',
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

  private montarGraficosCurva(): GraficoCrescimento[] {
    const indicadores = [
      'PESO_IDADE',
      'COMPRIMENTO_IDADE',
      'PERIMETRO_CEFALICO_IDADE'
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
    const posicaoPorZ = (zScore: number) => ((Math.max(zMin, Math.min(zMax, zScore)) - zMin) / (zMax - zMin)) * 100;

    const pontos = resultados.map(({ avaliacao, resultado }) => {
      return {
        label: this.formatarData(avaliacao.dataMedicao),
        valor: `${this.formatarNumero(resultado.valor, ` ${resultado.unidade}`)}`,
        zScore: resultado.zScore,
        percentil: resultado.percentil
      };
    });

    const itemRecente = resultados.at(-1);
    if (!itemRecente) {
      return null;
    }
    const recente = itemRecente.resultado;
    const tendencia = this.descreverTendencia(pontos);

    return {
      indicador,
      titulo: this.tituloIndicador(indicador),
      resumo: this.textoFamilia(recente),
      classe: this.classeResultado(recente),
      valorAtual: `${this.formatarNumero(recente.valor, ` ${recente.unidade}`)}`,
      dataAtual: pontos.at(-1)?.label ?? '',
      idadeLegenda: this.idadeDaCurvaTexto(itemRecente.avaliacao),
      idadeCorrigida: itemRecente.avaliacao.idadeCorrigida,
      marcador: Number(posicaoPorZ(recente.zScore).toFixed(2)),
      pontos,
      tendencia: tendencia.texto,
      orientacao: this.orientacaoGrafico(recente),
      tecnico: {
        percentil: this.formatarPercentil(recente.percentil),
        zScore: this.formatarZScore(recente.zScore),
        classificacao: recente.classificacaoTitulo,
        idadeUsada: this.formatarIdadeDias(itemRecente.avaliacao.idadeDias),
        idadeCronologica: this.formatarIdadeDias(itemRecente.avaliacao.idadeCronologicaDias),
        criterioIdade: itemRecente.avaliacao.criterioIdade,
        fonte: recente.fonte
      }
    };
  }

  private descreverTendencia(pontos: PontoGraficoCrescimento[]): { texto: string } {
    if (pontos.length < 2) {
      return {
        texto: 'Com a próxima medida, a evolução da curva começa a ficar mais clara.'
      };
    }

    const anterior = pontos[pontos.length - 2];
    const atual = pontos[pontos.length - 1];
    const diferencaZ = atual.zScore - anterior.zScore;
    const variacaoPercentil = atual.percentil - anterior.percentil;

    if (Math.abs(diferencaZ) < 0.35) {
      return {
        texto: 'Segue em trajetória parecida com a medida anterior.'
      };
    }

    const intensidade = Math.abs(diferencaZ) >= 0.67 ? 'Mudança importante' : 'Pequena mudança';
    const direcao = diferencaZ > 0 ? 'para uma faixa mais alta' : 'para uma faixa mais baixa';
    const pontosPercentil = Math.round(Math.abs(variacaoPercentil));
    const detalhePercentil = pontosPercentil >= 1
      ? `, com variação de ${pontosPercentil} ponto${pontosPercentil === 1 ? '' : 's'} na posição da curva`
      : '';

    return {
      texto: `${intensidade} ${direcao} em relação à medida anterior${detalhePercentil}.`
    };
  }

  private idadeDaCurvaTexto(avaliacao: AvaliacaoCurvaCrescimento): string {
    if (!avaliacao.idadeCorrigida) {
      return `Idade na curva: ${this.formatarIdadeDias(avaliacao.idadeDias)}`;
    }
    return `Idade corrigida usada na curva: ${this.formatarIdadeDias(avaliacao.idadeDias)}`;
  }

  private formatarIdadeDias(dias: number): string {
    if (dias < 30) {
      return `${dias} dia${dias === 1 ? '' : 's'}`;
    }

    const meses = Math.floor(dias / 30);
    const diasRestantes = dias % 30;
    if (meses < 24) {
      return diasRestantes > 0
        ? `${meses} mes${meses === 1 ? '' : 'es'} e ${diasRestantes} dia${diasRestantes === 1 ? '' : 's'}`
        : `${meses} mes${meses === 1 ? '' : 'es'}`;
    }

    const anos = Math.floor(meses / 12);
    const mesesRestantes = meses % 12;
    return mesesRestantes > 0
      ? `${anos} ano${anos === 1 ? '' : 's'} e ${mesesRestantes} mes${mesesRestantes === 1 ? '' : 'es'}`
      : `${anos} ano${anos === 1 ? '' : 's'}`;
  }

  private orientacaoGrafico(resultado: ResultadoCurvaCrescimento): string {
    if (resultado.classificacao === 'FAIXA_ESPERADA') {
      return 'Continue acompanhando nas próximas medidas.';
    }
    return 'Vale levar esse ponto para a próxima consulta.';
  }

  private tituloIndicador(indicador: string): string {
    const titulos: Record<string, string> = {
      PESO_IDADE: 'Peso',
      COMPRIMENTO_IDADE: 'Comprimento/estatura',
      PERIMETRO_CEFALICO_IDADE: 'Perímetro cefálico'
    };
    return titulos[indicador] ?? indicador;
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
