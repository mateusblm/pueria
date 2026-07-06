import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Crianca, Sexo, TipoParto } from '../../../shared/models/crianca.model';
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
  readonly dataMaximaNascimento = this.formatarDataInput(new Date());
  readonly dataMinimaNascimento = this.calcularDataMinimaNascimento();

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
    observacoesNascimento: this.formBuilder.nonNullable.control('', [Validators.maxLength(1000)])
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
            dataNascimento: crianca.dataNascimento,
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
            observacoesNascimento: crianca.observacoesNascimento ?? ''
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
      dataNascimento: valor.dataNascimento,
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
      observacoesNascimento: valor.observacoesNascimento.trim() || null
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
    const dataNascimento = this.form.controls.dataNascimento.value;
    if (!dataNascimento) {
      return '';
    }

    const nascimento = new Date(`${dataNascimento}T00:00:00`);
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

  private extrairMensagemErro(erro: HttpErrorResponse, fallback: string): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }

    return fallback;
  }
}
