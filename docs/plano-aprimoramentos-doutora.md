# Plano de aprimoramentos da doutora

Documento de execução baseado em `APRIMORAMENTOS.docx`, recebido em 18/07/2026.

## Princípios de produto e segurança

- Organizar observações da família e tendências ao longo do tempo, sem diagnosticar ou prescrever.
- Usar linguagem acolhedora, concreta e compreensível para mães, pais e cuidadores.
- Destacar situações para conversa com o pediatra com prioridade proporcional, sem alarmismo.
- Manter dados clínicos rastreáveis, versionados e úteis no relatório para consulta.
- Validar fluxos novos em celular e desktop, com testes de domínio, aplicação e interface conforme o risco.

## Como acompanhar

- `[ ]` pendente
- `[~]` em andamento ou parcialmente entregue
- `[x]` concluído e validado

## Fase 0 - Auditoria e ajustes de base

- [x] APR-00 Auditar cada solicitação contra a implementação atual e registrar o estado real neste documento.
- [x] APR-01 Renomear a apresentação de `Desenvolvimento` para `Neurodesenvolvimento` nos pontos voltados à família, preservando rotas e contratos internos quando possível.
- [x] APR-02 Revisar os textos de telas: autoplay, notificações, brincar ao ar livre e leitura/brincadeira sem tela.
- [x] APR-03 Ajustar os sinais observados do sono para `Difícil de ser acordado`, `Mal-humorado` e `Irritado`; investigar e cobrir com teste o erro de salvamento relatado.
- [x] APR-04 Trocar a ilustração atual da escala de Bristol por uma representação visual mais clara e consistente com o design do Puéria.
- [x] APR-05 Revisar alimentação: plural em `Como acontecem as refeições`, opção de alimentação exclusivamente por cuidador e orientação de rastreabilidade de alimentos alergênicos incluindo leite de vaca.

**Aceite:** textos consistentes na interface e no PDF, sem regressão de salvamento ou responsividade.

### Auditoria concluída em 18/07/2026

- A interface e a navegação interna preservam as rotas técnicas de desenvolvimento; a apresentação para a família passou a usar `Neurodesenvolvimento`.
- O catálogo já continha leite de vaca como alimento alergênico rastreável. A orientação agora o nomeia explicitamente e reforça que registro não equivale a restrição ou diagnóstico.
- A alimentação já usava o plural em `Como acontecem as refeições`; foi adicionada a opção persistida de alimentação exclusivamente conduzida por cuidador.
- O único sinal combinado de irritabilidade/cansaço no sono foi substituído na experiência atual por três sinais independentes. O campo anterior permanece apenas para leitura de registros históricos.
- A visualização da escala de Bristol foi redesenhada com formas mais distintas por tipo. O dado selecionado, os textos de consistência e os pontos para consulta não foram alterados.

## Fase 1 - Segurança e leitura clínica do neurodesenvolvimento

- [x] APR-10 Fazer `Preocupação da família` aparecer como ponto para conversar com o pediatra nos resultados e no relatório.
- [x] APR-11 Criar destaque prioritário, acolhedor e acionável para `Perda de habilidade`, inclusive no Acompanhamento e no relatório para consulta.
- [x] APR-12 Exibir aquisições novas e observações repetidas a partir do histórico de respostas, sem equivaler mudança de registro a diagnóstico.
- [x] APR-13 Revisar a nomenclatura de `oportunidade de estímulo` e seu lugar no fluxo, mantendo atividades leves e individualizadas por marco.

**Aceite:** preocupações e regressões ficam visíveis para a família e úteis na consulta; o produto não emite diagnóstico nem recomenda conduta clínica.

## Fase 2 - Eliminações fisiológicas e rotina emocional

- [x] APR-20 Renomear o módulo `Trânsito intestinal` para `Eliminações fisiológicas`.
- [x] APR-21 Manter o registro de fezes e adicionar diurese: intervalo aproximado, cor, aspecto e cheiro, com opção de padrão habitual/sem alterações relevantes.
- [x] APR-22 Criar o módulo `Humor e comportamento`, com observações mensais de humor, choro, consolabilidade, interesse por brincar e interação.
- [~] APR-23 Criar `Observações livres e eventos marcantes`, com texto livre, data e possibilidade de aparecer na linha do tempo e no relatório. A tela e o histórico próprio foram criados; a linha do tempo unificada e o relatório são entregas da Fase 5.

**Aceite:** a família consegue registrar o período sem excesso de campos; eventos podem contextualizar outros registros sem sugerir causalidade.

## Fase 3 - Crescimento e visualização evolutiva

- [ ] APR-30 Diferenciar visualmente posições abaixo e acima da faixa esperada nas curvas de crescimento.
- [ ] APR-31 Garantir que o gráfico mostre uma trajetória evolutiva conectada entre as medidas, com leitura compreensível para a família.
- [ ] APR-32 Manter detalhes técnicos sob demanda, incluindo referência, percentil, escore-z e critério de idade.

**Aceite:** uma pessoa cuidadora entende a trajetória; o profissional consegue abrir os dados técnicos necessários.

## Fase 4 - Resumos mensais por domínio

- [ ] APR-40 Criar o registro mensal de sono: padrão geral, início do sono, despertares, sonecas, forma/local de adormecer, piora e contexto associado.
- [ ] APR-41 Criar o registro mensal de alimentação: base alimentar, aceitação, hidratação, diversidade, progressão de textura, alimentos novos e possíveis reações observadas.
- [ ] APR-42 Criar o resumo mensal de eliminações: frequência, padrão das fezes, sinais associados, intervalos sem evacuar e padrão de diurese.
- [ ] APR-43 Criar o resumo mensal de sintomas e saúde: intercorrências, sintomas, intensidade, atendimento e uso eventual de medicamentos.
- [ ] APR-44 Criar o resumo mensal de uso regular de medicamentos, vitaminas e suplementos, separado de uso agudo por intercorrência.
- [ ] APR-45 Criar o resumo mensal de estilo de vida e ambiente: telas, ar livre, sol, brincadeira corporal e tempo em contenções.
- [ ] APR-46 Criar o resumo mensal de rotina e contexto: cuidador, creche, mudanças, contato com outras crianças e eventos familiares.

**Aceite:** cada domínio permite registrar o padrão predominante sem exigir diário detalhado; os dados ficam disponíveis para evolução e relatório.

## Fase 5 - Linha do tempo e interpretação evolutiva

- [ ] APR-50 Criar uma linha do tempo por dia, semana, mês e faixa etária, reunindo crescimento, desenvolvimento, rotina e eventos relevantes.
- [ ] APR-51 Definir o registro `Sem alterações relevantes no período` em cada domínio para diferenciar ausência de registro de período sem observações.
- [ ] APR-52 Criar regras versionadas de coincidência temporal entre eventos, sem atribuir causa ou diagnóstico.
- [ ] APR-53 Exibir ao profissional resumos diário, semanal e mensal, tendências recorrentes e pontos para discutir na consulta.
- [ ] APR-54 Expandir o relatório para consulta com os padrões temporais e os registros que os compõem.

**Aceite:** o app descreve fatos e coincidências temporais, com fonte e período claros; não usa termos diagnósticos, causalidade ou recomendações terapêuticas.

## Fase 6 - Adoção, marca e validação

- [ ] APR-60 Criar tutorial curto e opcional para os primeiros fluxos do Acompanhamento.
- [ ] APR-61 Fazer revisão visual externa em celular e desktop, priorizando cores, contraste, leitura e inclusão.
- [ ] APR-62 Decidir formalmente a grafia da marca `Puéria` e aplicar a mudança de forma coordenada em interface, documentos, domínio e comunicação.

**Aceite:** tutorial pode ser dispensado, o design é validado em resoluções reais e a marca tem uma única grafia oficial.

## Ordem recomendada

1. Fase 0: corrigir linguagem, segurança e problemas já percebidos.
2. Fase 1: tornar preocupações e perda de habilidade clinicamente úteis.
3. Fase 2: estruturar novas fontes de contexto familiar.
4. Fase 3: fechar leitura visual de crescimento.
5. Fase 4: criar o modelo mensal consistente por domínio.
6. Fase 5: conectar os dados na linha do tempo e no relatório.
7. Fase 6: aprimorar adoção, acessibilidade e marca.

## Decisões pendentes da doutora

- Quais fatos devem apenas aparecer no relatório e quais merecem destaque na tela do Acompanhamento?
- Qual linguagem e prioridade usar para cada combinação temporal, sem sugerir causa ou diagnóstico?
- Quais itens mensais são obrigatórios e quais podem ser opcionais por idade?
- Qual deve ser a apresentação visual substituta da escala de Bristol?
- A marca final será `Puéria` e haverá ajuste do domínio/identidade visual?
