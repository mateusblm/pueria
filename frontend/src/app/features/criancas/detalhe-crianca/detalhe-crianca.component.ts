import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { CriancasService } from '../criancas.service';

@Component({
  selector: 'app-detalhe-crianca',
  imports: [RouterLink],
  templateUrl: './detalhe-crianca.component.html',
  styleUrl: './detalhe-crianca.component.scss'
})
export class DetalheCriancaComponent implements OnInit {
  readonly crianca = signal<Crianca | null>(null);
  readonly carregando = signal(true);
  readonly removendo = signal(false);
  readonly confirmandoRemocao = signal(false);
  readonly erro = signal('');

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly criancasService: CriancasService
  ) {}

  ngOnInit(): void {
    this.carregarCrianca();
  }

  carregarCrianca(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.erro.set('Criança não encontrada.');
      this.carregando.set(false);
      return;
    }

    this.carregando.set(true);
    this.erro.set('');

    this.criancasService.buscarPorId(id)
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (crianca) => {
          this.crianca.set(crianca);
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(erro.status === 404
            ? 'Criança não encontrada ou não vinculada à sua conta.'
            : 'Não foi possível carregar os dados agora.');
        }
      });
  }

  abrirConfirmacaoRemocao(): void {
    this.confirmandoRemocao.set(true);
    this.erro.set('');
  }

  cancelarRemocao(): void {
    this.confirmandoRemocao.set(false);
  }

  confirmarRemocao(): void {
    const crianca = this.crianca();
    if (!crianca) {
      return;
    }

    this.removendo.set(true);
    this.erro.set('');

    this.criancasService.remover(crianca.id)
      .pipe(finalize(() => this.removendo.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/criancas');
        },
        error: (erro: HttpErrorResponse) => {
          this.confirmandoRemocao.set(false);
          const mensagens = erro.error?.mensagens;
          this.erro.set(Array.isArray(mensagens) && mensagens.length > 0
            ? mensagens[0]
            : 'Não foi possível remover o perfil agora.');
        }
      });
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

  formatarData(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`));
  }

  formatarPeso(pesoGramas: number): string {
    const pesoKg = pesoGramas / 1000;
    return `${pesoKg.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    })} kg (${pesoGramas.toLocaleString('pt-BR')} g)`;
  }

  formatarMedida(valor: number, unidade: string): string {
    return `${valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 1,
      maximumFractionDigits: 2
    })} ${unidade}`;
  }

  formatarIdadeGestacional(semanas: number, dias: number): string {
    return `${semanas} semanas${dias > 0 ? ` e ${dias} dias` : ''}`;
  }

  formatarApgar(valor?: number | null): string {
    return valor == null ? 'Não informado' : `${valor}/10`;
  }

  labelSexo(sexo: string | null): string {
    const labels: Record<string, string> = {
      FEMININO: 'Feminino',
      MASCULINO: 'Masculino',
      NAO_INFORMADO: 'Não informado'
    };
    return sexo ? labels[sexo] ?? sexo : 'Não informado';
  }

  labelTipoParto(tipoParto: string): string {
    const labels: Record<string, string> = {
      VAGINAL: 'Vaginal',
      CESAREA: 'Cesárea',
      VAGINAL_INSTRUMENTADO: 'Vaginal com instrumento',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[tipoParto] ?? tipoParto;
  }

  labelTriagem(status: string): string {
    const labels: Record<string, string> = {
      REALIZADO: 'Realizado',
      PENDENTE: 'Pendente',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[status] ?? status;
  }

  labelAlimentacaoInicial(alimentacao: string): string {
    const labels: Record<string, string> = {
      ALEITAMENTO_MATERNO_EXCLUSIVO: 'Aleitamento materno exclusivo',
      ALEITAMENTO_MISTO: 'Aleitamento misto',
      FORMULA_INFANTIL: 'Fórmula infantil',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[alimentacao] ?? alimentacao;
  }

  listarIntercorrencias(crianca: Crianca): string {
    const pontos = [
      crianca.utiNeonatal ? 'UTI neonatal' : '',
      crianca.reanimacaoNeonatal ? 'reanimação ao nascer' : '',
      crianca.ictericiaNeonatal ? 'icterícia neonatal' : '',
      crianca.dificuldadeRespiratoria ? 'dificuldade respiratória' : '',
      crianca.dificuldadeAmamentacao ? 'dificuldade para mamar' : ''
    ].filter(Boolean);

    return pontos.length > 0 ? pontos.join(', ') : 'Sem intercorrências registradas';
  }

  listarPontosGestacao(crianca: Crianca): string {
    const pontos = [
      crianca.diabetesGestacional ? 'diabetes gestacional' : '',
      crianca.hipertensaoGestacional ? 'pressão alta ou pré-eclâmpsia' : '',
      crianca.infeccaoGestacional ? 'infecção importante' : '',
      crianca.sangramentoGestacional ? 'sangramento importante' : '',
      crianca.usoAlcoolGestacao ? 'exposição a álcool' : '',
      crianca.usoTabacoGestacao ? 'exposição a tabaco' : '',
      crianca.outrasExposicoesGestacao ? 'outras exposições relevantes' : ''
    ].filter(Boolean);

    return pontos.length > 0 ? pontos.join(', ') : 'Sem intercorrências registradas';
  }
}
