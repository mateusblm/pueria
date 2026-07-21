import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, finalize, forkJoin, map, of, switchMap } from 'rxjs';
import { Crianca, Parentesco, Sexo, TipoParto } from '../../shared/models/crianca.model';
import { EstimuloDesenvolvimento, EventoTrajetoriaDesenvolvimento, MarcoDesenvolvimento, RelatoDesenvolvimento } from '../../shared/models/desenvolvimento.model';
import { CriancasService } from '../criancas/criancas.service';
import { DesenvolvimentoService, ResumoHomeDesenvolvimento } from '../desenvolvimento/desenvolvimento.service';
import { AppIconComponent, AppIconName } from '../../shared/components/app-icon/app-icon.component';

type ResumoCrianca = {
  crianca: Crianca;
  marcos: MarcoDesenvolvimento[];
  estimulos: EstimuloDesenvolvimento[];
  relatos: RelatoDesenvolvimento[];
  trajetoria: EventoTrajetoriaDesenvolvimento[];
  resumoHome: ResumoHomeDesenvolvimento;
  erro?: string;
};

type AtalhoCuidado = {
  titulo: string;
  detalhe: string;
  rota: string[];
  destaque?: boolean;
  icone: AppIconName;
  tema?: string;
};

@Component({
  selector: 'app-acompanhamento',
  imports: [RouterLink, ReactiveFormsModule, AppIconComponent],
  templateUrl: './acompanhamento.component.html',
  styleUrl: './acompanhamento.component.scss'
})
export class AcompanhamentoComponent implements OnInit {
  private readonly criancasService = inject(CriancasService);
  private readonly desenvolvimentoService = inject(DesenvolvimentoService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);

  readonly resumos = signal<ResumoCrianca[]>([]);
  readonly carregando = signal(true);
  readonly salvandoCrianca = signal(false);
  readonly cadastroInicialAberto = signal(false);
  readonly tutorialPrimeiroAcompanhamentoAberto = signal(false);
  readonly criancaDoPrimeiroAcompanhamento = signal<Crianca | null>(null);
  readonly etapaCadastro = signal(1);
  readonly erro = signal('');
  readonly erroCadastro = signal('');
  readonly criancaEmFocoId = signal<string | null>(this.lerCriancaEmFocoSalva());
  readonly mensagemHomePorEstado: Record<ResumoHomeDesenvolvimento['estado'], string> = {
    INICIAL: 'Aos poucos você monta o retrato dele. Não precisa preencher tudo hoje — comece pelo que fizer sentido agora.',
    ATENCAO: 'O acompanhamento dele está em dia. Há um ponto no desenvolvimento que vale levar ao pediatra — sem pressa, com calma.',
    TRANQUILO: 'Está tudo tranquilo por aqui. Nenhum ponto pede atenção agora — continue acompanhando no seu ritmo.'
  };

  readonly possuiCriancas = computed(() => this.resumos().length > 0);
  readonly criancaEmFoco = computed(() => {
    const resumos = this.resumos();
    const id = this.criancaEmFocoId();
    return (resumos.find((resumo) => resumo.crianca.id === id) ?? this.escolherResumoInicial(resumos)) as ResumoCrianca;
  });
  readonly dataMaximaNascimento = this.formatarDataInput(new Date());
  readonly dataMinimaNascimento = this.calcularDataMinimaNascimento();

  readonly sexos: { label: string; value: Sexo }[] = [
    { label: 'Feminino', value: 'FEMININO' },
    { label: 'Masculino', value: 'MASCULINO' },
    { label: 'Não informar', value: 'NAO_INFORMADO' }
  ];

  readonly parentescos: { label: string; value: Parentesco }[] = [
    { label: 'Mãe', value: 'MAE' },
    { label: 'Pai', value: 'PAI' },
    { label: 'Responsável legal', value: 'RESPONSAVEL_LEGAL' },
    { label: 'Avó/avô', value: 'AVO' },
    { label: 'Outro', value: 'OUTRO' }
  ];

  readonly formCadastro = this.formBuilder.group({
    nome: this.formBuilder.nonNullable.control('', [Validators.required, Validators.maxLength(150)]),
    dataNascimento: this.formBuilder.nonNullable.control('', [Validators.required]),
    sexo: this.formBuilder.nonNullable.control<Sexo>('NAO_INFORMADO'),
    parentesco: this.formBuilder.nonNullable.control<Parentesco>('MAE', [Validators.required]),
    semanasGestacionais: this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(22), Validators.max(42)]),
    diasGestacionais: this.formBuilder.control<number | null>(0, [Validators.required, Validators.min(0), Validators.max(6)]),
    prematura: this.formBuilder.nonNullable.control(false),
    pesoNascimentoGramas: this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(300), Validators.max(7000)]),
    comprimentoNascimentoCm: this.formBuilder.nonNullable.control('', [Validators.required]),
    perimetroCefalicoNascimentoCm: this.formBuilder.nonNullable.control('', [Validators.required]),
    aceiteConsentimento: this.formBuilder.nonNullable.control(false, [Validators.requiredTrue])
  });

  ngOnInit(): void {
    const criancaId = this.route.snapshot.queryParamMap.get('crianca');
    if (criancaId) {
      this.criancaEmFocoId.set(criancaId);
      localStorage.setItem('pueria.criancaEmFocoId', criancaId);
    }
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set('');

    this.criancasService.listar()
      .pipe(
        switchMap((criancas) => {
          if (criancas.length === 0) {
            return of([]);
          }

          return forkJoin(criancas.map((crianca) =>
            forkJoin({
              marcos: this.desenvolvimentoService.listarMarcos(crianca.id),
              estimulos: this.desenvolvimentoService.listarRecomendacoes(crianca.id).pipe(catchError(() => of([]))),
              relatos: this.desenvolvimentoService.listarRelatos(crianca.id).pipe(catchError(() => of([]))),
              trajetoria: this.desenvolvimentoService.listarTrajetoria(crianca.id).pipe(catchError(() => of([]))),
              resumoHome: this.desenvolvimentoService.resumoHome(crianca.id)
            }).pipe(
              map(({ marcos, estimulos, relatos, trajetoria, resumoHome }) => ({ crianca, marcos, estimulos, relatos, trajetoria, resumoHome })),
              catchError(() => of({ crianca, marcos: [], estimulos: [], relatos: [], trajetoria: [], resumoHome: { estado: 'INICIAL' as const, total: 0, respondidos: 0, pontosAtencao: 0, temPerdaHabilidade: false }, erro: 'Não foi possível carregar o desenvolvimento agora.' }))
            )
          ));
        }),
        finalize(() => this.carregando.set(false))
      )
      .subscribe({
        next: (resumos) => {
          this.resumos.set(resumos);
          if (resumos.length === 0) {
            this.abrirCadastroInicial();
          }
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  abrirCadastroInicial(): void {
    this.erroCadastro.set('');
    this.etapaCadastro.set(1);
    this.cadastroInicialAberto.set(true);
  }

  fecharCadastroInicial(): void {
    this.cadastroInicialAberto.set(false);
    this.erroCadastro.set('');
  }

  fecharTutorialPrimeiroAcompanhamento(): void {
    this.tutorialPrimeiroAcompanhamentoAberto.set(false);
  }

  avancarCadastro(): void {
    const erro = this.validarEtapa(this.etapaCadastro());
    if (erro) {
      this.erroCadastro.set(erro);
      return;
    }

    this.erroCadastro.set('');
    this.etapaCadastro.update((etapa) => Math.min(etapa + 1, 3));
  }

  voltarCadastro(): void {
    this.erroCadastro.set('');
    this.etapaCadastro.update((etapa) => Math.max(etapa - 1, 1));
  }

  formatarDataNascimento(evento: Event): void {
    const campo = evento.target as HTMLInputElement;
    const digitos = campo.value.replace(/\D/g, '').slice(0, 8);
    const partes = [
      digitos.slice(0, 2),
      digitos.slice(2, 4),
      digitos.slice(4, 8)
    ].filter(Boolean);
    const valorFormatado = partes.join('/');

    campo.value = valorFormatado;
    this.formCadastro.controls.dataNascimento.setValue(valorFormatado, { emitEvent: false });
  }

  cadastrarPrimeiraCrianca(): void {
    this.erroCadastro.set('');
    const erro = this.validarEtapa(1) || this.validarEtapa(2) || this.validarEtapa(3);
    if (erro) {
      this.erroCadastro.set(erro);
      return;
    }

    const valor = this.formCadastro.getRawValue();
    const dataNascimento = this.converterDataBrasileiraParaIso(valor.dataNascimento);
    const comprimento = this.lerDecimal(valor.comprimentoNascimentoCm, 'comprimento ao nascer', 20, 70);
    const perimetro = this.lerDecimal(valor.perimetroCefalicoNascimentoCm, 'perímetro cefálico ao nascer', 20, 50);
    const erroDecimal = this.extrairErroDecimal(comprimento, perimetro);
    if (erroDecimal) {
      this.erroCadastro.set(erroDecimal);
      return;
    }

    this.salvandoCrianca.set(true);
    this.criancasService.criar({
      nome: valor.nome.trim(),
      dataNascimento: dataNascimento as string,
      sexo: valor.sexo,
      prematura: valor.prematura,
      semanasGestacionais: valor.semanasGestacionais as number,
      diasGestacionais: valor.diasGestacionais as number,
      tipoParto: 'NAO_INFORMADO' as TipoParto,
      pesoNascimentoGramas: valor.pesoNascimentoGramas as number,
      comprimentoNascimentoCm: comprimento as number,
      perimetroCefalicoNascimentoCm: perimetro as number,
      apgarUmMinuto: null,
      apgarCincoMinutos: null,
      utiNeonatal: false,
      reanimacaoNeonatal: false,
      ictericiaNeonatal: false,
      dificuldadeRespiratoria: false,
      dificuldadeAmamentacao: false,
      observacoesNascimento: null,
      preNatalRealizado: false,
      consultasPreNatal: null,
      diabetesGestacional: false,
      hipertensaoGestacional: false,
      infeccaoGestacional: false,
      sangramentoGestacional: false,
      usoAlcoolGestacao: false,
      usoTabacoGestacao: false,
      outrasExposicoesGestacao: false,
      observacoesGestacao: null,
      diasAltaHospitalar: null,
      retornoHospitalarPrimeiraSemana: false,
      testePezinho: 'NAO_INFORMADO',
      testeOrelhinha: 'NAO_INFORMADO',
      testeOlhinho: 'NAO_INFORMADO',
      testeCoracaozinho: 'NAO_INFORMADO',
      amamentacaoPrimeiraHora: false,
      alimentacaoInicial: 'NAO_INFORMADO',
      parentesco: valor.parentesco,
      aceiteConsentimento: valor.aceiteConsentimento,
      versaoTermoConsentimento: '2026.07'
    })
      .pipe(finalize(() => this.salvandoCrianca.set(false)))
      .subscribe({
        next: (crianca) => {
          this.cadastroInicialAberto.set(false);
          this.criancaEmFocoId.set(crianca.id);
          localStorage.setItem('pueria.criancaEmFocoId', crianca.id);
          this.criancaDoPrimeiroAcompanhamento.set(crianca);
          this.tutorialPrimeiroAcompanhamentoAberto.set(true);
          this.carregar();
        },
        error: (erro: HttpErrorResponse) => this.erroCadastro.set(this.extrairMensagemErro(erro))
      });
  }

  idadeReferencia(marcos: MarcoDesenvolvimento[]): string {
    const idade = marcos.at(-1)?.idadeMeses;
    return idade === undefined ? 'Sem faixa disponível' : this.tituloIdade(idade);
  }

  progresso(marcos: MarcoDesenvolvimento[]): { total: number; respondidos: number; percentual: number } {
    const idade = marcos.at(-1)?.idadeMeses;
    const marcosDaIdade = idade === undefined ? [] : marcos.filter((marco) => marco.idadeMeses === idade);
    const total = marcosDaIdade.length;
    const respondidos = marcosDaIdade.filter((marco) => marco.status !== 'NAO_AVALIADO').length;

    return { total, respondidos, percentual: total === 0 ? 0 : Math.round((respondidos / total) * 100) };
  }

  pontosAtencao(marcos: MarcoDesenvolvimento[]): number {
    const idade = marcos.at(-1)?.idadeMeses;
    if (idade === undefined) {
      return 0;
    }
    return marcos.filter((marco) =>
      marco.idadeMeses === idade && (marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA')
    ).length;
  }

  experimentosRecomendados(resumo: ResumoCrianca): EstimuloDesenvolvimento[] {
    return resumo.estimulos.slice(0, 3);
  }

  labelPontosAtencao(total: number): string {
    if (total === 0) {
      return 'Sem dúvidas para a consulta';
    }
    return total === 1 ? '1 dúvida para a consulta' : `${total} dúvidas para a consulta`;
  }

  relatosParaConversa(relatos: RelatoDesenvolvimento[]): RelatoDesenvolvimento[] {
    return relatos.slice(0, 3);
  }

  existePerdaHabilidade(relatos: RelatoDesenvolvimento[]): boolean {
    return relatos.some((relato) => relato.tipo === 'PERDA_HABILIDADE');
  }

  tituloRelato(relato: RelatoDesenvolvimento): string {
    return relato.tipo === 'PERDA_HABILIDADE' ? 'Perda de habilidade registrada' : 'Preocupação da família';
  }

  textoTrajetoria(evento: EventoTrajetoriaDesenvolvimento): string {
    if (evento.tipo === 'OBSERVADO_NOVAMENTE') {
      return 'Observado novamente';
    }
    return evento.tipo === 'NOVA_OBSERVACAO' ? 'Passou a ser observado' : 'Primeira observação registrada';
  }

  formatarDataRegistro(data: string): string {
    return new Intl.DateTimeFormat('pt-BR').format(new Date(data));
  }

  mensagemDoDia(resumo: ResumoCrianca): string {
    if (resumo.erro) {
      return 'Não conseguimos atualizar os marcos agora, mas os outros registros continuam disponíveis.';
    }

    const progresso = this.progresso(resumo.marcos);
    const pontos = this.pontosAtencao(resumo.marcos);

    if (progresso.total === 0) {
      return 'O perfil já está criado. O próximo passo é fazer os primeiros registros.';
    }
    if (pontos > 0) {
      return 'Há respostas que merecem ser observadas com calma e levadas para a próxima conversa com o pediatra.';
    }
    if (progresso.percentual < 100) {
      return 'Continue pelos marcos da idade atual para deixar a leitura do desenvolvimento mais completa.';
    }
    return 'Os marcos da idade atual foram preenchidos. Mantenha os registros de rotina para acompanhar a evolução.';
  }

  textoAcaoPrincipal(resumo: ResumoCrianca): string {
    if (resumo.erro) {
      return 'Ver perfil';
    }
    return this.progresso(resumo.marcos).percentual < 100 ? 'Continuar marcos' : 'Ver desenvolvimento';
  }

  rotaAcaoPrincipal(resumo: ResumoCrianca): string[] {
    if (resumo.erro) {
      return ['/criancas', resumo.crianca.id];
    }
    return ['/criancas', resumo.crianca.id, 'desenvolvimento'];
  }

  proximosPassos(resumo: ResumoCrianca): AtalhoCuidado[] {
    const progresso = this.progresso(resumo.marcos);
    const pontos = this.pontosAtencao(resumo.marcos);
    const passos: AtalhoCuidado[] = [];

    if (resumo.erro) {
      return [{
        titulo: 'Abrir perfil',
        detalhe: 'Confira os dados da criança enquanto o desenvolvimento não carrega.',
        rota: ['/criancas', resumo.crianca.id],
        destaque: true,
        icone: 'user'
      }];
    }

    if (progresso.total === 0 || progresso.percentual < 100) {
      passos.push({
        titulo: 'Responder marcos da idade',
        detalhe: `${progresso.respondidos}/${progresso.total} respostas preenchidas na idade atual.`,
        rota: ['/criancas', resumo.crianca.id, 'desenvolvimento'],
        destaque: true,
        icone: 'brain'
      });
    }

    if (pontos > 0) {
      passos.push({
        titulo: 'Preparar conversa com o pediatra',
        detalhe: this.labelPontosAtencao(pontos),
        rota: ['/criancas', resumo.crianca.id, 'para-a-consulta'],
        destaque: true,
        icone: 'stethoscope'
      });
    }

    passos.push(
      {
        titulo: 'Atualizar crescimento',
        detalhe: 'Peso, altura e medida da cabeça ajudam a acompanhar o crescimento.',
        rota: ['/criancas', resumo.crianca.id, 'crescimento'],
        icone: 'chart'
      },
      {
        titulo: 'Registrar sono e rotina',
        detalhe: 'Sono, alimentação e telas ajudam a entender melhor a rotina da criança.',
        rota: ['/criancas', resumo.crianca.id, 'sono'],
        icone: 'moon'
      }
    );

    return passos.slice(0, 4);
  }

  modulosCuidado(resumo: ResumoCrianca): AtalhoCuidado[] {
    const progresso = this.progresso(resumo.marcos);

    return [
      {
        titulo: 'Neurodesenvolvimento',
        detalhe: progresso.total === 0
          ? 'Inicie os marcos da idade atual.'
          : `${progresso.percentual}% dos marcos da idade atual preenchidos.`,
        rota: ['/criancas', resumo.crianca.id, 'desenvolvimento'],
        destaque: progresso.percentual < 100,
        icone: 'brain',
        tema: 'desenvolvimento'
      },
      {
        titulo: 'Crescimento',
        detalhe: 'Veja se peso, altura e medida da cabeça seguem o esperado para a idade.',
        rota: ['/criancas', resumo.crianca.id, 'crescimento'],
        icone: 'chart',
        tema: 'crescimento'
      },
      {
        titulo: 'Alimentação',
        detalhe: 'Observe rotina, variedade alimentar e pontos úteis para a consulta.',
        rota: ['/criancas', resumo.crianca.id, 'alimentacao'],
        icone: 'salad',
        tema: 'alimentacao'
      },
      {
        titulo: 'Eliminações fisiológicas',
        detalhe: 'Registre fezes, diurese, assaduras e sinais que merecem observação.',
        rota: ['/criancas', resumo.crianca.id, 'transito-intestinal'],
        icone: 'toilet',
        tema: 'intestinal'
      },
      {
        titulo: 'Humor e comportamento',
        detalhe: 'Guarde uma visão gentil do humor, choro, interação e interesse em brincar.',
        rota: ['/criancas', resumo.crianca.id, 'humor-comportamento'],
        icone: 'heartPulse',
        tema: 'humor'
      },
      {
        titulo: 'Observações e eventos',
        detalhe: 'Registre mudanças e acontecimentos importantes para dar contexto à rotina.',
        rota: ['/criancas', resumo.crianca.id, 'observacoes-eventos'],
        icone: 'message',
        tema: 'observacoes'
      },
      {
        titulo: 'Sono',
        detalhe: 'Registre duração, qualidade e padrão de descanso em 24 horas.',
        rota: ['/criancas', resumo.crianca.id, 'sono'],
        icone: 'moon',
        tema: 'sono'
      },
      {
        titulo: 'Telas',
        detalhe: 'Acompanhe tempo, contexto de uso e oportunidades de ajuste.',
        rota: ['/criancas', resumo.crianca.id, 'telas'],
        icone: 'smartphone',
        tema: 'telas'
      },
      {
        titulo: 'Saúde e cuidados',
        detalhe: 'Registre suplementos de uso diário e intercorrências para lembrar na consulta.',
        rota: ['/criancas', resumo.crianca.id, 'saude'],
        icone: 'stethoscope',
        tema: 'saude'
      }
    ];
  }

  calcularIdade(dataNascimento: string): string {
    const nascimento = new Date(`${dataNascimento}T00:00:00`);
    const hoje = new Date();
    let anos = hoje.getFullYear() - nascimento.getFullYear();
    let meses = hoje.getMonth() - nascimento.getMonth();

    if (hoje.getDate() < nascimento.getDate()) {
      meses -= 1;
    }
    if (meses < 0) {
      anos -= 1;
      meses += 12;
    }
    if (anos <= 0) {
      return `${meses} ${meses === 1 ? 'mês' : 'meses'}`;
    }
    return `${anos} ${anos === 1 ? 'ano' : 'anos'} e ${meses} ${meses === 1 ? 'mês' : 'meses'}`;
  }

  artigoDaCrianca(sexo: Sexo | null): string {
    return sexo === 'MASCULINO' ? 'O' : 'A';
  }

  private escolherResumoInicial(resumos: ResumoCrianca[]): ResumoCrianca | undefined {
    return [...resumos].sort((a, b) => {
      const diferencaPrioridade = this.pontuacaoResumo(b) - this.pontuacaoResumo(a);
      if (diferencaPrioridade !== 0) {
        return diferencaPrioridade;
      }
      return a.crianca.nome.localeCompare(b.crianca.nome, 'pt-BR');
    })[0];
  }

  private pontuacaoResumo(resumo: ResumoCrianca): number {
    if (resumo.erro) {
      return 0;
    }

    const progresso = this.progresso(resumo.marcos);
    return (this.pontosAtencao(resumo.marcos) * 100)
      + (progresso.total === 0 ? 30 : 0)
      + (progresso.percentual < 100 ? 20 : 0);
  }

  private lerCriancaEmFocoSalva(): string | null {
    return localStorage.getItem('pueria.criancaEmFocoId');
  }

  private tituloIdade(idadeMeses: number): string {
    if (idadeMeses < 12) {
      return `${idadeMeses} meses`;
    }
    const anos = Math.floor(idadeMeses / 12);
    const meses = idadeMeses % 12;
    if (meses === 0) {
      return `${anos} ${anos === 1 ? 'ano' : 'anos'}`;
    }
    return `${anos} ${anos === 1 ? 'ano' : 'anos'} e ${meses} meses`;
  }

  private validarEtapa(etapa: number): string {
    if (etapa === 1) {
      this.formCadastro.controls.nome.markAsTouched();
      this.formCadastro.controls.dataNascimento.markAsTouched();
      if (this.formCadastro.controls.nome.invalid || this.formCadastro.controls.dataNascimento.invalid) {
        return 'Informe nome e data de nascimento para continuar.';
      }
      return this.validarRegraIdade();
    }

    if (etapa === 2) {
      this.formCadastro.controls.semanasGestacionais.markAsTouched();
      this.formCadastro.controls.diasGestacionais.markAsTouched();
      this.formCadastro.controls.pesoNascimentoGramas.markAsTouched();
      this.formCadastro.controls.comprimentoNascimentoCm.markAsTouched();
      this.formCadastro.controls.perimetroCefalicoNascimentoCm.markAsTouched();
      if (
        this.formCadastro.controls.semanasGestacionais.invalid
        || this.formCadastro.controls.diasGestacionais.invalid
        || this.formCadastro.controls.pesoNascimentoGramas.invalid
        || this.formCadastro.controls.comprimentoNascimentoCm.invalid
        || this.formCadastro.controls.perimetroCefalicoNascimentoCm.invalid
      ) {
        return 'Preencha os dados de nascimento para criar o primeiro acompanhamento.';
      }
      return this.validarRegraPrematuridade();
    }

    this.formCadastro.controls.parentesco.markAsTouched();
    this.formCadastro.controls.aceiteConsentimento.markAsTouched();
    if (this.formCadastro.controls.parentesco.invalid || this.formCadastro.controls.aceiteConsentimento.invalid) {
      return 'Confirme seu vínculo e a autorização para registrar o acompanhamento.';
    }
    return '';
  }

  private validarRegraPrematuridade(): string {
    const semanas = this.formCadastro.controls.semanasGestacionais.value;
    const prematura = this.formCadastro.controls.prematura.value;

    if (semanas == null) {
      return '';
    }
    if (semanas < 37 && !prematura) {
      return 'Se nasceu com menos de 37 semanas, marque que houve prematuridade.';
    }
    if (semanas >= 37 && prematura) {
      return 'Prematuridade deve ser marcada quando o nascimento ocorreu antes de 37 semanas.';
    }
    return '';
  }

  private validarRegraIdade(): string {
    const dataNascimento = this.converterDataBrasileiraParaIso(this.formCadastro.controls.dataNascimento.value);
    if (!dataNascimento) {
      return 'Informe a data de nascimento no formato dd/mm/aaaa.';
    }

    const nascimento = new Date(`${dataNascimento}T00:00:00`);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    if (nascimento > hoje) {
      return 'A data de nascimento não pode estar no futuro.';
    }

    const limite = new Date(hoje);
    limite.setFullYear(limite.getFullYear() - 7);
    if (nascimento <= limite) {
      return 'No momento, o Pueria acompanha crianças de até 6 anos neste cadastro.';
    }
    return '';
  }

  private converterDataBrasileiraParaIso(valor: string): string | null {
    const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(valor.trim());
    if (!partes) {
      return null;
    }

    const dia = Number(partes[1]);
    const mes = Number(partes[2]);
    const ano = Number(partes[3]);
    const data = new Date(ano, mes - 1, dia);

    if (data.getFullYear() !== ano || data.getMonth() !== mes - 1 || data.getDate() !== dia) {
      return null;
    }

    return `${ano}-${String(mes).padStart(2, '0')}-${String(dia).padStart(2, '0')}`;
  }

  private lerDecimal(valor: string, campo: string, minimo: number, maximo: number): number | string {
    const numero = Number(String(valor).replace(',', '.'));
    if (!Number.isFinite(numero) || numero < minimo || numero > maximo) {
      return `Informe ${campo} entre ${minimo.toLocaleString('pt-BR')} e ${maximo.toLocaleString('pt-BR')} cm.`;
    }
    return numero;
  }

  private extrairErroDecimal(...valores: Array<number | string>): string {
    return valores.find((valor): valor is string => typeof valor === 'string') ?? '';
  }

  private calcularDataMinimaNascimento(): string {
    const data = new Date();
    data.setFullYear(data.getFullYear() - 7);
    data.setDate(data.getDate() + 1);
    return this.formatarDataInput(data);
  }

  private formatarDataInput(data: Date): string {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar o acompanhamento agora.';
  }
}
