import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { CriancasService } from '../criancas.service';
import { CriarCriancaRequest, Parentesco, Sexo } from '../../../shared/models/crianca.model';

@Component({
  selector: 'app-nova-crianca',
  imports: [ReactiveFormsModule],
  templateUrl: './nova-crianca.component.html',
  styleUrl: './nova-crianca.component.scss'
})
export class NovaCriancaComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly router = inject(Router);

  carregando = false;
  erro = '';

  readonly sexos: Array<{ valor: Sexo; label: string }> = [
    { valor: 'FEMININO', label: 'Feminino' },
    { valor: 'MASCULINO', label: 'Masculino' },
    { valor: 'NAO_INFORMADO', label: 'Não informar agora' }
  ];

  readonly parentescos: Array<{ valor: Parentesco; label: string }> = [
    { valor: 'MAE', label: 'Mãe' },
    { valor: 'PAI', label: 'Pai' },
    { valor: 'RESPONSAVEL_LEGAL', label: 'Responsável legal' },
    { valor: 'AVO', label: 'Avó/Avô' },
    { valor: 'OUTRO', label: 'Outro' }
  ];

  readonly form = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    dataNascimento: ['', [Validators.required]],
    sexo: this.formBuilder.control<Sexo | null>(null),
    prematura: [false],
    semanasGestacionais: this.formBuilder.control<number | null>(null, [
      Validators.required,
      Validators.min(22),
      Validators.max(42)
    ]),
    pesoNascimentoGramas: this.formBuilder.control<number | null>(null, [
      Validators.required,
      Validators.min(300),
      Validators.max(7000)
    ]),
    parentesco: this.formBuilder.nonNullable.control<Parentesco>('MAE', [Validators.required]),
    aceiteConsentimento: [false, [Validators.requiredTrue]],
    versaoTermoConsentimento: ['2026.07', [Validators.required, Validators.maxLength(30)]]
  });

  criar(): void {
    this.erro = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro = this.obterPrimeiraMensagemFormulario();
      return;
    }

    const valores = this.form.getRawValue();
    const semanasGestacionais = valores.semanasGestacionais as number;
    const pesoNascimentoGramas = valores.pesoNascimentoGramas as number;

    const erroCoerencia = this.validarCoerenciaPrematuridade(valores.prematura, semanasGestacionais);
    if (erroCoerencia) {
      this.erro = erroCoerencia;
      return;
    }

    const request: CriarCriancaRequest = {
      nome: valores.nome.trim(),
      dataNascimento: valores.dataNascimento,
      sexo: valores.sexo,
      prematura: valores.prematura,
      semanasGestacionais,
      pesoNascimentoGramas,
      parentesco: valores.parentesco,
      aceiteConsentimento: valores.aceiteConsentimento,
      versaoTermoConsentimento: valores.versaoTermoConsentimento.trim()
    };

    this.carregando = true;

    this.criancasService.criar(request)
      .pipe(finalize(() => this.carregando = false))
      .subscribe({
        next: (crianca) => void this.router.navigate(['/app/criancas', crianca.id]),
        error: (erro: HttpErrorResponse) => {
          this.erro = this.extrairMensagemErro(erro);
        }
      });
  }

  fecharErro(): void {
    this.erro = '';
  }

  campoInvalido(campo: keyof typeof this.form.controls): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.touched || controle.dirty);
  }

  mensagemCampo(campo: keyof typeof this.form.controls): string {
    const controle = this.form.controls[campo];

    if (!controle.errors) {
      return '';
    }

    if (controle.errors['required'] || controle.errors['requiredTrue']) {
      return this.mensagemObrigatoria(campo);
    }

    if (controle.errors['min']) {
      return campo === 'semanasGestacionais'
        ? 'Informe no mínimo 22 semanas.'
        : 'Informe no mínimo 300 gramas.';
    }

    if (controle.errors['max']) {
      return campo === 'semanasGestacionais'
        ? 'Informe no máximo 42 semanas.'
        : 'Informe no máximo 7000 gramas.';
    }

    if (controle.errors['maxlength']) {
      return 'O valor informado está maior que o permitido.';
    }

    return 'Revise este campo.';
  }

  private obterPrimeiraMensagemFormulario(): string {
    const ordem: Array<keyof typeof this.form.controls> = [
      'nome',
      'dataNascimento',
      'semanasGestacionais',
      'pesoNascimentoGramas',
      'parentesco',
      'aceiteConsentimento',
      'versaoTermoConsentimento'
    ];

    for (const campo of ordem) {
      if (this.form.controls[campo].invalid) {
        return this.mensagemCampo(campo);
      }
    }

    return 'Revise os dados informados.';
  }

  private mensagemObrigatoria(campo: keyof typeof this.form.controls): string {
    const mensagens: Record<string, string> = {
      nome: 'O nome da criança é obrigatório.',
      dataNascimento: 'A data de nascimento é obrigatória.',
      semanasGestacionais: 'As semanas gestacionais são obrigatórias.',
      pesoNascimentoGramas: 'O peso de nascimento é obrigatório.',
      parentesco: 'O vínculo com a criança é obrigatório.',
      aceiteConsentimento: 'O consentimento precisa estar aceito para cadastrar a criança.',
      versaoTermoConsentimento: 'A versão do termo de consentimento é obrigatória.'
    };

    return mensagens[String(campo)] ?? 'Este campo é obrigatório.';
  }

  private validarCoerenciaPrematuridade(prematura: boolean, semanasGestacionais: number): string {
    if (prematura && semanasGestacionais >= 37) {
      return 'Uma criança marcada como prematura deve ter menos de 37 semanas gestacionais.';
    }

    if (!prematura && semanasGestacionais < 37) {
      return 'Uma criança com menos de 37 semanas gestacionais deve ser marcada como prematura.';
    }

    return '';
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;

    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens.join('\n');
    }

    if (typeof erro.error?.message === 'string') {
      return erro.error.message;
    }

    if (erro.status === 401) {
      return 'Sua sessão expirou. Faça login novamente.';
    }

    if (erro.status === 400) {
      return 'Revise os dados informados.';
    }

    return 'Não foi possível cadastrar a criança agora.';
  }
}
