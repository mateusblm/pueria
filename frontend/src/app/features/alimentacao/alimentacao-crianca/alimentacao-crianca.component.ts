import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { AceitacaoAlimento, AlimentoRegistroAlimentacao, ClassificacaoGluten, EstagioAlimentar, GrupoAlimento, OrigemPreparoAlimento, RegistroAlimentacao, SalvarRegistroAlimentacaoRequest, SituacaoSinaisOferta, TexturaAlimentar, TipoLeiteAlimentacao, TipoOrigemAlimento } from '../../../shared/models/alimentacao.model';
import { CriancasService } from '../../criancas/criancas.service';
import { AlimentacaoService } from '../alimentacao.service';
import { CATALOGO_ALIMENTOS, CatalogoAlimento, ORIENTACOES_GRUPOS } from './catalogo-alimentos';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';

type Opcao<T extends string> = { valor: T; label: string };
type FiltroCatalogo = GrupoAlimento | 'TODOS' | 'ALERGENICOS' | 'LEGUMES_E_FOLHAS' | 'GRAOS_E_LEGUMINOSAS' | 'FONTES_ANIMAIS';
type GrupoCatalogo = { valor: FiltroCatalogo; label: string };

@Component({
  selector: 'app-alimentacao-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent],
  templateUrl: './alimentacao-crianca.component.html',
  styleUrl: './alimentacao-crianca.component.scss'
})
export class AlimentacaoCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly alimentacaoService = inject(AlimentacaoService);

  readonly crianca = signal<Crianca | null>(null);

  rotaRetorno(): string[] {
    return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento'
      ? ['/acompanhamento']
      : ['/criancas', this.route.snapshot.paramMap.get('id') ?? ''];
  }

  textoRetorno(): string {
    return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento' ? 'Acompanhamento' : 'Perfil';
  }
  readonly registros = signal<RegistroAlimentacao[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly aviso = signal('');
  readonly editandoId = signal('');
  readonly modalAlimentosAberta = signal(false);
  readonly buscaAlimento = signal('');
  readonly grupoAlimentoAtivo = signal<FiltroCatalogo>('TODOS');
  readonly alimentosSelecionados = signal<AlimentoRegistroAlimentacao[]>([]);
  readonly alimentoEmDetalhe = signal<string | null>(null);
  readonly novaDataReexposicao = signal('');
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly gruposAlimentos: GrupoCatalogo[] = [
    { valor: 'TODOS', label: 'Todos' },
    { valor: 'ALERGENICOS', label: 'Atenção e rastreabilidade' },
    { valor: 'FRUTA', label: 'Frutas' },
    { valor: 'LEGUME_HORTALICA_FRUTO', label: 'Legumes' },
    { valor: 'VERDURA_FOLHA', label: 'Verduras e folhas' },
    { valor: 'RAIZ_TUBERCULO_AMIDO', label: 'Raízes e tubérculos' },
    { valor: 'CEREAL_GRAO_MASSA', label: 'Cereais e massas' },
    { valor: 'PSEUDOCEREAL_GRAO_ESPECIAL', label: 'Grãos especiais' },
    { valor: 'LEGUMINOSA', label: 'Leguminosas' },
    { valor: 'CARNE_AVE', label: 'Carnes e aves' },
    { valor: 'PEIXE_FRUTO_MAR', label: 'Peixes e frutos do mar' },
    { valor: 'OVO', label: 'Ovos' },
    { valor: 'LEITE_DERIVADO', label: 'Leite e derivados' },
    { valor: 'OLEAGINOSA', label: 'Oleaginosas' },
    { valor: 'SEMENTE', label: 'Sementes' },
    { valor: 'GORDURA', label: 'Gorduras' },
    { valor: 'BEBIDA_LIQUIDO', label: 'Bebidas e líquidos' }
  ];

  readonly catalogoAlimentos: CatalogoAlimento[] = CATALOGO_ALIMENTOS;

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

  readonly origensAlimentos: Opcao<TipoOrigemAlimento>[] = [
    { valor: 'NAO_INFORMADO', label: 'Prefiro não informar' },
    { valor: 'ORGANICO', label: 'Predominantemente orgânicos' },
    { valor: 'CONVENCIONAL', label: 'Predominantemente convencionais' },
    { valor: 'MISTO', label: 'Uma combinação dos dois' }
  ];

  readonly origensPreparo: Opcao<OrigemPreparoAlimento>[] = [
    { valor: 'NAO_INFORMADO', label: 'Prefiro não informar' },
    { valor: 'PREPARO_EM_CASA', label: 'Preparo em casa' },
    { valor: 'PREPARO_NA_ESCOLA_CRECHE', label: 'Preparo na escola/creche' },
    { valor: 'PREPARO_EM_RESTAURANTES', label: 'Preparo em restaurantes' },
    { valor: 'ALIMENTOS_CONGELADOS', label: 'Alimentos congelados' },
    { valor: 'MISTO_CASA_RESTAURANTE', label: 'Misto casa/restaurante' }
  ];

  readonly aceitacoes: Opcao<AceitacaoAlimento>[] = [
    { valor: 'NAO_INFORMADA', label: 'Não informado' },
    { valor: 'BOA', label: 'Aceitou bem' },
    { valor: 'PARCIAL', label: 'Aceitou um pouco' },
    { valor: 'RECUSOU', label: 'Recusou' }
  ];

  readonly classificacoesGluten: Opcao<ClassificacaoGluten>[] = [
    { valor: 'NAO_INFORMADO', label: 'Verificar no rótulo ou preparo' },
    { valor: 'CONTEM', label: 'Contém glúten' },
    { valor: 'NAO_CONTEM', label: 'Não contém glúten' },
    { valor: 'PODE_CONTER_TRACOS', label: 'Pode conter traços' }
  ];

  readonly situacoesSinais: Opcao<SituacaoSinaisOferta>[] = [
    { valor: 'NAO_INFORMADO', label: 'Não informado' },
    { valor: 'NENHUM_PERCEBIDO', label: 'Nenhum sinal percebido' },
    { valor: 'SINAIS_PERCEBIDOS', label: 'Percebi um ou mais sinais' }
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
    tipoOrigemAlimento: this.fb.nonNullable.control<TipoOrigemAlimento>('NAO_INFORMADO'),
    origemPreparoAlimento: this.fb.nonNullable.control<OrigemPreparoAlimento>('NAO_INFORMADO'),
    observacao: ['', Validators.maxLength(1000)]
  });

  readonly registrosOrdenados = computed(() =>
    [...this.registros()].sort((a, b) => this.compararRegistrosRecentes(a, b))
  );
  readonly ultimoRegistro = computed(() => this.registrosOrdenados()[0] ?? null);
  readonly alimentosFiltrados = computed(() => {
    const grupo = this.grupoAlimentoAtivo();
    const busca = this.normalizarTexto(this.buscaAlimento());
    return this.catalogoAlimentos.filter((alimento) => {
      const combinaGrupo = grupo === 'TODOS'
        || (grupo === 'ALERGENICOS' && alimento.alergenico)
        || (grupo === 'LEGUMES_E_FOLHAS' && ['LEGUME_HORTALICA_FRUTO', 'VERDURA_FOLHA'].includes(alimento.grupo))
        || (grupo === 'GRAOS_E_LEGUMINOSAS' && ['CEREAL_GRAO_MASSA', 'PSEUDOCEREAL_GRAO_ESPECIAL', 'LEGUMINOSA'].includes(alimento.grupo))
        || (grupo === 'FONTES_ANIMAIS' && ['CARNE_AVE', 'PEIXE_FRUTO_MAR', 'OVO', 'LEITE_DERIVADO'].includes(alimento.grupo))
        || (grupo !== 'ALERGENICOS'
          && grupo !== 'LEGUMES_E_FOLHAS'
          && grupo !== 'GRAOS_E_LEGUMINOSAS'
          && grupo !== 'FONTES_ANIMAIS'
          && (alimento.grupo === grupo || alimento.gruposRelacionados?.includes(grupo)));
      const combinaBusca = !busca || this.normalizarTexto(alimento.nome).includes(busca);
      return combinaGrupo && combinaBusca;
    });
  });
  readonly resumoVariedade = computed(() => this.resumirAlimentos(this.alimentosSelecionados()));
  readonly orientacaoGrupoAtivo = computed(() => {
    const grupo = this.grupoAlimentoAtivo();
    if (grupo === 'TODOS' || grupo === 'LEGUMES_E_FOLHAS' || grupo === 'GRAOS_E_LEGUMINOSAS' || grupo === 'FONTES_ANIMAIS') {
      return '';
    }
    return ORIENTACOES_GRUPOS[grupo] ?? '';
  });
  readonly alimentoDetalhado = computed(() => {
    const codigo = this.alimentoEmDetalhe();
    return codigo ? this.alimentosSelecionados().find((alimento) => alimento.codigo === codigo) ?? null : null;
  });

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
      tipoOrigemAlimento: registro.tipoOrigemAlimento ?? 'NAO_INFORMADO',
      origemPreparoAlimento: registro.origemPreparoAlimento ?? 'NAO_INFORMADO',
      observacao: registro.observacao ?? ''
    });
    this.alimentosSelecionados.set(registro.alimentosOferecidos ?? []);
  }

  cancelarEdicao(): void {
    this.editandoId.set('');
    this.alimentosSelecionados.set([]);
    this.fecharModalAlimentos();
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
      tipoOrigemAlimento: 'NAO_INFORMADO',
      origemPreparoAlimento: 'NAO_INFORMADO',
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

  labelOrigemAlimento(valor: TipoOrigemAlimento): string {
    return this.origensAlimentos.find((opcao) => opcao.valor === valor)?.label ?? 'Prefiro não informar';
  }

  labelOrigemPreparo(valor: OrigemPreparoAlimento): string {
    return this.origensPreparo.find((opcao) => opcao.valor === valor)?.label ?? 'Prefiro não informar';
  }

  abrirModalAlimentos(grupo: FiltroCatalogo = 'TODOS'): void {
    this.grupoAlimentoAtivo.set(grupo);
    this.modalAlimentosAberta.set(true);
  }

  fecharModalAlimentos(): void {
    this.modalAlimentosAberta.set(false);
    this.alimentoEmDetalhe.set(null);
    this.buscaAlimento.set('');
    this.grupoAlimentoAtivo.set('TODOS');
  }

  alterarBuscaAlimento(evento: Event): void {
    this.buscaAlimento.set((evento.target as HTMLInputElement).value);
  }

  selecionarGrupoAlimento(grupo: FiltroCatalogo): void {
    this.grupoAlimentoAtivo.set(grupo);
  }

  alternarAlimento(alimento: AlimentoRegistroAlimentacao): void {
    this.alimentosSelecionados.update((selecionados) => {
      if (selecionados.some((item) => item.codigo === alimento.codigo)) {
        if (this.alimentoEmDetalhe() === alimento.codigo) {
          this.fecharDetalhesAlimento();
        }
        return selecionados.filter((item) => item.codigo !== alimento.codigo);
      }
      const selecionado: AlimentoRegistroAlimentacao = {
        codigo: alimento.codigo,
        nome: alimento.nome,
        grupo: alimento.grupo,
        alergenico: alimento.alergenico ?? false,
        textura: 'NAO_INFORMADO',
        aceitacao: 'NAO_INFORMADA',
        classificacaoGluten: alimento.classificacaoGluten ?? 'NAO_SE_APLICA',
        datasReexposicao: [],
        situacaoSinais: 'NAO_INFORMADO'
      };
      return [...selecionados, selecionado].sort((a, b) => a.grupo.localeCompare(b.grupo) || a.nome.localeCompare(b.nome, 'pt-BR'));
    });
  }

  alimentoSelecionado(codigo: string): boolean {
    return this.alimentosSelecionados().some((alimento) => alimento.codigo === codigo);
  }

  removerAlimento(codigo: string): void {
    this.alimentosSelecionados.update((selecionados) => selecionados.filter((alimento) => alimento.codigo !== codigo));
  }

  labelGrupoAlimento(grupo: GrupoAlimento): string {
    return this.gruposAlimentos.find((opcao) => opcao.valor === grupo)?.label ?? grupo;
  }

  alimentosPorGrupo(grupo: GrupoAlimento): AlimentoRegistroAlimentacao[] {
    return this.alimentosSelecionados().filter((alimento) => alimento.grupo === grupo);
  }

  abrirDetalhesAlimento(codigo: string): void {
    this.erro.set('');
    this.novaDataReexposicao.set('');
    this.alimentoEmDetalhe.set(codigo);
  }

  fecharDetalhesAlimento(): void {
    this.novaDataReexposicao.set('');
    this.alimentoEmDetalhe.set(null);
  }

  atualizarTextoAlimento(campo: 'formaPreparo' | 'quantidadeAproximada' | 'tipoPeixe' | 'observacao', evento: Event): void {
    const valor = (evento.target as HTMLInputElement | HTMLTextAreaElement).value.trim() || null;
    this.atualizarAlimentoEmDetalhe({ [campo]: valor });
  }

  atualizarOpcaoAlimento(campo: 'textura' | 'aceitacao' | 'classificacaoGluten', evento: Event): void {
    this.atualizarAlimentoEmDetalhe({ [campo]: (evento.target as HTMLSelectElement).value });
  }

  atualizarSituacaoSinais(evento: Event): void {
    const situacaoSinais = (evento.target as HTMLSelectElement).value as SituacaoSinaisOferta;
    const limparSinais = situacaoSinais !== 'SINAIS_PERCEBIDOS';
    this.atualizarAlimentoEmDetalhe({
      situacaoSinais,
      ...(limparSinais ? {
        sintomasPele: false,
        sintomasIntestinais: false,
        sintomasRespiratorios: false,
        alteracaoSono: false,
        alteracaoComportamento: false
      } : {})
    });
  }

  atualizarMarcacaoAlimento(
    campo: 'sintomasPele' | 'sintomasIntestinais' | 'sintomasRespiratorios' | 'alteracaoSono' | 'alteracaoComportamento',
    evento: Event
  ): void {
    const codigo = this.alimentoEmDetalhe();
    const marcado = (evento.target as HTMLInputElement).checked;
    if (!codigo) {
      return;
    }
    this.alimentosSelecionados.update((selecionados) => selecionados.map((alimento) => {
      if (alimento.codigo !== codigo) {
        return alimento;
      }
      const atualizado = { ...alimento, [campo]: marcado };
      return {
        ...atualizado,
        situacaoSinais: this.possuiSinalMarcado(atualizado) ? 'SINAIS_PERCEBIDOS' : 'NAO_INFORMADO'
      };
    }));
  }

  atualizarDataIntroducao(evento: Event): void {
    const valor = (evento.target as HTMLInputElement).value.trim();
    if (!valor) {
      this.atualizarAlimentoEmDetalhe({ dataIntroducao: null });
      return;
    }
    try {
      const dataIntroducao = this.lerData(valor);
      const dataRegistro = this.lerData(this.form.controls.dataRegistro.value);
      if (dataIntroducao > dataRegistro) {
        throw new Error('A primeira oferta não pode ser posterior à data deste registro.');
      }
      this.atualizarAlimentoEmDetalhe({ dataIntroducao });
      this.erro.set('');
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise a data da primeira oferta.');
    }
  }

  dataIntroducaoParaExibicao(alimento: AlimentoRegistroAlimentacao): string {
    return alimento.dataIntroducao ? this.formatarEntradaData(alimento.dataIntroducao) : '';
  }

  alterarNovaDataReexposicao(evento: Event): void {
    this.novaDataReexposicao.set((evento.target as HTMLInputElement).value);
  }

  adicionarReexposicao(): void {
    const alimento = this.alimentoDetalhado();
    if (!alimento?.dataIntroducao) {
      this.erro.set('Informe primeiro a data da primeira oferta.');
      return;
    }
    try {
      const data = this.lerData(this.novaDataReexposicao());
      const dataRegistro = this.lerData(this.form.controls.dataRegistro.value);
      if (data <= alimento.dataIntroducao) {
        throw new Error('A reexposição precisa acontecer depois da primeira oferta.');
      }
      if (data > dataRegistro) {
        throw new Error('A reexposição não pode ser posterior à data deste registro.');
      }
      const datas = [...new Set([...(alimento.datasReexposicao ?? []), data])].sort();
      this.atualizarAlimentoEmDetalhe({ datasReexposicao: datas, repetiuOutroDia: true });
      this.novaDataReexposicao.set('');
      this.erro.set('');
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise a data da reexposição.');
    }
  }

  removerReexposicao(data: string): void {
    const alimento = this.alimentoDetalhado();
    if (!alimento) {
      return;
    }
    const datas = (alimento.datasReexposicao ?? []).filter((item) => item !== data);
    this.atualizarAlimentoEmDetalhe({ datasReexposicao: datas, repetiuOutroDia: datas.length > 0 });
  }

  exibeClassificacaoGluten(alimento: AlimentoRegistroAlimentacao): boolean {
    return alimento.grupo === 'CEREAL_GRAO_MASSA'
      || alimento.grupo === 'PSEUDOCEREAL_GRAO_ESPECIAL'
      || alimento.classificacaoGluten !== 'NAO_SE_APLICA';
  }

  exibeTipoPeixe(alimento: AlimentoRegistroAlimentacao): boolean {
    return alimento.grupo === 'PEIXE_FRUTO_MAR' && alimento.codigo === 'peixe';
  }

  idadeNaIntroducao(alimento: AlimentoRegistroAlimentacao): string {
    const nascimento = this.crianca()?.dataNascimento;
    if (!nascimento || !alimento.dataIntroducao || alimento.dataIntroducao < nascimento) {
      return '';
    }
    const inicio = new Date(`${nascimento}T00:00:00Z`);
    const fim = new Date(`${alimento.dataIntroducao}T00:00:00Z`);
    const dias = Math.floor((fim.getTime() - inicio.getTime()) / 86_400_000);
    if (dias < 60) {
      return `${dias} dia(s) de vida`;
    }
    return `${Math.floor(dias / 30.4375)} mes(es) de vida`;
  }

  possuiDetalhesAlimento(alimento: AlimentoRegistroAlimentacao): boolean {
    return Boolean(
      alimento.dataIntroducao || alimento.formaPreparo || alimento.quantidadeAproximada
      || alimento.tipoPeixe || (alimento.datasReexposicao?.length ?? 0) > 0
      || (alimento.classificacaoGluten && !['NAO_INFORMADO', 'NAO_SE_APLICA'].includes(alimento.classificacaoGluten))
      || (alimento.aceitacao && alimento.aceitacao !== 'NAO_INFORMADA')
      || (alimento.situacaoSinais && alimento.situacaoSinais !== 'NAO_INFORMADO')
      || alimento.sintomasPele || alimento.sintomasIntestinais || alimento.sintomasRespiratorios
      || alimento.alteracaoSono || alimento.alteracaoComportamento || alimento.observacao
    );
  }

  possuiAlgumaAnalise(registro: RegistroAlimentacao | null): boolean {
    if (!registro) {
      return false;
    }
    return registro.analise.rotina.length > 0 || registro.analise.conversaConsulta.length > 0 || registro.analise.habitosApoio.length > 0;
  }

  private criarRequest(): SalvarRegistroAlimentacaoRequest {
    const valor = this.form.getRawValue();
    const alimentos = this.alimentosSelecionados();
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
      consomeFrutas: this.possuiGrupoSelecionado(alimentos, 'FRUTA'),
      consomeLegumesVerduras: this.possuiGrupoSelecionado(alimentos, 'LEGUME_HORTALICA_FRUTO') || this.possuiGrupoSelecionado(alimentos, 'VERDURA_FOLHA'),
      consomeLegumes: this.possuiGrupoSelecionado(alimentos, 'LEGUME_HORTALICA_FRUTO'),
      consomeVerduras: this.possuiGrupoSelecionado(alimentos, 'VERDURA_FOLHA'),
      consomeCereaisTuberculos: this.possuiAlgumGrupoSelecionado(alimentos, ['CEREAL_GRAO_MASSA', 'PSEUDOCEREAL_GRAO_ESPECIAL', 'RAIZ_TUBERCULO_AMIDO']),
      consomeFeijoesLeguminosas: this.possuiGrupoSelecionado(alimentos, 'LEGUMINOSA'),
      consomeCarnesOvos: this.possuiAlgumGrupoSelecionado(alimentos, ['CARNE_AVE', 'PEIXE_FRUTO_MAR', 'OVO']),
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
      tipoOrigemAlimento: valor.tipoOrigemAlimento,
      origemPreparoAlimento: valor.origemPreparoAlimento,
      observacao: valor.observacao?.trim() || null,
      alimentosOferecidos: alimentos
    };
  }

  private atualizarAlimentoEmDetalhe(alteracoes: Partial<AlimentoRegistroAlimentacao>): void {
    const codigo = this.alimentoEmDetalhe();
    if (!codigo) {
      return;
    }
    this.alimentosSelecionados.update((selecionados) => selecionados.map((alimento) =>
      alimento.codigo === codigo ? { ...alimento, ...alteracoes } : alimento
    ));
  }

  private possuiSinalMarcado(alimento: AlimentoRegistroAlimentacao): boolean {
    return Boolean(alimento.sintomasPele || alimento.sintomasIntestinais || alimento.sintomasRespiratorios
      || alimento.alteracaoSono || alimento.alteracaoComportamento);
  }

  private possuiGrupoSelecionado(alimentos: AlimentoRegistroAlimentacao[], grupo: GrupoAlimento): boolean {
    return alimentos.some((alimento) => alimento.grupo === grupo);
  }

  private possuiAlgumGrupoSelecionado(alimentos: AlimentoRegistroAlimentacao[], grupos: GrupoAlimento[]): boolean {
    return alimentos.some((alimento) => grupos.includes(alimento.grupo));
  }

  private resumirAlimentos(alimentos: AlimentoRegistroAlimentacao[]): Record<GrupoAlimento, number> {
    return alimentos.reduce((resumo, alimento) => {
      resumo[alimento.grupo] = (resumo[alimento.grupo] ?? 0) + 1;
      return resumo;
    }, {
      FRUTA: 0,
      LEGUME_HORTALICA_FRUTO: 0,
      VERDURA_FOLHA: 0,
      RAIZ_TUBERCULO_AMIDO: 0,
      CEREAL_GRAO_MASSA: 0,
      PSEUDOCEREAL_GRAO_ESPECIAL: 0,
      LEGUMINOSA: 0,
      CARNE_AVE: 0,
      PEIXE_FRUTO_MAR: 0,
      OVO: 0,
      LEITE_DERIVADO: 0,
      OLEAGINOSA: 0,
      SEMENTE: 0,
      GORDURA: 0,
      BEBIDA_LIQUIDO: 0
    } as Record<GrupoAlimento, number>);
  }

  private normalizarTexto(valor: string): string {
    return valor
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
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
