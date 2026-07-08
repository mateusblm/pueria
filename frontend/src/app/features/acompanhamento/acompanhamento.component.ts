import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { catchError, finalize, forkJoin, map, of, switchMap } from 'rxjs';
import { Crianca, Parentesco, Sexo, TipoParto } from '../../shared/models/crianca.model';
import { MarcoDesenvolvimento } from '../../shared/models/desenvolvimento.model';
import { CriancasService } from '../criancas/criancas.service';
import { DesenvolvimentoService } from '../desenvolvimento/desenvolvimento.service';

type ResumoCrianca = {
  crianca: Crianca;
  marcos: MarcoDesenvolvimento[];
  erro?: string;
};

@Component({
  selector: 'app-acompanhamento',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './acompanhamento.component.html',
  styleUrl: './acompanhamento.component.scss'
})
export class AcompanhamentoComponent implements OnInit {
  private readonly criancasService = inject(CriancasService);
  private readonly desenvolvimentoService = inject(DesenvolvimentoService);
  private readonly formBuilder = inject(FormBuilder);

  readonly resumos = signal<ResumoCrianca[]>([]);
  readonly carregando = signal(true);
  readonly salvandoCrianca = signal(false);
  readonly cadastroInicialAberto = signal(false);
  readonly etapaCadastro = signal(1);
  readonly erro = signal('');
  readonly erroCadastro = signal('');

  readonly possuiCriancas = computed(() => this.resumos().length > 0);
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
            this.desenvolvimentoService.listarMarcos(crianca.id).pipe(
              map((marcos) => ({ crianca, marcos })),
              catchError(() => of({ crianca, marcos: [], erro: 'Não foi possível carregar o desenvolvimento agora.' }))
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

  cadastrarPrimeiraCrianca(): void {
    this.erroCadastro.set('');
    const erro = this.validarEtapa(1) || this.validarEtapa(2) || this.validarEtapa(3);
    if (erro) {
      this.erroCadastro.set(erro);
      return;
    }

    const valor = this.formCadastro.getRawValue();
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
      dataNascimento: valor.dataNascimento,
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
        next: () => {
          this.cadastroInicialAberto.set(false);
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

  labelPontosAtencao(total: number): string {
    if (total === 0) {
      return 'Sem dúvidas para a consulta';
    }
    return total === 1 ? '1 dúvida para a consulta' : `${total} dúvidas para a consulta`;
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
    const dataNascimento = this.formCadastro.controls.dataNascimento.value;
    if (!dataNascimento) {
      return '';
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
