import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { CriancasService } from '../criancas.service';
import { CriarCriancaRequest, Parentesco, Sexo } from '../../../shared/models/crianca.model';

interface ErroApiResponse {
  status?: number;
  erro?: string;
  mensagens?: string[];
}

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
    { valor: 'MASCULINO', label: 'Masculino' }
  ];

  readonly parentescos: Array<{ valor: Parentesco; label: string }> = [
    { valor: 'MAE', label: 'Mãe' },
    { valor: 'PAI', label: 'Pai' },
    { valor: 'RESPONSAVEL_LEGAL', label: 'Responsável legal' },
    { valor: 'AVO', label: 'Avó/Avô' },
    { valor: 'OUTRO', label: 'Outro' }
  ];

  readonly form = this.formBuilder.group({
    nome: this.formBuilder.nonNullable.control('', [Validators.required, Validators.maxLength(150)]),
    dataNascimento: this.formBuilder.nonNullable.control('', [Validators.required]),
    sexo: this.formBuilder.control<Sexo | null>(null),
    prematura: this.formBuilder.nonNullable.control(false),
    semanasGestacionais: this.formBuilder.control<number | null>(null),
    pesoNascimentoGramas: this.formBuilder.control<number | null>(null),
    parentesco: this.formBuilder.nonNullable.control<Parentesco>('MAE', [Validators.required]),
    aceiteConsentimento: this.formBuilder.nonNullable.control(false, [Validators.requiredTrue]),
    versaoTermoConsentimento: this.formBuilder.nonNullable.control('2026.07', [Validators.required, Validators.maxLength(30)])
  });

  criar(): void {
    this.erro = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro = this.obterMensagemFormulario();
      return;
    }

    const valores = this.form.getRawValue();

    if (!this.validarPrematuridade(valores.semanasGestacionais, valores.prematura)) {
      return;
    }

    const request: CriarCriancaRequest = {
      nome: valores.nome.trim(),
      dataNascimento: valores.dataNascimento,
      sexo: valores.sexo,
      prematura: valores.prematura,
      semanasGestacionais: valores.semanasGestacionais,
      pesoNascimentoGramas: valores.pesoNascimentoGramas,
      parentesco: valores.parentesco,
      aceiteConsentimento: valores.aceiteConsentimento,
      versaoTermoConsentimento: valores.versaoTermoConsentimento
    };

    this.carregando = true;

    this.criancasService.criar(request)
      .pipe(finalize(() => {
        this.carregando = false;
      }))
      .subscribe({
        next: (crianca) => {
          if (!crianca?.id) {
            this.erro = 'A criança foi cadastrada, mas a API não retornou o identificador para abrir o perfil.';
            return;
          }

          void this.router.navigate(['/app/criancas', crianca.id]);
        },
        error: (erro: HttpErrorResponse) => {
          this.erro = this.obterMensagemErro(erro);
        }
      });
  }

  campoInvalido(campo: keyof typeof this.form.controls): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.touched || controle.dirty);
  }

  limparErro(): void {
    this.erro = '';
  }

  private validarPrematuridade(semanasGestacionais: number | null, prematura: boolean): boolean {
    if (semanasGestacionais !== null && semanasGestacionais < 37 && !prematura) {
      this.erro = 'Uma criança com menos de 37 semanas gestacionais deve ser marcada como prematura.';
      return false;
    }

    return true;
  }

  private obterMensagemFormulario(): string {
    if (this.form.controls.nome.invalid) {
      return 'Informe o nome da criança.';
    }

    if (this.form.controls.dataNascimento.invalid) {
      return 'Informe a data de nascimento da criança.';
    }

    if (this.form.controls.aceiteConsentimento.invalid) {
      return 'O consentimento precisa estar aceito para cadastrar a criança.';
    }

    return 'Revise os campos obrigatórios antes de continuar.';
  }

  private obterMensagemErro(erro: HttpErrorResponse): string {
    const corpo = erro.error as ErroApiResponse | string | null;

    if (typeof corpo === 'string' && corpo.trim().length > 0) {
      return corpo;
    }

    if (corpo && typeof corpo === 'object') {
      if (Array.isArray(corpo.mensagens) && corpo.mensagens.length > 0) {
        return corpo.mensagens[0];
      }

      if (corpo.erro) {
        return corpo.erro;
      }
    }

    if (erro.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se a API está em execução.';
    }

    if (erro.status === 401) {
      return 'Sua sessão expirou. Faça login novamente.';
    }

    if (erro.status === 400) {
      return 'Revise os dados informados antes de continuar.';
    }

    return 'Não foi possível cadastrar a criança agora.';
  }
}
