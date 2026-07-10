import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { beforeEach, describe, expect, it } from 'vitest';

import { AlimentoRegistroAlimentacao } from '../../../shared/models/alimentacao.model';
import { CriancasService } from '../../criancas/criancas.service';
import { AlimentacaoService } from '../alimentacao.service';
import { AlimentacaoCriancaComponent } from './alimentacao-crianca.component';

describe('AlimentacaoCriancaComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlimentacaoCriancaComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'crianca-1' } } } },
        { provide: CriancasService, useValue: {} },
        { provide: AlimentacaoService, useValue: {} }
      ]
    }).compileComponents();
  });

  function criarComponenteComAlimento(): { component: AlimentacaoCriancaComponent; alimento: AlimentoRegistroAlimentacao } {
    const component = TestBed.createComponent(AlimentacaoCriancaComponent).componentInstance;
    const alimento: AlimentoRegistroAlimentacao = {
      codigo: 'ovo', nome: 'Ovo', grupo: 'OVO', alergenico: true,
      dataIntroducao: '2026-07-01', situacaoSinais: 'SINAIS_PERCEBIDOS', sintomasPele: true,
      datasReexposicao: []
    };
    component.alimentosSelecionados.set([alimento]);
    component.abrirDetalhesAlimento(alimento.codigo);
    component.form.controls.dataRegistro.setValue('10/07/2026');
    return { component, alimento };
  }

  it('diferencia nenhum sinal percebido de informação ausente', () => {
    const { component } = criarComponenteComAlimento();

    component.atualizarSituacaoSinais({ target: { value: 'NENHUM_PERCEBIDO' } } as unknown as Event);

    const atualizado = component.alimentosSelecionados()[0];
    expect(atualizado.situacaoSinais).toBe('NENHUM_PERCEBIDO');
    expect(atualizado.sintomasPele).toBe(false);
  });

  it('registra reexposições com data posterior à primeira oferta', () => {
    const { component } = criarComponenteComAlimento();
    component.novaDataReexposicao.set('05/07/2026');

    component.adicionarReexposicao();

    expect(component.alimentosSelecionados()[0].datasReexposicao).toEqual(['2026-07-05']);
    expect(component.alimentosSelecionados()[0].repetiuOutroDia).toBe(true);
  });

  it('recusa reexposição anterior à primeira oferta', () => {
    const { component } = criarComponenteComAlimento();
    component.novaDataReexposicao.set('30/06/2026');

    component.adicionarReexposicao();

    expect(component.alimentosSelecionados()[0].datasReexposicao).toEqual([]);
    expect(component.erro()).toBe('A reexposição precisa acontecer depois da primeira oferta.');
  });
});
