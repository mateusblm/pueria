import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { EstagioAlimentar, RegistroAlimentacao, SalvarRegistroAlimentacaoRequest, TexturaAlimentar, TipoLeiteAlimentacao } from '../../../shared/models/alimentacao.model';
import { CriancasService } from '../../criancas/criancas.service';
import { AlimentacaoService } from '../alimentacao.service';

type Opcao<T extends string> = { valor: T; label: string };

@Component({
  selector: 'app-alimentacao-crianca',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './alimentacao-crianca.component.html',
  styleUrl: './alimentacao-crianca.component.scss'
})
export class AlimentacaoCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly alimentacaoService = inject(AlimentacaoService);

  readonly crianca = signal<Crianca | null>(null);
  readonly registros = signal<RegistroAlimentacao[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly aviso = signal('');
  readonly editandoId = signal('');
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly tiposLeite: Opcao<TipoLeiteAlimentacao>[] = [
    { valor: 'LEITE_MATERNO', label: 'Leite materno' },
    { valor: 'FORMULA_INFANTIL', label: 'Fórmula infantil' },
    { valor: 'MISTO', label: 'Leite materno e fórmula' },
    { valor: 'NAO_CONSOME_LEITE', label: 'Não consome leite' },
    { valor: 'NAO_INFORMADO', label: 'Não informado' }
  ];

  readonly estagios: Opcao<EstagioAlimentar>[] = [
    { valor: 'APENAS_LEITE', label: 'Apenas leite' },
    { valor: 'INICIANDO_ALIMENTACAO_COMPLEMENTAR', label: 'Iniciando alimentação complementar' },
    { valor: 'ALIMENTACAO_COMPLEMENTAR_ESTABELECIDA', label: 'Alimentação complementar estabelecida' },
    { valor: 'COMIDA_DA_FAMILIA', label: 'Comida da família' },
    { valor: 'NAO_INFORMADO', label: 'Não informado' }
  ];

  readonly texturas: Opcao<TexturaAlimentar>[] = [
    { valor: 'LIQUIDA', label: 'Líquida' },
    { valor: 'PASTOSA', label: 'Pastosa' },
    { valor: 'AMASSADA', label: 'Amassada' },
    { valor: 'PEDACOS_MACIOS', label: 'Pedaços macios' },
    { valor: 'COMIDA_DA_FAMILIA', label: 'Comida da família' },
    { valor: 'NAO_INFORMADO', label: 'Não informado' }
  ];

  readonly form = this.fb.group({
    dataRegistro: ['', Validators.required],
    tipoLeite: this.fb.nonNullable.control<TipoLeiteAlimentacao>('NAO_INFORMADO', Validators.required),
    estagioAlimentar: this.fb.nonNullable.control<EstagioAlimentar>('NAO_INFORMADO', Validators.required),
    idadeInicioAlimentacaoComplementarMeses: [''],
    refeicoesPorDia: [''],
    consomeAgua: [false],
    usaMamadeira: [false],
    usaCopo: [false],
    usaColher: [false],
    blwMisto: [false],
    autoalimentacao: [false],
    texturaPredominante: this.fb.nonNullable.control<TexturaAlimentar>('NAO_INFORMADO', Validators.required),
    consomeFrutas: [false],
    consomeLegumesVerduras: [false],
    consomeLegumes: [false],
    consomeVerduras: [false],
    consomeCereaisTuberculos: [false],
    consomeFeijoesLeguminosas: [false],
    consomeCarnesOvos: [false],
    ultraprocessadosFrequentes: [false],
    bebidasAdocadas: [false],
    acucarAdicionado: [false],
    salAdicionado: [false],
    telasDuranteRefeicoes: [false],
    refeicoesEmFamilia: [false],
    rotinaAlimentarRegular: [false],
    seletividadeAlimentar: [false],
    recusaPersistente: [false],
    engasgosFrequentes: [false],
    vomitosRecorrentes: [false],
    constipacao: [false],
    diarreiaRecorrente: [false],
    dificuldadeGanhoPesoPercebida: [false],
    familiaTranquilaGanhoPesoAtual: [false],
    preocupacaoFamilia: [false],
    observacao: ['', Validators.maxLength(1000)]
  });

  readonly registrosOrdenados = computed(() =>
    [...this.registros()].sort((a, b) => this.compararRegistrosRecentes(a, b))
  );
  readonly ultimoRegistro = computed(() => this.registrosOrdenados()[0] ?? null);

  ngOnInit(): void {
    this.form.patchValue({ dataRegistro: this.formatarEntradaData(this.dataMaximaIso) });
    this.carregar();
  }

  carregar(): void {
    const criancaId = this.route.snapshot.paramMap.get('id') ?? '';
    this.carregando.set(true);
    this.erro.set('');

    forkJoin({
      crianca: this.criancasService.buscarPorId(criancaId),
      registros: this.alimentacaoService.listar(criancaId)
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ crianca, registros }) => {
          this.crianca.set(crianca);
          this.registros.set(registros);
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

    let request: SalvarRegistroAlimentacaoRequest;
    try {
      request = this.criarRequest();
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro alimentar.');
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
      ? this.alimentacaoService.atualizar(criancaId, editandoId, request)
      : this.alimentacaoService.registrar(criancaId, request);

    requisicao
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: (registro) => {
          this.registros.update((registros) => {
            const semAtual = registros.filter((item) => item.id !== registro.id);
            return [...semAtual, registro];
          });
          this.cancelarEdicao();
          this.aviso.set('Registro alimentar salvo.');
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  editar(registro: RegistroAlimentacao): void {
    this.editandoId.set(registro.id);
    this.erro.set('');
    this.aviso.set('');
    this.form.patchValue({
      dataRegistro: this.formatarEntradaData(registro.dataRegistro),
      tipoLeite: registro.tipoLeite,
      estagioAlimentar: registro.estagioAlimentar,
      idadeInicioAlimentacaoComplementarMeses: this.formatarInteiro(registro.idadeInicioAlimentacaoComplementarMeses),
      refeicoesPorDia: this.formatarInteiro(registro.refeicoesPorDia),
      consomeAgua: !!registro.consomeAgua,
      usaMamadeira: !!registro.usaMamadeira,
      usaCopo: !!registro.usaCopo,
      usaColher: !!registro.usaColher,
      blwMisto: !!registro.blwMisto,
      autoalimentacao: !!registro.autoalimentacao,
      texturaPredominante: registro.texturaPredominante,
      consomeFrutas: !!registro.consomeFrutas,
      consomeLegumesVerduras: !!registro.consomeLegumesVerduras,
      consomeLegumes: !!registro.consomeLegumes || !!registro.consomeLegumesVerduras,
      consomeVerduras: !!registro.consomeVerduras || !!registro.consomeLegumesVerduras,
      consomeCereaisTuberculos: !!registro.consomeCereaisTuberculos,
      consomeFeijoesLeguminosas: !!registro.consomeFeijoesLeguminosas,
      consomeCarnesOvos: !!registro.consomeCarnesOvos,
      ultraprocessadosFrequentes: !!registro.ultraprocessadosFrequentes,
      bebidasAdocadas: !!registro.bebidasAdocadas,
      acucarAdicionado: !!registro.acucarAdicionado,
      salAdicionado: !!registro.salAdicionado,
      telasDuranteRefeicoes: !!registro.telasDuranteRefeicoes,
      refeicoesEmFamilia: !!registro.refeicoesEmFamilia,
      rotinaAlimentarRegular: !!registro.rotinaAlimentarRegular,
      seletividadeAlimentar: !!registro.seletividadeAlimentar,
      recusaPersistente: !!registro.recusaPersistente,
      engasgosFrequentes: !!registro.engasgosFrequentes,
      vomitosRecorrentes: !!registro.vomitosRecorrentes,
      constipacao: !!registro.constipacao,
      diarreiaRecorrente: !!registro.diarreiaRecorrente,
      dificuldadeGanhoPesoPercebida: !!registro.dificuldadeGanhoPesoPercebida,
      familiaTranquilaGanhoPesoAtual: !!registro.familiaTranquilaGanhoPesoAtual,
      preocupacaoFamilia: !!registro.preocupacaoFamilia,
      observacao: registro.observacao ?? ''
    });
  }

  cancelarEdicao(): void {
    this.editandoId.set('');
    this.form.reset({
      dataRegistro: this.formatarEntradaData(this.dataMaximaIso),
      tipoLeite: 'NAO_INFORMADO',
      estagioAlimentar: 'NAO_INFORMADO',
      idadeInicioAlimentacaoComplementarMeses: '',
      refeicoesPorDia: '',
      consomeAgua: false,
      usaMamadeira: false,
      usaCopo: false,
      usaColher: false,
      blwMisto: false,
      autoalimentacao: false,
      texturaPredominante: 'NAO_INFORMADO',
      consomeFrutas: false,
      consomeLegumesVerduras: false,
      consomeLegumes: false,
      consomeVerduras: false,
      consomeCereaisTuberculos: false,
      consomeFeijoesLeguminosas: false,
      consomeCarnesOvos: false,
      ultraprocessadosFrequentes: false,
      bebidasAdocadas: false,
      acucarAdicionado: false,
      salAdicionado: false,
      telasDuranteRefeicoes: false,
      refeicoesEmFamilia: false,
      rotinaAlimentarRegular: false,
      seletividadeAlimentar: false,
      recusaPersistente: false,
      engasgosFrequentes: false,
      vomitosRecorrentes: false,
      constipacao: false,
      diarreiaRecorrente: false,
      dificuldadeGanhoPesoPercebida: false,
      familiaTranquilaGanhoPesoAtual: false,
      preocupacaoFamilia: false,
      observacao: ''
    });
  }

  formatarData(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`));
  }

  labelTipoLeite(valor: TipoLeiteAlimentacao): string {
    return this.tiposLeite.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  labelEstagio(valor: EstagioAlimentar): string {
    return this.estagios.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  labelTextura(valor: TexturaAlimentar): string {
    return this.texturas.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  possuiAlgumaAnalise(registro: RegistroAlimentacao | null): boolean {
    if (!registro) {
      return false;
    }
    return registro.analise.rotina.length > 0 || registro.analise.conversaConsulta.length > 0 || registro.analise.habitosApoio.length > 0;
  }

  private criarRequest(): SalvarRegistroAlimentacaoRequest {
    const valor = this.form.getRawValue();
    return {
      dataRegistro: this.lerData(valor.dataRegistro),
      tipoLeite: valor.tipoLeite,
      estagioAlimentar: valor.estagioAlimentar,
      idadeInicioAlimentacaoComplementarMeses: this.lerInteiro(valor.idadeInicioAlimentacaoComplementarMeses, 'idade de início da alimentação complementar', 0, 24),
      refeicoesPorDia: this.lerInteiro(valor.refeicoesPorDia, 'número de refeições por dia', 0, 10),
      consomeAgua: valor.consomeAgua,
      usaMamadeira: valor.usaMamadeira,
      usaCopo: valor.usaCopo,
      usaColher: valor.usaColher,
      blwMisto: valor.blwMisto,
      autoalimentacao: valor.autoalimentacao,
      texturaPredominante: valor.texturaPredominante,
      consomeFrutas: valor.consomeFrutas,
      consomeLegumesVerduras: !!valor.consomeLegumes || !!valor.consomeVerduras,
      consomeLegumes: valor.consomeLegumes,
      consomeVerduras: valor.consomeVerduras,
      consomeCereaisTuberculos: valor.consomeCereaisTuberculos,
      consomeFeijoesLeguminosas: valor.consomeFeijoesLeguminosas,
      consomeCarnesOvos: valor.consomeCarnesOvos,
      ultraprocessadosFrequentes: valor.ultraprocessadosFrequentes,
      bebidasAdocadas: valor.bebidasAdocadas,
      acucarAdicionado: valor.acucarAdicionado,
      salAdicionado: valor.salAdicionado,
      telasDuranteRefeicoes: valor.telasDuranteRefeicoes,
      refeicoesEmFamilia: valor.refeicoesEmFamilia,
      rotinaAlimentarRegular: valor.rotinaAlimentarRegular,
      seletividadeAlimentar: valor.seletividadeAlimentar,
      recusaPersistente: valor.recusaPersistente,
      engasgosFrequentes: valor.engasgosFrequentes,
      vomitosRecorrentes: valor.vomitosRecorrentes,
      constipacao: valor.constipacao,
      diarreiaRecorrente: valor.diarreiaRecorrente,
      dificuldadeGanhoPesoPercebida: valor.dificuldadeGanhoPesoPercebida,
      familiaTranquilaGanhoPesoAtual: valor.familiaTranquilaGanhoPesoAtual,
      preocupacaoFamilia: valor.preocupacaoFamilia,
      observacao: valor.observacao?.trim() || null
    };
  }

  private lerData(valor: string | null | undefined): string {
    const texto = (valor ?? '').trim();
    const partes = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(texto);
    if (!partes) {
      throw new Error('Informe a data no formato dd/mm/aaaa.');
    }

    const dia = Number(partes[1]);
    const mes = Number(partes[2]);
    const ano = Number(partes[3]);
    const data = new Date(Date.UTC(ano, mes - 1, dia));
    const dataExiste = data.getUTCFullYear() === ano && data.getUTCMonth() === mes - 1 && data.getUTCDate() === dia;
    if (!dataExiste) {
      throw new Error('Informe uma data válida.');
    }

    const iso = `${ano.toString().padStart(4, '0')}-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}`;
    if (iso > this.dataMaximaIso) {
      throw new Error('A data não pode estar no futuro.');
    }
    return iso;
  }

  private lerInteiro(valor: string | null | undefined, label: string, minimo: number, maximo: number): number | null {
    const texto = (valor ?? '').trim();
    if (!texto) {
      return null;
    }
    if (!/^\d+$/.test(texto)) {
      throw new Error(`Informe ${label} usando números inteiros.`);
    }
    const numero = Number(texto);
    if (numero < minimo || numero > maximo) {
      throw new Error(`O campo ${label} está fora do limite esperado.`);
    }
    return numero;
  }

  private formatarInteiro(valor?: number | null): string {
    return valor === null || valor === undefined ? '' : String(valor);
  }

  private formatarEntradaData(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  private compararRegistrosRecentes(a: RegistroAlimentacao, b: RegistroAlimentacao): number {
    const porData = b.dataRegistro.localeCompare(a.dataRegistro);
    if (porData !== 0) {
      return porData;
    }
    const momentoA = a.atualizadoEm ?? a.criadoEm;
    const momentoB = b.atualizadoEm ?? b.criadoEm;
    return momentoB.localeCompare(momentoA);
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar a alimentação agora.';
  }
}
