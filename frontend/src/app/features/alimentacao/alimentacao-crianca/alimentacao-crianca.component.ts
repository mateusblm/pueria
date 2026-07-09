import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { AlimentoRegistroAlimentacao, EstagioAlimentar, GrupoAlimento, RegistroAlimentacao, SalvarRegistroAlimentacaoRequest, TexturaAlimentar, TipoLeiteAlimentacao } from '../../../shared/models/alimentacao.model';
import { CriancasService } from '../../criancas/criancas.service';
import { AlimentacaoService } from '../alimentacao.service';

type Opcao<T extends string> = { valor: T; label: string };
type GrupoCatalogo = { valor: GrupoAlimento | 'TODOS'; label: string };

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
  readonly modalAlimentosAberta = signal(false);
  readonly buscaAlimento = signal('');
  readonly grupoAlimentoAtivo = signal<GrupoAlimento | 'TODOS'>('TODOS');
  readonly alimentosSelecionados = signal<AlimentoRegistroAlimentacao[]>([]);
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly gruposAlimentos: GrupoCatalogo[] = [
    { valor: 'TODOS', label: 'Todos' },
    { valor: 'FRUTA', label: 'Frutas' },
    { valor: 'LEGUME', label: 'Legumes' },
    { valor: 'VERDURA', label: 'Verduras' },
    { valor: 'RAIZ_TUBERCULO', label: 'Raízes' },
    { valor: 'FEIJAO_LEGUMINOSA', label: 'Feijões' },
    { valor: 'CEREAL', label: 'Cereais' },
    { valor: 'PROTEINA', label: 'Proteínas' }
  ];

  readonly catalogoAlimentos: AlimentoRegistroAlimentacao[] = [
    { codigo: 'banana', nome: 'Banana', grupo: 'FRUTA' },
    { codigo: 'maca', nome: 'Maçã', grupo: 'FRUTA' },
    { codigo: 'mamao', nome: 'Mamão', grupo: 'FRUTA' },
    { codigo: 'pera', nome: 'Pera', grupo: 'FRUTA' },
    { codigo: 'laranja', nome: 'Laranja', grupo: 'FRUTA' },
    { codigo: 'abacate', nome: 'Abacate', grupo: 'FRUTA' },
    { codigo: 'manga', nome: 'Manga', grupo: 'FRUTA' },
    { codigo: 'melancia', nome: 'Melancia', grupo: 'FRUTA' },
    { codigo: 'melao', nome: 'Melão', grupo: 'FRUTA' },
    { codigo: 'uva', nome: 'Uva', grupo: 'FRUTA' },
    { codigo: 'morango', nome: 'Morango', grupo: 'FRUTA' },
    { codigo: 'goiaba', nome: 'Goiaba', grupo: 'FRUTA' },
    { codigo: 'abacaxi', nome: 'Abacaxi', grupo: 'FRUTA' },
    { codigo: 'ameixa', nome: 'Ameixa', grupo: 'FRUTA' },
    { codigo: 'cenoura', nome: 'Cenoura', grupo: 'LEGUME' },
    { codigo: 'abobora', nome: 'Abóbora', grupo: 'LEGUME' },
    { codigo: 'chuchu', nome: 'Chuchu', grupo: 'LEGUME' },
    { codigo: 'abobrinha', nome: 'Abobrinha', grupo: 'LEGUME' },
    { codigo: 'beterraba', nome: 'Beterraba', grupo: 'LEGUME' },
    { codigo: 'brocolis', nome: 'Brócolis', grupo: 'LEGUME' },
    { codigo: 'couve-flor', nome: 'Couve-flor', grupo: 'LEGUME' },
    { codigo: 'quiabo', nome: 'Quiabo', grupo: 'LEGUME' },
    { codigo: 'vagem', nome: 'Vagem', grupo: 'LEGUME' },
    { codigo: 'tomate', nome: 'Tomate', grupo: 'LEGUME' },
    { codigo: 'berinjela', nome: 'Berinjela', grupo: 'LEGUME' },
    { codigo: 'pepino', nome: 'Pepino', grupo: 'LEGUME' },
    { codigo: 'alface', nome: 'Alface', grupo: 'VERDURA' },
    { codigo: 'couve', nome: 'Couve', grupo: 'VERDURA' },
    { codigo: 'espinafre', nome: 'Espinafre', grupo: 'VERDURA' },
    { codigo: 'agriao', nome: 'Agrião', grupo: 'VERDURA' },
    { codigo: 'rucula', nome: 'Rúcula', grupo: 'VERDURA' },
    { codigo: 'repolho', nome: 'Repolho', grupo: 'VERDURA' },
    { codigo: 'acelga', nome: 'Acelga', grupo: 'VERDURA' },
    { codigo: 'batata', nome: 'Batata', grupo: 'RAIZ_TUBERCULO' },
    { codigo: 'batata-doce', nome: 'Batata-doce', grupo: 'RAIZ_TUBERCULO' },
    { codigo: 'mandioca', nome: 'Mandioca/aipim', grupo: 'RAIZ_TUBERCULO' },
    { codigo: 'inhame', nome: 'Inhame', grupo: 'RAIZ_TUBERCULO' },
    { codigo: 'cara', nome: 'Cará', grupo: 'RAIZ_TUBERCULO' },
    { codigo: 'mandioquinha', nome: 'Mandioquinha', grupo: 'RAIZ_TUBERCULO' },
    { codigo: 'feijao-carioca', nome: 'Feijão carioca', grupo: 'FEIJAO_LEGUMINOSA' },
    { codigo: 'feijao-preto', nome: 'Feijão preto', grupo: 'FEIJAO_LEGUMINOSA' },
    { codigo: 'lentilha', nome: 'Lentilha', grupo: 'FEIJAO_LEGUMINOSA' },
    { codigo: 'grao-de-bico', nome: 'Grão-de-bico', grupo: 'FEIJAO_LEGUMINOSA' },
    { codigo: 'ervilha', nome: 'Ervilha', grupo: 'FEIJAO_LEGUMINOSA' },
    { codigo: 'arroz', nome: 'Arroz', grupo: 'CEREAL' },
    { codigo: 'aveia', nome: 'Aveia', grupo: 'CEREAL' },
    { codigo: 'milho', nome: 'Milho', grupo: 'CEREAL' },
    { codigo: 'macarrao', nome: 'Macarrão', grupo: 'CEREAL' },
    { codigo: 'cuscuz', nome: 'Cuscuz de milho', grupo: 'CEREAL' },
    { codigo: 'frango', nome: 'Frango', grupo: 'PROTEINA' },
    { codigo: 'carne-bovina', nome: 'Carne bovina', grupo: 'PROTEINA' },
    { codigo: 'peixe', nome: 'Peixe', grupo: 'PROTEINA' },
    { codigo: 'ovo', nome: 'Ovo', grupo: 'PROTEINA' }
  ];

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
  readonly alimentosFiltrados = computed(() => {
    const grupo = this.grupoAlimentoAtivo();
    const busca = this.normalizarTexto(this.buscaAlimento());
    return this.catalogoAlimentos.filter((alimento) => {
      const combinaGrupo = grupo === 'TODOS' || alimento.grupo === grupo;
      const combinaBusca = !busca || this.normalizarTexto(alimento.nome).includes(busca);
      return combinaGrupo && combinaBusca;
    });
  });
  readonly resumoVariedade = computed(() => this.resumirAlimentos(this.alimentosSelecionados()));

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

  abrirModalAlimentos(): void {
    this.modalAlimentosAberta.set(true);
  }

  fecharModalAlimentos(): void {
    this.modalAlimentosAberta.set(false);
    this.buscaAlimento.set('');
    this.grupoAlimentoAtivo.set('TODOS');
  }

  alterarBuscaAlimento(evento: Event): void {
    this.buscaAlimento.set((evento.target as HTMLInputElement).value);
  }

  selecionarGrupoAlimento(grupo: GrupoAlimento | 'TODOS'): void {
    this.grupoAlimentoAtivo.set(grupo);
  }

  alternarAlimento(alimento: AlimentoRegistroAlimentacao): void {
    this.alimentosSelecionados.update((selecionados) => {
      if (selecionados.some((item) => item.codigo === alimento.codigo)) {
        return selecionados.filter((item) => item.codigo !== alimento.codigo);
      }
      return [...selecionados, alimento].sort((a, b) => a.grupo.localeCompare(b.grupo) || a.nome.localeCompare(b.nome, 'pt-BR'));
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
      consomeLegumesVerduras: this.possuiGrupoSelecionado(alimentos, 'LEGUME') || this.possuiGrupoSelecionado(alimentos, 'VERDURA'),
      consomeLegumes: this.possuiGrupoSelecionado(alimentos, 'LEGUME'),
      consomeVerduras: this.possuiGrupoSelecionado(alimentos, 'VERDURA'),
      consomeCereaisTuberculos: this.possuiGrupoSelecionado(alimentos, 'CEREAL') || this.possuiGrupoSelecionado(alimentos, 'RAIZ_TUBERCULO'),
      consomeFeijoesLeguminosas: this.possuiGrupoSelecionado(alimentos, 'FEIJAO_LEGUMINOSA'),
      consomeCarnesOvos: this.possuiGrupoSelecionado(alimentos, 'PROTEINA'),
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
      observacao: valor.observacao?.trim() || null,
      alimentosOferecidos: alimentos
    };
  }

  private possuiGrupoSelecionado(alimentos: AlimentoRegistroAlimentacao[], grupo: GrupoAlimento): boolean {
    return alimentos.some((alimento) => alimento.grupo === grupo);
  }

  private resumirAlimentos(alimentos: AlimentoRegistroAlimentacao[]): Record<GrupoAlimento, number> {
    return alimentos.reduce((resumo, alimento) => {
      resumo[alimento.grupo] = (resumo[alimento.grupo] ?? 0) + 1;
      return resumo;
    }, {
      FRUTA: 0,
      LEGUME: 0,
      VERDURA: 0,
      RAIZ_TUBERCULO: 0,
      FEIJAO_LEGUMINOSA: 0,
      CEREAL: 0,
      PROTEINA: 0
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
