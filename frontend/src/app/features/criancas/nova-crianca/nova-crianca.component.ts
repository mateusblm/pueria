import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { CriancasService } from '../criancas.service';
import { CriarCriancaRequest, Parentesco, Sexo } from '../../../shared/models/crianca.model';

interface ErroApi {
  mensagens?: string[];
  mensagem?: string;
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

  readonly form = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    dataNascimento: ['', [Validators.required]],
    sexo: this.formBuilder.control<Sexo | ''>(''),
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
      this.erro = this.obterMensagemFormularioInvalido();
      return;
    }

    const valores = this.form.getRawValue();

    if (!valores.prematura && valores.semanasGestacionais !== null && valores.semanasGestacionais < 37) {
      this.erro = 'Uma criança com menos de 37 semanas gestacionais deve ser marcada como prematura.';
      return;
    }

    if (valores.prematura && valores.semanasGestacionais !== null && valores.semanasGestacionais >= 37) {
      this.erro = 'Uma criança marcada como prematura deve ter menos de 37 semanas gestacionais.';
      return;
    }

    const request: CriarCriancaRequest = {
      nome: valores.nome.trim(),
      dataNascimento: valores.dataNascimento,
      sexo: valores.sexo ? valores.sexo : null,
      prematura: valores.prematura,
      semanasGestacionais: valores.semanasGestacionais!,
      pesoNascimentoGramas: valores.pesoNascimentoGramas!,
      parentesco: valores.parentesco,
      aceiteConsentimento: valores.aceiteConsentimento,
      versaoTermoConsentimento: valores.versaoTermoConsentimento
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

  private obterMensagemFormularioInvalido(): string {
    const controles = this.form.controls;

    if (controles.nome.invalid) {
      return 'Informe o nome da criança.';
    }

    if (controles.dataNascimento.invalid) {
      return 'Informe uma data de nascimento válida.';
    }

    if (controles.semanasGestacionais.invalid) {
      return 'Informe as semanas gestacionais entre 22 e 42.';
    }

    if (controles.pesoNascimentoGramas.invalid) {
      return 'Informe o peso ao nascer entre 300g e 7000g.';
    }

    if (controles.parentesco.invalid) {
      return 'Informe seu vínculo com a criança.';
    }

    if (controles.aceiteConsentimento.invalid) {
      return 'O consentimento precisa estar aceito para cadastrar a criança.';
    }

    return 'Revise os dados informados antes de continuar.';
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const body = erro.error as ErroApi | string | null;

    if (body && typeof body === 'object') {
      if (Array.isArray(body.mensagens) && body.mensagens.length > 0) {
        return body.mensagens[0];
      }

      if (body.mensagem) {
        return body.mensagem;
      }
    }

    if (typeof body === 'string' && body.trim()) {
      return body;
    }

    if (erro.status === 401) {
      return 'Sua sessão expirou. Entre novamente para continuar.';
    }

    return 'Não foi possível cadastrar a criança agora.';
  }
}
