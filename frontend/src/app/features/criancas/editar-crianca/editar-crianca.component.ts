import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AlimentacaoInicial, Crianca, Sexo, StatusCondicaoClinica, StatusTriagemNeonatal, TipoGestacao, TipoParto } from '../../../shared/models/crianca.model';
import { CriancasService } from '../criancas.service';

@Component({
  selector: 'app-editar-crianca',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './editar-crianca.component.html',
  styleUrl: './editar-crianca.component.scss'
})
export class EditarCriancaComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly criancasService = inject(CriancasService);

  readonly sexos: { label: string; value: Sexo }[] = [
    { label: 'Feminino', value: 'FEMININO' },
    { label: 'Masculino', value: 'MASCULINO' },
    { label: 'Não informar', value: 'NAO_INFORMADO' }
  ];

  readonly tiposParto: { label: string; value: TipoParto }[] = [
    { label: 'Vaginal', value: 'VAGINAL' },
    { label: 'Cesárea', value: 'CESAREA' },
    { label: 'Vaginal com instrumento', value: 'VAGINAL_INSTRUMENTADO' },
    { label: 'Não informado', value: 'NAO_INFORMADO' }
  ];

  readonly statusTriagens: { label: string; value: StatusTriagemNeonatal }[] = [
    { label: 'Realizado', value: 'REALIZADO' },
    { label: 'Pendente', value: 'PENDENTE' },
    { label: 'Não informado', value: 'NAO_INFORMADO' }
  ];

  readonly alimentacoesIniciais: { label: string; value: AlimentacaoInicial }[] = [
    { label: 'Aleitamento materno exclusivo', value: 'ALEITAMENTO_MATERNO_EXCLUSIVO' },
    { label: 'Aleitamento misto', value: 'ALEITAMENTO_MISTO' },
    { label: 'Fórmula infantil', value: 'FORMULA_INFANTIL' },
    { label: 'Não informado', value: 'NAO_INFORMADO' }
  ];
  readonly statusCondicoes: { label: string; value: StatusCondicaoClinica }[] = [
    { label: 'Não', value: 'NAO' }, { label: 'Sim', value: 'SIM' },
    { label: 'Em investigação', value: 'EM_INVESTIGACAO' },
    { label: 'Prefiro informar depois', value: 'PREFIRO_INFORMAR_DEPOIS' }
  ];

  readonly form = this.formBuilder.group({
    nome: this.formBuilder.nonNullable.control('', [Validators.required, Validators.maxLength(150)]),
    dataNascimento: this.formBuilder.nonNullable.control('', [Validators.required]),
    sexo: this.formBuilder.nonNullable.control<Sexo>('NAO_INFORMADO'),
    prematura: this.formBuilder.nonNullable.control(false),
    semanasGestacionais: this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(22), Validators.max(42)]),
    diasGestacionais: this.formBuilder.control<number | null>(0, [Validators.required, Validators.min(0), Validators.max(6)]),
    tipoParto: this.formBuilder.nonNullable.control<TipoParto>('NAO_INFORMADO', [Validators.required]),
    pesoNascimentoGramas: this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(300), Validators.max(7000)]),
    comprimentoNascimentoCm: this.formBuilder.nonNullable.control('', [Validators.required]),
    perimetroCefalicoNascimentoCm: this.formBuilder.nonNullable.control('', [Validators.required]),
    apgarUmMinuto: this.formBuilder.control<number | null>(null, [Validators.min(0), Validators.max(10)]),
    apgarCincoMinutos: this.formBuilder.control<number | null>(null, [Validators.min(0), Validators.max(10)]),
    utiNeonatal: this.formBuilder.nonNullable.control(false),
    reanimacaoNeonatal: this.formBuilder.nonNullable.control(false),
    ictericiaNeonatal: this.formBuilder.nonNullable.control(false),
    dificuldadeRespiratoria: this.formBuilder.nonNullable.control(false),
    dificuldadeAmamentacao: this.formBuilder.nonNullable.control(false),
    observacoesNascimento: this.formBuilder.nonNullable.control('', [Validators.maxLength(1000)]),
    preNatalRealizado: this.formBuilder.nonNullable.control(false),
    consultasPreNatal: this.formBuilder.control<number | null>(null, [Validators.min(0), Validators.max(60)]),
    diabetesGestacional: this.formBuilder.nonNullable.control(false),
    hipertensaoGestacional: this.formBuilder.nonNullable.control(false),
    infeccaoGestacional: this.formBuilder.nonNullable.control(false),
    sangramentoGestacional: this.formBuilder.nonNullable.control(false),
    usoAlcoolGestacao: this.formBuilder.nonNullable.control(false),
    usoTabacoGestacao: this.formBuilder.nonNullable.control(false),
    outrasExposicoesGestacao: this.formBuilder.nonNullable.control(false),
    observacoesGestacao: this.formBuilder.nonNullable.control('', [Validators.maxLength(1000)]),
    diasAltaHospitalar: this.formBuilder.control<number | null>(null, [Validators.min(0), Validators.max(365)]),
    retornoHospitalarPrimeiraSemana: this.formBuilder.nonNullable.control(false),
    testePezinho: this.formBuilder.nonNullable.control<StatusTriagemNeonatal>('NAO_INFORMADO'),
    testeOrelhinha: this.formBuilder.nonNullable.control<StatusTriagemNeonatal>('NAO_INFORMADO'),
    testeOlhinho: this.formBuilder.nonNullable.control<StatusTriagemNeonatal>('NAO_INFORMADO'),
    testeCoracaozinho: this.formBuilder.nonNullable.control<StatusTriagemNeonatal>('NAO_INFORMADO'),
    amamentacaoPrimeiraHora: this.formBuilder.nonNullable.control(false),
    alimentacaoInicial: this.formBuilder.nonNullable.control<AlimentacaoInicial>('NAO_INFORMADO'),
    tipoGestacao: this.formBuilder.nonNullable.control<TipoGestacao>('NAO_INFORMADO'),
    statusT21: this.formBuilder.nonNullable.control<StatusCondicaoClinica>('PREFIRO_INFORMAR_DEPOIS'),
    statusTurner: this.formBuilder.nonNullable.control<StatusCondicaoClinica>('PREFIRO_INFORMAR_DEPOIS'),
    outraCondicaoRelevante: this.formBuilder.nonNullable.control(false),
    observacoesCondicaoRelevante: this.formBuilder.nonNullable.control('', Validators.maxLength(1000))
  });

  readonly crianca = signal<Crianca | null>(null);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.erro.set('Criança não encontrada.');
      this.carregando.set(false);
      return;
    }

    this.criancasService.buscarPorId(id)
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (crianca) => {
          this.crianca.set(crianca);
          this.form.patchValue({
            nome: crianca.nome,
            dataNascimento: this.formatarDataBrasileira(crianca.dataNascimento),
            sexo: crianca.sexo ?? 'NAO_INFORMADO',
            prematura: crianca.prematura,
            semanasGestacionais: crianca.semanasGestacionais,
            diasGestacionais: crianca.diasGestacionais,
            tipoParto: crianca.tipoParto,
            pesoNascimentoGramas: crianca.pesoNascimentoGramas,
            comprimentoNascimentoCm: this.formatarDecimalInput(crianca.comprimentoNascimentoCm),
            perimetroCefalicoNascimentoCm: this.formatarDecimalInput(crianca.perimetroCefalicoNascimentoCm),
            apgarUmMinuto: crianca.apgarUmMinuto ?? null,
            apgarCincoMinutos: crianca.apgarCincoMinutos ?? null,
            utiNeonatal: crianca.utiNeonatal,
            reanimacaoNeonatal: crianca.reanimacaoNeonatal,
            ictericiaNeonatal: crianca.ictericiaNeonatal,
            dificuldadeRespiratoria: crianca.dificuldadeRespiratoria,
            dificuldadeAmamentacao: crianca.dificuldadeAmamentacao,
            observacoesNascimento: crianca.observacoesNascimento ?? '',
            preNatalRealizado: crianca.preNatalRealizado,
            consultasPreNatal: crianca.consultasPreNatal ?? null,
            diabetesGestacional: crianca.diabetesGestacional,
            hipertensaoGestacional: crianca.hipertensaoGestacional,
            infeccaoGestacional: crianca.infeccaoGestacional,
            sangramentoGestacional: crianca.sangramentoGestacional,
            usoAlcoolGestacao: crianca.usoAlcoolGestacao,
            usoTabacoGestacao: crianca.usoTabacoGestacao,
            outrasExposicoesGestacao: crianca.outrasExposicoesGestacao,
            observacoesGestacao: crianca.observacoesGestacao ?? '',
            diasAltaHospitalar: crianca.diasAltaHospitalar ?? null,
            retornoHospitalarPrimeiraSemana: crianca.retornoHospitalarPrimeiraSemana,
            testePezinho: crianca.testePezinho,
            testeOrelhinha: crianca.testeOrelhinha,
            testeOlhinho: crianca.testeOlhinho,
            testeCoracaozinho: crianca.testeCoracaozinho,
            amamentacaoPrimeiraHora: crianca.amamentacaoPrimeiraHora,
            alimentacaoInicial: crianca.alimentacaoInicial,
            tipoGestacao: crianca.tipoGestacao,
            statusT21: crianca.statusT21,
            statusTurner: crianca.statusTurner,
            outraCondicaoRelevante: crianca.outraCondicaoRelevante,
            observacoesCondicaoRelevante: crianca.observacoesCondicaoRelevante ?? ''
          });
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(this.extrairMensagemErro(erro, 'Não foi possível carregar os dados da criança.'));
        }
      });
  }

  salvar(): void {
    const crianca = this.crianca();
    if (!crianca) {
      return;
    }

    this.erro.set('');
    this.form.markAllAsTouched();

    const erroRegra = this.validarRegraIdade() || this.validarRegraPrematuridade();
    if (erroRegra) {
      this.erro.set(erroRegra);
      return;
    }

    const comprimento = this.lerDecimal(this.form.controls.comprimentoNascimentoCm.value, 'comprimento ao nascer', 20, 70);
    const perimetro = this.lerDecimal(this.form.controls.perimetroCefalicoNascimentoCm.value, 'perímetro cefálico ao nascer', 20, 50);
    const erroDecimal = this.extrairErroDecimal(comprimento, perimetro);
    if (erroDecimal) {
      this.erro.set(erroDecimal);
      return;
    }
    const comprimentoNumero = comprimento as number;
    const perimetroNumero = perimetro as number;

    if (this.form.invalid) {
      this.erro.set('Revise os campos obrigatórios antes de salvar.');
      return;
    }

    const valor = this.form.getRawValue();
    this.salvando.set(true);

    this.criancasService.atualizar(crianca.id, {
      nome: valor.nome,
      dataNascimento: this.dataParaIso(valor.dataNascimento) as string,
      sexo: valor.sexo,
      prematura: valor.prematura,
      semanasGestacionais: valor.semanasGestacionais as number,
      diasGestacionais: valor.diasGestacionais as number,
      tipoParto: valor.tipoParto,
      pesoNascimentoGramas: valor.pesoNascimentoGramas as number,
      comprimentoNascimentoCm: comprimentoNumero,
      perimetroCefalicoNascimentoCm: perimetroNumero,
      apgarUmMinuto: valor.apgarUmMinuto,
      apgarCincoMinutos: valor.apgarCincoMinutos,
      utiNeonatal: valor.utiNeonatal,
      reanimacaoNeonatal: valor.reanimacaoNeonatal,
      ictericiaNeonatal: valor.ictericiaNeonatal,
      dificuldadeRespiratoria: valor.dificuldadeRespiratoria,
      dificuldadeAmamentacao: valor.dificuldadeAmamentacao,
      observacoesNascimento: valor.observacoesNascimento.trim() || null,
      preNatalRealizado: valor.preNatalRealizado,
      consultasPreNatal: valor.consultasPreNatal,
      diabetesGestacional: valor.diabetesGestacional,
      hipertensaoGestacional: valor.hipertensaoGestacional,
      infeccaoGestacional: valor.infeccaoGestacional,
      sangramentoGestacional: valor.sangramentoGestacional,
      usoAlcoolGestacao: valor.usoAlcoolGestacao,
      usoTabacoGestacao: valor.usoTabacoGestacao,
      outrasExposicoesGestacao: valor.outrasExposicoesGestacao,
      observacoesGestacao: valor.observacoesGestacao.trim() || null,
      diasAltaHospitalar: valor.diasAltaHospitalar,
      retornoHospitalarPrimeiraSemana: valor.retornoHospitalarPrimeiraSemana,
      testePezinho: valor.testePezinho,
      testeOrelhinha: valor.testeOrelhinha,
      testeOlhinho: valor.testeOlhinho,
      testeCoracaozinho: valor.testeCoracaozinho,
      amamentacaoPrimeiraHora: valor.amamentacaoPrimeiraHora,
      alimentacaoInicial: valor.alimentacaoInicial,
      tipoGestacao: valor.tipoGestacao,
      statusT21: valor.statusT21,
      statusTurner: valor.sexo === 'FEMININO' ? valor.statusTurner : 'PREFIRO_INFORMAR_DEPOIS',
      outraCondicaoRelevante: valor.outraCondicaoRelevante,
      observacoesCondicaoRelevante: valor.observacoesCondicaoRelevante.trim() || null
    })
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: (crianca) => {
          void this.router.navigate(['/criancas', crianca.id]);
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(this.extrairMensagemErro(erro, 'Não foi possível salvar as alterações agora.'));
        }
      });
  }

  fecharErro(): void {
    this.erro.set('');
  }

  rotaCancelar(): string[] {
    const origem = this.route.snapshot.queryParamMap.get('origem');
    if (origem === 'minhas-criancas') {
      return ['/criancas'];
    }

    const crianca = this.crianca();
    return crianca ? ['/criancas', crianca.id] : ['/criancas'];
  }

  formatarDataNascimento(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitos = input.value.replace(/\D/g, '').slice(0, 8);
    const dataFormatada = digitos
      .replace(/^(\d{2})(\d)/, '$1/$2')
      .replace(/^(\d{2}\/\d{2})(\d)/, '$1/$2');

    this.form.controls.dataNascimento.setValue(dataFormatada, { emitEvent: false });
  }

  private validarRegraPrematuridade(): string {
    const semanas = this.form.controls.semanasGestacionais.value;
    const prematura = this.form.controls.prematura.value;

    if (semanas == null) {
      return '';
    }

    if (semanas < 37 && !prematura) {
      return 'Uma criança com menos de 37 semanas gestacionais deve ser marcada como prematura.';
    }

    if (semanas >= 37 && prematura) {
      return 'Uma criança marcada como prematura deve ter menos de 37 semanas gestacionais.';
    }

    return '';
  }

  private validarRegraIdade(): string {
    const dataNascimento = this.dataParaIso(this.form.controls.dataNascimento.value);
    if (!dataNascimento) {
      return 'Informe a data de nascimento no formato dd/mm/aaaa.';
    }

    const [ano, mes, dia] = dataNascimento.split('-').map(Number);
    const nascimento = new Date(ano, mes - 1, dia);
    if (nascimento.getFullYear() !== ano || nascimento.getMonth() !== mes - 1 || nascimento.getDate() !== dia) {
      return 'Informe uma data de nascimento válida.';
    }

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    if (nascimento > hoje) {
      return 'A data de nascimento não pode estar no futuro.';
    }

    const limiteMvp = new Date(hoje);
    limiteMvp.setFullYear(limiteMvp.getFullYear() - 7);
    if (nascimento <= limiteMvp) {
      return 'No momento, o Pueria acompanha crianças de até 6 anos neste cadastro.';
    }

    return '';
  }

  private dataParaIso(valor: string): string | null {
    const correspondencia = valor.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
    if (!correspondencia) {
      return null;
    }

    const [, dia, mes, ano] = correspondencia;
    return `${ano}-${mes}-${dia}`;
  }

  private formatarDataBrasileira(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
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

  private formatarDecimalInput(valor: number): string {
    return valor.toLocaleString('pt-BR', { maximumFractionDigits: 2 });
  }

  private extrairMensagemErro(erro: HttpErrorResponse, fallback: string): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }

    return fallback;
  }
}
