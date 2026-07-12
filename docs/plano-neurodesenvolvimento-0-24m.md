# Plano de implementação - Neurodesenvolvimento 0-24 meses

Documento de execução baseado nos materiais clínicos enviados pela dra. em 12/07/2026:

- `App_Neurodesenvolvimento_DOCUMENTO_MESTRE.md`
- `MARCOS- App_Neurodesenvolvimento_0-24m_Especificacao_v2.md`
- `ESTIMULOS PROPOSTOS- Programa_Estimulacao_Mensal_0-24m_Protocolos.md`
- `FERRAMENTA-checklist_marcos_desenvolvimento_v2.html`

## Princípios inegociáveis

- O Pueria acompanha desenvolvimento e apoia a conversa com o pediatra. Não diagnostica, não substitui consulta e não determina conduta clínica.
- Cada marco terá fonte, versão do conteúdo e justificativa clínica rastreáveis.
- Respostas e alertas devem usar idade corrigida para crianças prematuras até 24 meses corrigidos.
- P50 da OMS é uma referência de observação precoce para os seis marcos motores estudados. Não é, isoladamente, prazo para alerta vermelho ou diagnóstico.
- Regressão de habilidade, preocupação persistente da família e combinação longitudinal de respostas merecem prioridade de orientação, sempre com linguagem calma e não diagnóstica.
- Instrumentos validados de triagem são módulos próprios. Não serão simulados ou modificados dentro do checklist de marcos.

## Situação atual

| Frente | Status | Situação no Pueria |
|---|---|---|
| Checkpoints | Parcial | Há marcos entre 2 e 60 meses, mas o conjunto atual é reduzido e segue fontes genéricas CDC/SBP. |
| Respostas | Parcial | Existem três estados próximos à proposta, porém sem o significado de consistência `sim, sempre`. |
| Fontes por marco | Parcial | O texto da fonte é genérico; faltam método, versão e relevância clínica por item. |
| Idade corrigida | Pendente | O módulo de desenvolvimento usa idade cronológica; crescimento já possui cálculo próprio. |
| Histórico longitudinal | Pendente | O registro atual mantém o estado mais recente do marco, sem eventos históricos por checkpoint. |
| Regras de atenção | Parcial | Há orientação de conversa, mas não há motor versionado de trajetória, regressão e prioridade. |
| Estímulos | Pendente | Não há banco de atividades mensais conectado a domínio, idade e resposta. |
| M-CHAT-R/F | Bloqueado | Exige fluxo independente e licença comercial antes da incorporação. |
| Relatório para consulta | Pendente | Deve consolidar marcos, trajetória, atividades e pontos a conversar. |

## Lista de entregas

### Fase 0 - Governança clínica e dados

| ID | Entrega | Status | Critério de aceite |
|---|---|---|---|
| NEU-00 | Registrar esta decisão clínica e o inventário de fontes no repositório. | Implementado | Documento versionado e revisável pelo time clínico. |
| NEU-01 | Criar catálogo versionado de marcos de 0 a 24 meses. | Implementado | Base `NEURO_V2_2026_07` com checkpoint, domínio, texto familiar, fonte, versão e status ativo. |
| NEU-02 | Registrar papel clínico do marco: observação, atenção persistente ou alta relevância. | Implementado | O papel clínico e a marca de vigilância alta são persistidos por item. |
| NEU-03 | Criar matriz de decisão da dra. para os itens divergentes do CDC 2022. | Pendente | A dra. aprova, item a item, checkpoint, fonte e tipo de alerta. |
| NEU-04 | Reinicializar marcos e respostas de teste na troca para o catálogo v2. | Implementado | O ambiente ainda é de desenvolvimento; não há camada de compatibilidade ou migração de respostas legadas. |

### Fase 1 - Checklist de acompanhamento

| ID | Entrega | Status | Critério de aceite |
|---|---|---|---|
| NEU-10 | Substituir o dataset atual pelo conjunto 2, 4, 6, 9, 12, 15, 18 e 24 meses aprovado. | Pendente | Itens, domínios e fontes correspondem à matriz clínica aprovada. |
| NEU-11 | Formalizar respostas `SIM_SEMPRE`, `AS_VEZES`, `AINDA_NAO`. | Pendente | A tela explica a resposta sem linguagem de aprovação ou reprovação. |
| NEU-12 | Aplicar idade corrigida nos checkpoints de prematuros até 24 meses corrigidos. | Pendente | Criança prematura vê a faixa correta; a regra é testada com semanas e dias gestacionais. |
| NEU-13 | Mostrar a fonte e a explicação do marco sob demanda. | Pendente | Detalhe técnico não sobrecarrega a tela da família. |
| NEU-14 | Exibir disclaimer persistente no fluxo de desenvolvimento e de resultados. | Pendente | Declara que o app acompanha e não diagnostica; orienta conversa com pediatra. |
| NEU-15 | Preservar o fluxo guiado, um marco por vez, apenas na faixa aplicável à idade. | Parcial | Fluxo atual existe; será recalibrado para a nova base. |

### Fase 2 - Trajetória e orientação segura

| ID | Entrega | Status | Critério de aceite |
|---|---|---|---|
| NEU-20 | Persistir eventos de resposta por criança, marco, checkpoint e data. | Pendente | Uma alteração não apaga a resposta histórica. |
| NEU-21 | Registrar relato opcional de perda de habilidade. | Pendente | A família pode informar regressão com contexto e data, sem o app nomear diagnóstico. |
| NEU-22 | Implementar orientação de atenção gradual. | Pendente | Antes da janela: reforço e conversa planejada; após regra clínica validada: orientação de procurar pediatra. |
| NEU-23 | Agregar respostas persistentes e múltiplos domínios em uma única orientação. | Pendente | Não há avalanche de alertas nem repetição de mensagens. |
| NEU-24 | Criar tela de acompanhamento longitudinal. | Pendente | Família visualiza evolução por checkpoint, respostas e próximos passos sem parecer checklist bruto. |

### Fase 3 - Estímulos e conteúdo educativo

| ID | Entrega | Status | Critério de aceite |
|---|---|---|---|
| NEU-30 | Criar banco de estímulos mensais de 0 a 24 meses. | Pendente | Atividade possui faixa etária, domínio, objetivo, fonte, cuidados e texto familiar. |
| NEU-31 | Mostrar poucos estímulos contextuais por vez. | Pendente | Sugestões se relacionam à idade e aos domínios observados, sem prescrição ou sobrecarga. |
| NEU-32 | Incluir avisos de segurança revisados clinicamente. | Pendente | Conteúdos de bruços, sono, mobilidade e objetos pequenos têm orientação segura adequada à idade. |
| NEU-33 | Permitir marcar atividade como experimentada e registrar observação opcional. | Pendente | Histórico não é tratado como adesão obrigatória ou indicador diagnóstico. |

### Fase 4 - Triagem validada e relatório

| ID | Entrega | Status | Critério de aceite |
|---|---|---|---|
| NEU-40 | Confirmar licença comercial do M-CHAT-R/F. | Bloqueado | Termo de licença documentado antes de qualquer item do instrumento entrar no produto. |
| NEU-41 | Implementar M-CHAT-R/F como módulo separado aos 18 e 24 meses. | Bloqueado | Itens, ordem, instruções, follow-up, copyright e algoritmo seguem a licença e a fonte oficial. |
| NEU-42 | Avaliar licenças e formato de ASQ-3/SWYC antes de uso. | Pesquisa/validação | Nenhum instrumento protegido é reproduzido ou pontuado sem permissão. |
| NEU-43 | Gerar PDF para consulta pediátrica. | Pendente | Inclui contexto de prematuridade, respostas por etapa, trajetória, estímulos e pontos a conversar; não contém diagnóstico automatizado. |

## Ordem de execução

1. NEU-01 a NEU-04: catálogo, decisão clínica e migração segura.
2. NEU-10 a NEU-15: checklist novo, idade corrigida e UX.
3. NEU-20 a NEU-24: histórico e leitura de trajetória.
4. NEU-30 a NEU-33: estímulos mensais revisados.
5. NEU-43: relatório clínico consolidado.
6. NEU-40 a NEU-42: somente após licença e validação formal.

## Decisões que exigem validação da dra.

- Confirmar cada marco realocado para P50 como `observação precoce`, `atenção persistente` ou `prioridade`, especialmente sentar aos 6 meses, marcha aos 12 meses, primeira palavra aos 12 meses e vocabulário de 50 palavras aos 24 meses.
- Definir o limiar clínico que converte uma resposta isolada em orientação prioritária. Não usaremos automaticamente o checkpoint P50 como alerta vermelho.
- Validar o conteúdo final dos estímulos, inclusive a adaptação cultural e os avisos de segurança.
- Aprovar texto do disclaimer e do relatório para consulta.

## Referências primárias e de compliance

- OMS, janelas de aquisição dos seis marcos motores: https://www.who.int/tools/child-growth-standards/standards/motor-development-milestones
- CDC, limites de uso dos checklists de marcos: https://www.cdc.gov/act-early/milestones/key-points.html
- AAP, triagem geral aos 9/18/30 meses e triagem de TEA aos 18/24 meses: https://www.aap.org/en/patient-care/developmental-behavioral-pediatrics-a-resource-guide-for-general-pediatricians/autism-spectrum-disorder-resources-for-pediatricians/
- M-CHAT-R/F, permissões e licença: https://www.mchatscreen.com/mchat-rf/
