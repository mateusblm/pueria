import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { ContextoUsoTela, RegistroTelas, SalvarRegistroTelasRequest, TipoConteudoTela, TipoDispositivoTela } from '../../../shared/models/telas.model';
import { CriancasService } from '../../criancas/criancas.service';
import { TelasService } from '../telas.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { ToastService } from '../../../core/toast/toast.service';
import { RegistroRapidoComponent } from '../../../shared/components/registro-rapido/registro-rapido.component';

type Opcao<T extends string> = { valor: T; label: string };

@Component({
  selector: 'app-telas-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent, RegistroRapidoComponent],
  templateUrl: './telas-crianca.component.html',
  styleUrl: './telas-crianca.component.scss'
})
export class TelasCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly telasService = inject(TelasService);
  private readonly toast = inject(ToastService);

  readonly crianca = signal<Crianca | null>(null);

  rotaRetorno(): string[] {
    return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento'
      ? ['/acompanhamento']
      : ['/criancas', this.route.snapshot.paramMap.get('id') ?? ''];
  }

  textoRetorno(): string {
    return this.route.snapshot.queryParamMap.get('origem') === 'acompanhamento' ? 'Acompanhamento' : 'Perfil';
  }
  readonly registros = signal<RegistroTelas[]>([]);
  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly aviso = signal('');
  private readonly notificarErro = effect(() => {
    const mensagem = this.erro();
    if (mensagem) this.toast.erro(mensagem);
  });
  private readonly notificarSucesso = effect(() => {
    const mensagem = this.aviso();
    if (mensagem) this.toast.sucesso(mensagem);
  });
  readonly editandoId = signal('');
  readonly registroAberto = signal(false);
  readonly etapaRegistro = signal<1 | 2>(1);
  readonly dataMaximaIso = new Date().toISOString().slice(0, 10);

  readonly tiposConteudo: Opcao<TipoConteudoTela>[] = [
    { valor: 'NAO_INFORMADO', label: 'Não informado' },
    { valor: 'VIDEO_PASSIVO', label: 'Vídeos ou desenhos' },
    { valor: 'VIDEOCHAMADA', label: 'Videochamada com família' },
    { valor: 'EDUCATIVO_INTERATIVO', label: 'Conteúdo interativo acompanhado' },
    { valor: 'JOGOS', label: 'Jogos' },
    { valor: 'MUSICA_AUDIOVISUAL', label: 'Música com vídeo' },
    { valor: 'OUTRO', label: 'Outro' }
  ];

  readonly dispositivos: Opcao<TipoDispositivoTela>[] = [
    { valor: 'CELULAR', label: 'Celular' },
    { valor: 'TABLET', label: 'Tablet/iPad' },
    { valor: 'TV', label: 'TV' }
  ];

  readonly form = this.fb.group({
    dataRegistro: ['', Validators.required],
    minutosDiaSemana: [''],
    minutosFimSemana: [''],
    tipoConteudoPredominante: this.fb.nonNullable.control<TipoConteudoTela>('NAO_INFORMADO', Validators.required),
    usaCelular: [false],
    conteudoCelular: this.fb.nonNullable.control<TipoConteudoTela>('NAO_INFORMADO'),
    usaTablet: [false],
    conteudoTablet: this.fb.nonNullable.control<TipoConteudoTela>('NAO_INFORMADO'),
    usaTv: [false],
    conteudoTv: this.fb.nonNullable.control<TipoConteudoTela>('NAO_INFORMADO'),
    telaAoAcordar: [false],
    telaDuranteRefeicoes: [false],
    telaAntesDormir: [false],
    telaParaAcalmar: [false],
    telaEmSegundoPlano: [false],
    usoAcompanhadoAdulto: [false],
    conteudoAdultoSupervisionado: [false],
    criancaEscolheConteudoLivremente: [false],
    videochamadaFamilia: [false],
    autoplayAtivo: [false],
    notificacoesAtivas: [false],
    dispositivoNoQuarto: [false],
    brincaAoArLivre: [false],
    leituraBrincadeiraSemTela: [false],
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
      registros: this.telasService.listar(criancaId)
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

  avancarEtapa(): void {
    this.erro.set('');
    try {
      this.lerData(this.form.controls.dataRegistro.value);
      this.lerHorasParaMinutos(this.form.controls.minutosDiaSemana.value, 'tempo em dias de semana');
      this.lerHorasParaMinutos(this.form.controls.minutosFimSemana.value, 'tempo em fim de semana');
      this.etapaRegistro.set(2);
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise os dados de tempo de tela.');
    }
  }

  voltarEtapa(): void {
    this.erro.set('');
    this.etapaRegistro.set(1);
  }

  salvar(): void {
    this.erro.set('');
    this.aviso.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro.set('Revise o registro antes de salvar.');
      return;
    }

    let request: SalvarRegistroTelasRequest;
    try {
      request = this.criarRequest();
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro de telas.');
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
      ? this.telasService.atualizar(criancaId, editandoId, request)
      : this.telasService.registrar(criancaId, request);

    requisicao
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: (registro) => {
          this.registros.update((registros) => {
            const semAtual = registros.filter((item) => item.id !== registro.id);
            return [...semAtual, registro];
          });
          this.cancelarEdicao();
          this.registroAberto.set(false);
          this.aviso.set('Registro de telas salvo.');
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  editar(registro: RegistroTelas): void {
    this.editandoId.set(registro.id);
    this.etapaRegistro.set(1);
    this.erro.set('');
    this.aviso.set('');
    this.registroAberto.set(true);
    this.form.patchValue({
      dataRegistro: this.formatarEntradaData(registro.dataRegistro),
      minutosDiaSemana: this.formatarHorasEntrada(registro.minutosDiaSemana),
      minutosFimSemana: this.formatarHorasEntrada(registro.minutosFimSemana),
      tipoConteudoPredominante: registro.tipoConteudoPredominante,
      usaCelular: this.possuiDispositivo(registro.contextosUso, 'CELULAR'),
      conteudoCelular: this.conteudoDoDispositivo(registro.contextosUso, 'CELULAR'),
      usaTablet: this.possuiDispositivo(registro.contextosUso, 'TABLET'),
      conteudoTablet: this.conteudoDoDispositivo(registro.contextosUso, 'TABLET'),
      usaTv: this.possuiDispositivo(registro.contextosUso, 'TV'),
      conteudoTv: this.conteudoDoDispositivo(registro.contextosUso, 'TV'),
      telaAoAcordar: !!registro.telaAoAcordar,
      telaDuranteRefeicoes: !!registro.telaDuranteRefeicoes,
      telaAntesDormir: !!registro.telaAntesDormir,
      telaParaAcalmar: !!registro.telaParaAcalmar,
      telaEmSegundoPlano: !!registro.telaEmSegundoPlano,
      usoAcompanhadoAdulto: !!registro.usoAcompanhadoAdulto,
      conteudoAdultoSupervisionado: !!registro.conteudoAdultoSupervisionado,
      criancaEscolheConteudoLivremente: !!registro.criancaEscolheConteudoLivremente,
      videochamadaFamilia: !!registro.videochamadaFamilia,
      autoplayAtivo: !!registro.autoplayAtivo,
      notificacoesAtivas: !!registro.notificacoesAtivas,
      dispositivoNoQuarto: !!registro.dispositivoNoQuarto,
      brincaAoArLivre: !!registro.brincaAoArLivre,
      leituraBrincadeiraSemTela: !!registro.leituraBrincadeiraSemTela,
      preocupacaoFamilia: !!registro.preocupacaoFamilia,
      observacao: registro.observacao ?? ''
    });
  }

  cancelarEdicao(): void {
    this.editandoId.set('');
    this.etapaRegistro.set(1);
    this.form.reset({
      dataRegistro: this.formatarEntradaData(this.dataMaximaIso),
      minutosDiaSemana: '',
      minutosFimSemana: '',
      tipoConteudoPredominante: 'NAO_INFORMADO',
      usaCelular: false,
      conteudoCelular: 'NAO_INFORMADO',
      usaTablet: false,
      conteudoTablet: 'NAO_INFORMADO',
      usaTv: false,
      conteudoTv: 'NAO_INFORMADO',
      telaAoAcordar: false,
      telaDuranteRefeicoes: false,
      telaAntesDormir: false,
      telaParaAcalmar: false,
      telaEmSegundoPlano: false,
      usoAcompanhadoAdulto: false,
      conteudoAdultoSupervisionado: false,
      criancaEscolheConteudoLivremente: false,
      videochamadaFamilia: false,
      autoplayAtivo: false,
      notificacoesAtivas: false,
      dispositivoNoQuarto: false,
      brincaAoArLivre: false,
      leituraBrincadeiraSemTela: false,
      preocupacaoFamilia: false,
      observacao: ''
    });
  }

  abrirRegistro(): void {
    this.erro.set('');
    this.aviso.set('');
    this.etapaRegistro.set(1);
    this.registroAberto.set(true);
  }

  fecharRegistro(): void {
    this.registroAberto.set(false);
    this.cancelarEdicao();
  }

  formatarData(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`));
  }

  labelTipoConteudo(valor: TipoConteudoTela): string {
    return this.tiposConteudo.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  formatarDuracao(minutos?: number | null): string {
    if (minutos === null || minutos === undefined) {
      return 'Não informado';
    }
    if (minutos < 60) {
      return `${minutos} min`;
    }
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    return resto === 0 ? `${horas}h` : `${horas}h${resto.toString().padStart(2, '0')}`;
  }

  classeTempo(registro: RegistroTelas): string {
    return registro.analise.classificacaoTempo === 'DENTRO_DA_REFERENCIA'
      ? 'telas-sintese__badge--ok'
      : 'telas-sintese__badge--atencao';
  }

  textoReferencia(registro: RegistroTelas): string {
    const maximo = registro.analise.minutosReferenciaMaximo;
    if (maximo === 0) {
      return 'Evitar telas de rotina';
    }
    return `Até ${this.formatarDuracao(maximo)} por dia`;
  }

  possuiAlgumaAnalise(registro: RegistroTelas | null): boolean {
    if (!registro) {
      return false;
    }
    return registro.analise.rotina.length > 0 || registro.analise.conversaConsulta.length > 0 || registro.analise.habitosApoio.length > 0;
  }

  private possuiDispositivo(contextos: ContextoUsoTela[] | undefined, dispositivo: TipoDispositivoTela): boolean {
    return (contextos ?? []).some((contexto) => contexto.dispositivo === dispositivo);
  }

  private conteudoDoDispositivo(contextos: ContextoUsoTela[] | undefined, dispositivo: TipoDispositivoTela): TipoConteudoTela {
    return (contextos ?? []).find((contexto) => contexto.dispositivo === dispositivo)?.conteudo ?? 'NAO_INFORMADO';
  }

  private criarRequest(): SalvarRegistroTelasRequest {
    const valor = this.form.getRawValue();
    return {
      dataRegistro: this.lerData(valor.dataRegistro),
      minutosDiaSemana: this.lerHorasParaMinutos(valor.minutosDiaSemana, 'tempo em dias de semana'),
      minutosFimSemana: this.lerHorasParaMinutos(valor.minutosFimSemana, 'tempo em fim de semana'),
      tipoConteudoPredominante: valor.tipoConteudoPredominante,
      contextosUso: [
        valor.usaCelular ? { dispositivo: 'CELULAR', conteudo: valor.conteudoCelular } : null,
        valor.usaTablet ? { dispositivo: 'TABLET', conteudo: valor.conteudoTablet } : null,
        valor.usaTv ? { dispositivo: 'TV', conteudo: valor.conteudoTv } : null
      ].filter((contexto): contexto is ContextoUsoTela => contexto !== null),
      telaAoAcordar: valor.telaAoAcordar,
      telaDuranteRefeicoes: valor.telaDuranteRefeicoes,
      telaAntesDormir: valor.telaAntesDormir,
      telaParaAcalmar: valor.telaParaAcalmar,
      telaEmSegundoPlano: valor.telaEmSegundoPlano,
      usoAcompanhadoAdulto: valor.usoAcompanhadoAdulto,
      conteudoAdultoSupervisionado: valor.conteudoAdultoSupervisionado,
      criancaEscolheConteudoLivremente: valor.criancaEscolheConteudoLivremente,
      videochamadaFamilia: valor.videochamadaFamilia,
      autoplayAtivo: valor.autoplayAtivo,
      notificacoesAtivas: valor.notificacoesAtivas,
      dispositivoNoQuarto: valor.dispositivoNoQuarto,
      brincaAoArLivre: valor.brincaAoArLivre,
      leituraBrincadeiraSemTela: valor.leituraBrincadeiraSemTela,
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

  private lerHorasParaMinutos(valor: string | null | undefined, label: string): number | null {
    const texto = (valor ?? '').trim().replace(',', '.');
    if (!texto) {
      return null;
    }
    if (!/^\d+(\.\d{1,2})?$/.test(texto)) {
      throw new Error(`Informe ${label} em horas por dia. Exemplo: 1,5.`);
    }
    const horas = Number(texto);
    if (horas < 0 || horas > 24) {
      throw new Error(`O campo ${label} deve ficar entre 0 e 24 horas por dia.`);
    }
    return Math.round(horas * 60);
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

  private formatarHorasEntrada(valor?: number | null): string {
    if (valor === null || valor === undefined) {
      return '';
    }
    const horas = valor / 60;
    return Number.isInteger(horas) ? String(horas) : String(Number(horas.toFixed(2))).replace('.', ',');
  }

  private formatarEntradaData(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  private compararRegistrosRecentes(a: RegistroTelas, b: RegistroTelas): number {
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
    return 'Não foi possível carregar telas agora.';
  }
}
