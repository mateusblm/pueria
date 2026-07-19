import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { AmbienteSono, RegistroSono, SalvarRegistroSonoRequest, SuperficieSono, TipoDespertarNoturno } from '../../../shared/models/sono.model';
import { CriancasService } from '../../criancas/criancas.service';
import { SonoService } from '../sono.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { ToastService } from '../../../core/toast/toast.service';
import { RegistroRapidoComponent } from '../../../shared/components/registro-rapido/registro-rapido.component';

type Opcao<T extends string> = { valor: T; label: string };

@Component({
  selector: 'app-sono-crianca',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent, RegistroRapidoComponent],
  templateUrl: './sono-crianca.component.html',
  styleUrl: './sono-crianca.component.scss'
})
export class SonoCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly criancasService = inject(CriancasService);
  private readonly sonoService = inject(SonoService);
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
  readonly registros = signal<RegistroSono[]>([]);
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

  readonly superficiesSono: Opcao<SuperficieSono>[] = [
    { valor: 'BERCO', label: 'Berço' },
    { valor: 'CAMA_PROPRIA', label: 'Cama própria' },
    { valor: 'CAMA_COMPARTILHADA', label: 'Cama compartilhada' },
    { valor: 'OUTRA', label: 'Outra' },
    { valor: 'NAO_INFORMADA', label: 'Não informado' }
  ];

  readonly ambientesSono: Opcao<AmbienteSono>[] = [
    { valor: 'QUARTO_DOS_RESPONSAVEIS', label: 'Quarto dos responsáveis' },
    { valor: 'QUARTO_DA_PROPRIA_CRIANCA', label: 'Quarto da própria criança' },
    { valor: 'OUTRO', label: 'Outro' },
    { valor: 'NAO_INFORMADO', label: 'Não informado' }
  ];

  readonly tiposDespertar: Opcao<TipoDespertarNoturno>[] = [
    { valor: 'ACORDA_E_MAMA', label: 'Acorda e mama/se alimenta' },
    { valor: 'ACORDA_SEM_SE_ALIMENTAR', label: 'Acorda sem se alimentar' },
    { valor: 'VOLTA_A_DORMIR_RAPIDO', label: 'Volta a dormir rápido' },
    { valor: 'DEMORA_PARA_VOLTAR_A_DORMIR', label: 'Demora para voltar a dormir' }
  ];

  readonly form = this.fb.group({
    dataRegistro: ['', Validators.required],
    horarioDormiu: [''],
    horarioAcordou: [''],
    quantidadeCochilos: [''],
    minutosCochilos: [''],
    despertaresNoturnos: [''],
    dificuldadeIniciarSono: [false],
    rotinaSonoConsistente: [false],
    telasAntesDormir: [false],
    superficieSono: this.fb.nonNullable.control<SuperficieSono>('NAO_INFORMADA', Validators.required),
    ambienteSono: this.fb.nonNullable.control<AmbienteSono>('NAO_INFORMADO', Validators.required),
    tiposDespertarNoturno: this.fb.nonNullable.control<TipoDespertarNoturno[]>([]),
    roncosFrequentes: [false],
    pausasRespiratoriasPercebidas: [false],
    sonoAgitado: [false],
    rangerDentesDuranteSono: [false],
    acordaBemDisposto: [false],
    sonolenciaDiurna: [false],
    dificilDeSerAcordado: [false],
    malHumorado: [false],
    irritado: [false],
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
      registros: this.sonoService.listar(criancaId)
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
      const dormiu = this.lerHorario(this.form.controls.horarioDormiu.value, 'horário de dormir');
      const acordou = this.lerHorario(this.form.controls.horarioAcordou.value, 'horário de acordar');
      if ((dormiu && !acordou) || (!dormiu && acordou)) {
        throw new Error('Informe os dois horários ou deixe ambos em branco por enquanto.');
      }
      if (dormiu && acordou && dormiu === acordou) {
        throw new Error('O horário de dormir e o horário de acordar não podem ser iguais.');
      }
      this.etapaRegistro.set(2);
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise os dados do descanso.');
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
      this.erro.set('Revise os dados informados antes de salvar.');
      return;
    }

    let request: SalvarRegistroSonoRequest;
    try {
      request = this.criarRequest();
    } catch (erro) {
      this.erro.set(erro instanceof Error ? erro.message : 'Revise o registro de sono.');
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
      ? this.sonoService.atualizar(criancaId, editandoId, request)
      : this.sonoService.registrar(criancaId, request);

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
          this.aviso.set('Registro de sono salvo.');
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  editar(registro: RegistroSono): void {
    this.editandoId.set(registro.id);
    this.etapaRegistro.set(1);
    this.erro.set('');
    this.aviso.set('');
    this.registroAberto.set(true);
    this.form.patchValue({
      dataRegistro: this.formatarEntradaData(registro.dataRegistro),
      horarioDormiu: this.formatarHora(registro.horarioDormiu),
      horarioAcordou: this.formatarHora(registro.horarioAcordou),
      quantidadeCochilos: this.formatarInteiro(registro.quantidadeCochilos),
      minutosCochilos: this.formatarInteiro(registro.minutosCochilos),
      despertaresNoturnos: this.formatarInteiro(registro.despertaresNoturnos),
      dificuldadeIniciarSono: !!registro.dificuldadeIniciarSono,
      rotinaSonoConsistente: !!registro.rotinaSonoConsistente,
      telasAntesDormir: !!registro.telasAntesDormir,
      superficieSono: registro.superficieSono,
      ambienteSono: registro.ambienteSono,
      tiposDespertarNoturno: registro.tiposDespertarNoturno ?? [],
      roncosFrequentes: !!registro.roncosFrequentes,
      pausasRespiratoriasPercebidas: !!registro.pausasRespiratoriasPercebidas,
      sonoAgitado: !!registro.sonoAgitado,
      rangerDentesDuranteSono: !!registro.rangerDentesDuranteSono,
      acordaBemDisposto: !!registro.acordaBemDisposto,
      sonolenciaDiurna: !!registro.sonolenciaDiurna,
      dificilDeSerAcordado: !!registro.dificilDeSerAcordado,
      malHumorado: !!registro.malHumorado,
      irritado: !!registro.irritado,
      preocupacaoFamilia: !!registro.preocupacaoFamilia,
      observacao: registro.observacao ?? ''
    });
  }

  cancelarEdicao(): void {
    this.editandoId.set('');
    this.etapaRegistro.set(1);
    this.form.reset({
      dataRegistro: this.formatarEntradaData(this.dataMaximaIso),
      horarioDormiu: '',
      horarioAcordou: '',
      quantidadeCochilos: '',
      minutosCochilos: '',
      despertaresNoturnos: '',
      dificuldadeIniciarSono: false,
      rotinaSonoConsistente: false,
      telasAntesDormir: false,
      superficieSono: 'NAO_INFORMADA',
      ambienteSono: 'NAO_INFORMADO',
      tiposDespertarNoturno: [],
      roncosFrequentes: false,
      pausasRespiratoriasPercebidas: false,
      sonoAgitado: false,
      rangerDentesDuranteSono: false,
      acordaBemDisposto: false,
      sonolenciaDiurna: false,
      dificilDeSerAcordado: false,
      malHumorado: false,
      irritado: false,
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

  labelSuperficieSono(valor: SuperficieSono): string {
    return this.superficiesSono.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  labelAmbienteSono(valor: AmbienteSono): string {
    return this.ambientesSono.find((opcao) => opcao.valor === valor)?.label ?? 'Não informado';
  }

  alternarTipoDespertar(tipo: TipoDespertarNoturno): void {
    const atual = this.form.controls.tiposDespertarNoturno.value;
    this.form.controls.tiposDespertarNoturno.setValue(atual.includes(tipo) ? atual.filter((item) => item !== tipo) : [...atual, tipo]);
  }

  tipoDespertarSelecionado(tipo: TipoDespertarNoturno): boolean {
    return this.form.controls.tiposDespertarNoturno.value.includes(tipo);
  }

  normalizarCampoHorario(campo: 'horarioDormiu' | 'horarioAcordou'): void {
    const controle = this.form.controls[campo];
    const horario = this.normalizarHorario(controle.value);
    if (horario) {
      controle.setValue(horario);
    }
    controle.setErrors(horario && !/^([01]\d|2[0-3]):([0-5]\d)$/.test(horario) ? { horarioInvalido: true } : null);
    controle.markAsTouched();
  }

  formatarDuracao(minutos?: number | null): string {
    if (minutos === null || minutos === undefined) {
      return 'Não informado';
    }
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    if (resto === 0) {
      return `${horas}h`;
    }
    return `${horas}h${resto.toString().padStart(2, '0')}`;
  }

  classeDuracao(registro: RegistroSono): string {
    return registro.analise.classificacaoDuracao === 'FAIXA_ESPERADA' ? 'sono-sintese__badge--ok' : 'sono-sintese__badge--atencao';
  }

  possuiAlgumaAnalise(registro: RegistroSono | null): boolean {
    if (!registro) {
      return false;
    }
    return registro.analise.rotina.length > 0 || registro.analise.conversaConsulta.length > 0 || registro.analise.habitosApoio.length > 0;
  }

  private criarRequest(): SalvarRegistroSonoRequest {
    const valor = this.form.getRawValue();
    const horarioDormiu = this.lerHorario(valor.horarioDormiu, 'horário de dormir');
    const horarioAcordou = this.lerHorario(valor.horarioAcordou, 'horário de acordar');
    if ((horarioDormiu && !horarioAcordou) || (!horarioDormiu && horarioAcordou)) {
      throw new Error('Informe horário de dormir e horário de acordar para calcular o sono noturno.');
    }
    if (horarioDormiu && horarioAcordou && horarioDormiu === horarioAcordou) {
      throw new Error('O horário de dormir e o horário de acordar não podem ser iguais.');
    }

    return {
      dataRegistro: this.lerData(valor.dataRegistro),
      horarioDormiu,
      horarioAcordou,
      quantidadeCochilos: this.lerInteiro(valor.quantidadeCochilos, 'quantidade de cochilos', 0, 12),
      minutosCochilos: this.lerInteiro(valor.minutosCochilos, 'tempo total de cochilos', 0, 1200),
      despertaresNoturnos: this.lerInteiro(valor.despertaresNoturnos, 'despertares noturnos', 0, 30),
      dificuldadeIniciarSono: valor.dificuldadeIniciarSono,
      rotinaSonoConsistente: valor.rotinaSonoConsistente,
      telasAntesDormir: valor.telasAntesDormir,
      superficieSono: valor.superficieSono,
      ambienteSono: valor.ambienteSono,
      tiposDespertarNoturno: valor.tiposDespertarNoturno,
      roncosFrequentes: valor.roncosFrequentes,
      pausasRespiratoriasPercebidas: valor.pausasRespiratoriasPercebidas,
      sonoAgitado: valor.sonoAgitado,
      rangerDentesDuranteSono: valor.rangerDentesDuranteSono,
      acordaBemDisposto: valor.acordaBemDisposto,
      sonolenciaDiurna: valor.sonolenciaDiurna,
      dificilDeSerAcordado: valor.dificilDeSerAcordado,
      malHumorado: valor.malHumorado,
      irritado: valor.irritado,
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

  private lerHorario(valor: string | null | undefined, label: string): string | null {
    const horario = this.normalizarHorario(valor);
    if (!horario) {
      return null;
    }
    const partes = /^([01]\d|2[0-3]):([0-5]\d)$/.exec(horario);
    if (!partes) {
      throw new Error(`Informe ${label} no formato hh:mm. Exemplo: 20:00.`);
    }
    return `${partes[1]}:${partes[2]}`;
  }

  private normalizarHorario(valor: string | null | undefined): string {
    const texto = (valor ?? '').trim().toLowerCase();
    if (!texto) {
      return '';
    }
    const numeros = texto.replace(/\D/g, '');
    if (numeros.length === 3) {
      return `0${numeros[0]}:${numeros.slice(1)}`;
    }
    if (numeros.length === 4) {
      return `${numeros.slice(0, 2)}:${numeros.slice(2)}`;
    }
    return texto.replace(/[h.\s]/g, ':').replace(/:+/g, ':');
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

  private formatarHora(valor?: string | null): string {
    return valor?.slice(0, 5) ?? '';
  }

  private formatarEntradaData(dataIso: string): string {
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  private compararRegistrosRecentes(a: RegistroSono, b: RegistroSono): number {
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
    return 'Não foi possível carregar o sono agora.';
  }
}
