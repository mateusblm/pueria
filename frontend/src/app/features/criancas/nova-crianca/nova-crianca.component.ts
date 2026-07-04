import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { CriancasService } from '../criancas.service';
import { CriarCriancaRequest, Parentesco, Sexo } from '../../../shared/models/crianca.model';

@Component({
  selector: 'app-nova-crianca',
  imports: [ReactiveFormsModule],
  templateUrl: './nova-crianca.component.html',
  styleUrl: './nova-crianca.component.scss'
})
export class NovaCriancaComponent {
  carregando = false;
  erro = '';

    constructor(
    private readonly formBuilder: FormBuilder,
    private readonly criancasService: CriancasService,
    private readonly router: Router
  ) {}

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
    sexo: [''],
    prematura: [false],
    semanasGestacionais: this.formBuilder.control<number | null>(null),
    pesoNascimentoGramas: this.formBuilder.control<number | null>(null),
    parentesco: this.formBuilder.nonNullable.control<Parentesco>('MAE', [Validators.required]),
    aceiteConsentimento: [false, [Validators.requiredTrue]],
    versaoTermoConsentimento: ['2026.07', [Validators.required, Validators.maxLength(30)]]
  });



  criar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valores = this.form.getRawValue();
    const request: CriarCriancaRequest = {
      nome: valores.nome.trim(),
      dataNascimento: valores.dataNascimento,
      sexo: valores.sexo ? valores.sexo as Sexo : null,
      prematura: valores.prematura,
      semanasGestacionais: valores.semanasGestacionais,
      pesoNascimentoGramas: valores.pesoNascimentoGramas,
      parentesco: valores.parentesco,
      aceiteConsentimento: valores.aceiteConsentimento,
      versaoTermoConsentimento: valores.versaoTermoConsentimento
    };

    this.carregando = true;
    this.erro = '';

    this.criancasService.criar(request).subscribe({
      next: (crianca) => void this.router.navigate(['/app/criancas', crianca.id]),
      error: (erro: HttpErrorResponse) => {
        this.erro = erro.status === 400
          ? 'Revise os dados informados. O consentimento precisa estar aceito.'
          : 'Não foi possível cadastrar a criança agora.';
        this.carregando = false;
      },
      complete: () => {
        this.carregando = false;
      }
    });
  }
}
