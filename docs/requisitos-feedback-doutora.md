# Requisitos e feedback da doutora

Documento de acompanhamento das mudanças solicitadas após a validação clínica da versão atual do Pueria.

Status usados:

- `Pendente`: ainda não implementado.
- `Parcial`: existe algo no app, mas precisa ajuste.
- `Implementado`: já existe e está adequado.
- `Pesquisa/validação`: precisa confirmação clínica, fonte oficial ou decisão de produto antes de codar.

## Princípios para implementação

- Não diagnosticar. Usar linguagem de acompanhamento, revisão de medida e conversa com pediatra.
- A família não escolhe curvas de crescimento manualmente; o app calcula com base nos dados informados.
- A interface para pais deve ser simples, acolhedora e prática. Termos técnicos ficam em detalhes ou modo profissional.
- Sempre que houver dado clínico ou referência antropométrica, usar fonte oficial ou artigo primário e registrar a fonte no código/dataset.
- Evitar telas que pareçam apenas formulário. Priorizar leitura visual, cartões de evolução, escolhas guiadas e explicações curtas.

## Prioridade sugerida

1. Ajustes rápidos nos módulos já existentes: desenvolvimento, alimentação, sono e telas.
2. Criar módulo de trânsito intestinal.
3. Evoluir crescimento para seleção automática de curva por idade gestacional, prematuridade e condições clínicas.
4. Melhorar cadastro inicial para coletar dados necessários ao crescimento avançado.
5. Adicionar modo/detalhe profissional para percentil, escore-Z, curva usada e trajetória.

## Feedback de experiência e conteúdo

### Desenvolvimento

| ID | Requisito | Status | Observações |
|---|---|---|---|
| DEV-01 | Trocar o botão `Pular` por `Seguinte` nas etapas de desenvolvimento. | Implementado | `Pular` sugere não responder. A ação atual avança para o próximo marco. |

### Alimentação

| ID | Requisito | Status | Observações |
|---|---|---|---|
| ALI-01 | Adicionar `BLW misto - toca alimentos enquanto é alimentado` em "Como acontece nas refeições". | Implementado | Campo incluído na interface, API, domínio e banco. |
| ALI-02 | Trocar `Tem rotina alimentar previsível` por linguagem que inclua `local e horário`. | Implementado | Texto usado: `Tem local e horário previsíveis para as refeições`. |
| ALI-03 | Remover `Constipação` e `Diarreia recorrente` de alimentação. | Parcial | Removido da interface. Dados antigos permanecem no domínio até criação do módulo `Trânsito intestinal`. |
| ALI-04 | Trocar `Preocupação com ganho de peso` por `Família está tranquila quanto ao ganho de peso atual`. | Implementado | Campo positivo incluído sem gerar alerta automático quando não marcado. |
| ALI-05 | Trocar `Usa colher` por `Aprendendo ou usando talheres`. | Implementado | Ajuste aplicado na interface mantendo compatibilidade com campo atual. |
| ALI-06 | Separar `Legumes e verduras` em dois grupos. | Implementado | Campos separados incluídos na interface, API, domínio e banco. |
| ALI-07 | Criar quadro de diversidade alimentar por grupos e alimentos. | Implementado | Bloco compacto com modal, busca, filtros por grupo e chips de alimentos oferecidos; seleção é salva no banco por registro alimentar. |
| ALI-08 | Listar exemplos de proteína animal: ovos, frango, carneiro, vaca, porco e peixe. | Implementado | Catálogo ampliado com ovos, carnes, aves, peixes e frutos do mar. |
| ALI-09 | Registrar a origem predominante dos alimentos: orgânica, convencional ou mista. | Implementado | Campo descritivo e opcional, com linguagem sem julgamento, salvo por registro. |
| ALI-10 | Ampliar o catálogo para os grupos alimentares definidos pela revisão clínica. | Implementado | Catálogo pesquisável com frutas, hortaliças, raízes, cereais, pseudocereais, leguminosas, proteínas, derivados, oleaginosas, sementes, gorduras e líquidos. |
| ALI-11 | Permitir rastreabilidade de alimentos mais alergênicos sem apresentá-los como restrição. | Implementado | Filtro específico e identificação contextual; o app explicita que o registro não representa diagnóstico ou proibição. |
| ALI-12 | Registrar detalhes opcionais por alimento. | Implementado | Data da primeira oferta, idade calculada, preparo, textura, quantidade, aceitação, repetição, sinais percebidos e observações são persistidos. |
| ALI-13 | Diferenciar ausência de sinais de informação não preenchida. | Implementado | Cada oferta registra: não informado, nenhum sinal percebido ou sinais percebidos; os sinais específicos aparecem apenas na última opção. |
| ALI-14 | Manter histórico datado de reexposições. | Implementado | A família pode adicionar e remover datas; o app valida a relação com a primeira oferta e a data do registro. |
| ALI-15 | Permitir identificar opcionalmente qual peixe foi oferecido. | Implementado | Campo aparece somente nos detalhes do item genérico `Peixe`. |
| ALI-16 | Registrar classificação de glúten de forma estruturada. | Implementado | Contém, não contém, pode conter traços ou não informado; o catálogo sugere a classificação inicial e permite ajuste. |

### Sono

| ID | Requisito | Status | Observações |
|---|---|---|---|
| SON-01 | Trocar linguagem de evento passado para rotina: `dormiu` -> `dorme`, `acordou` -> `acorda`. | Implementado | A tela registra uma rotina observada, mesmo com data. |
| SON-02 | Ajustar local do sono para diferenciar cama/berço e quarto. | Implementado | O registro separa onde dorme (berço/cama) de em qual quarto dorme. |
| SON-03 | Detalhar despertares noturnos. | Implementado | Permite registrar se se alimenta, se volta a dormir rápido ou se demora a voltar a dormir. |
| SON-04 | Adicionar sinais observados: `Ranger de dentes durante o sono` e `Acorda bem disposto/alegre`. | Implementado | Os dois sinais foram incluídos no registro de observações. |
| SON-05 | Mover `Sono agitado` de hábitos antes/durante o sono para sinais observados. | Implementado | Feedback direto da doutora. |
| SON-06 | Máscara automática para horários. | Implementado | Digitar `2000` ou `20h00` é normalizado para `20:00` ao sair do campo. |
| SON-07 | Deixar claro que `Tempo total de cochilos` é em minutos. | Implementado | Label usado: `Tempo total de cochilos (minutos)`. |
| SON-08 | Melhorar validação visual de horário antes de salvar. | Implementado | O campo normaliza o horário e mostra uma orientação junto ao campo quando o formato não é válido. |

### Telas

| ID | Requisito | Status | Observações |
|---|---|---|---|
| TEL-01 | Explicitar unidade nos campos: `Dia de semana (horas por dia)` e `Fim de semana (horas por dia)`. | Implementado | Front aceita horas por dia e converte para minutos na API. |
| TEL-02 | Separar tipo de tela: celular, tablet/iPad e TV. | Pendente | A doutora precisa saber onde o conteúdo acontece. |
| TEL-03 | Relacionar conteúdo ao tipo de tela. | Pendente | Conteúdos: jogos, vídeos/desenhos, videochamada com família, conteúdo interativo, música etc. |
| TEL-04 | Revisar `ligada ao fundo`. | Implementado | Texto usado: `TV ou tela ligada enquanto a criança faz outra atividade`. |
| TEL-05 | Adicionar uso para distrair/acalmar a criança. | Implementado | Texto usado: `Para acalmar ou distrair`. |
| TEL-06 | Adicionar se a criança tem liberdade de escolher. | Pendente | Informação relevante sobre condução familiar. |

### Novo módulo: trânsito intestinal

| ID | Requisito | Status | Observações |
|---|---|---|---|
| INT-01 | Criar aba/módulo `Trânsito intestinal` no acompanhamento. | Implementado | Módulo criado e integrado ao perfil/acompanhamento da criança. |
| INT-02 | Registrar fezes com escala de Bristol. | Implementado | Escala visual de 1 a 7 incluída com descrição e alerta de que é guia de consistência, não diagnóstico. |
| INT-03 | Registrar elementos anormais nas fezes. | Implementado | Muco, restos alimentares e raias de sangue incluídos. |
| INT-04 | Registrar facilidade de limpeza. | Implementado | Opções: fácil de limpar, difícil de limpar e não informado. |
| INT-05 | Migrar constipação e diarreia recorrente para este módulo. | Implementado | Novos registros ficam no módulo intestinal. Campos antigos da alimentação foram preservados apenas por compatibilidade. |
| INT-06 | Registrar assaduras. | Implementado | Inclui assaduras frequentes, com vermelhidão e com pontos vermelhos. |

## Crescimento 0-2 anos

### Estado atual observado no projeto

- Existe dataset OMS LMS para peso/idade, comprimento/idade e perímetro cefálico/idade em `src/main/resources/oms/child-growth-standards`.
- O backend já calcula percentil e z-score com LMS da OMS.
- O app já possui idade gestacional em semanas e dias adicionais no cadastro da criança.
- O app já possui idade corrigida para prematuridade no módulo de crescimento.
- Ainda não há INTERGROWTH-21st, curvas T21/Down, curvas Turner, peso por comprimento, IMC por idade, seleção de curva por condição clínica, nem modo profissional completo.

### Curvas e seleção automática

| ID | Requisito | Status | Observações |
|---|---|---|---|
| CRE-01 | Usar OMS 2006 como curva principal para crianças nascidas a termo. | Parcial | Já há parte da OMS, mas faltam peso/comprimento e IMC/idade. |
| CRE-02 | Para prematuros com IG atual calculada menor que 64 semanas, usar INTERGROWTH-21st Preterm. | Pendente | Exige importar dataset INTERGROWTH e calcular IG atual. |
| CRE-03 | Para prematuros após 64 semanas de IG atual calculada, usar OMS 2006 com idade corrigida. | Parcial | Idade corrigida já existe, mas regra de transição por 64 semanas ainda precisa ser explícita. |
| CRE-04 | Usar idade corrigida até 24 meses cronológicos. | Parcial | Validar regra atual no backend e exibir texto simples. |
| CRE-05 | Permitir extensão da idade corrigida até 36 meses para prematuros extremos apenas em modo profissional. | Pendente | Futuro, depende de perfil profissional/configuração. |
| CRE-06 | Se T21/Down confirmado, ativar curva CDC/Zemel 2015 após fase de prematuridade. | Pendente | Precisa campo clínico e dataset. |
| CRE-07 | Se Turner confirmado em menina, ativar curva específica para crescimento linear e velocidade de crescimento. | Pesquisa/validação | Precisa definir fonte/dataset mais adequado antes de implementar. |
| CRE-08 | Família não escolhe curva manualmente. | Pendente | O app deve explicar a curva usada, não pedir escolha. |

### Dados de cadastro necessários

| ID | Requisito | Status | Observações |
|---|---|---|---|
| CAD-01 | Nome da criança. | Implementado | Já existe. |
| CAD-02 | Data de nascimento. | Implementado | Já existe. |
| CAD-03 | Sexo biológico. | Implementado | Já existe. |
| CAD-04 | Idade gestacional ao nascimento em semanas e dias. | Implementado | Já existe. |
| CAD-05 | Peso ao nascimento. | Implementado | Já existe. |
| CAD-06 | Comprimento ao nascimento. | Implementado | Já existe. |
| CAD-07 | Perímetro cefálico ao nascimento. | Implementado | Já existe. |
| CAD-08 | Tipo de gestação: única ou múltipla. | Pendente | Não identificado no cadastro atual. |
| CAD-09 | Internação em UTI neonatal. | Pendente | Existe bloco neonatal, mas validar se há campo específico de UTI neonatal. |
| CAD-10 | Diagnóstico conhecido de Down/T21. | Pendente | Opções: não; sim; em investigação; prefiro informar depois. |
| CAD-11 | Diagnóstico conhecido de Turner. | Pendente | Ocultar para sexo masculino no fluxo familiar ou deixar em modo profissional. |
| CAD-12 | Outra condição genética ou neurológica relevante. | Pendente | Campo sensível, usar linguagem não alarmista. |

### Medidas de acompanhamento

| ID | Requisito | Status | Observações |
|---|---|---|---|
| MED-01 | Data da medida. | Implementado | Já existe. |
| MED-02 | Peso em gramas ou kg. | Parcial | Entrada já aceita kg no front, banco usa gramas. Validar tolerância a vírgula. |
| MED-03 | Comprimento em cm. | Implementado | Já existe. |
| MED-04 | Perímetro cefálico em cm. | Implementado | Já existe. |
| MED-05 | Local da medida: casa, consultório, posto de saúde, hospital. | Pendente | Exige novo campo. |
| MED-06 | Quem mediu: mãe/pai, pediatra, enfermagem, outro. | Pendente | Exige novo campo. |
| MED-07 | Observação opcional. | Pendente | Exige novo campo. |
| MED-08 | Avisar que medida em casa pode ter maior variação. | Pendente | Mensagem de apoio, não bloqueio. |

### Saídas e análises

| ID | Requisito | Status | Observações |
|---|---|---|---|
| OUT-01 | Armazenar idade cronológica em dias por medida. | Pendente | Hoje parece calculado em runtime; decidir se persiste. |
| OUT-02 | Armazenar idade corrigida em dias quando prematuro. | Pendente | Hoje parece calculado em runtime; decidir se persiste. |
| OUT-03 | Armazenar IG atual calculada em dias quando prematuro. | Pendente | Necessário para INTERGROWTH. |
| OUT-04 | Armazenar curva usada. | Pendente | Importante para auditoria clínica. |
| OUT-05 | Armazenar percentil e escore-Z. | Pesquisa/validação | Pode calcular sob demanda, mas persistir facilita relatório e auditoria. |
| OUT-06 | Mostrar classificação resumida para pais. | Parcial | Já existe linguagem simples, mas precisa alinhar com múltiplas curvas. |
| OUT-07 | Mostrar detalhes profissionais: curva, percentil, z-score, trajetória, mudança de canal. | Parcial | Modal de detalhes existe parcialmente. |
| OUT-08 | Alertar sem diagnosticar. | Parcial | Manter frase segura: `Este resultado não é um diagnóstico. Ele indica que a medida merece ser revisada ou discutida com o pediatra.` |

### Validações clínicas e de qualidade de dado

| ID | Requisito | Status | Observações |
|---|---|---|---|
| VAL-01 | Medida em data anterior ao nascimento. | Pendente | Deve bloquear. |
| VAL-02 | IG ao nascimento menor que 22 ou maior que 42 semanas. | Implementado | Já há validação no cadastro. |
| VAL-03 | Peso incompatível com idade. | Pesquisa/validação | Definir limites com fonte/dataset antes de bloquear. |
| VAL-04 | Comprimento incompatível com idade. | Pesquisa/validação | Definir limites com fonte/dataset antes de bloquear. |
| VAL-05 | Perímetro cefálico incompatível com idade. | Pesquisa/validação | Definir limites com fonte/dataset antes de bloquear. |
| VAL-06 | Provável troca de unidade, ex.: 6500 kg em vez de 6,5 kg. | Pendente | Deve alertar de forma útil. |
| VAL-07 | Comprimento diminuir entre medidas. | Parcial | O app já mostra tendência, mas precisa transformar em revisão clara. |
| VAL-08 | Perímetro cefálico diminuir entre medidas. | Parcial | Idem. |
| VAL-09 | Salto grande de percentil em curto intervalo. | Parcial | Existe análise visual, mas precisa regra clínica mais robusta. |

## Fontes consultadas para balizar requisitos

- OMS, `WHO Child Growth Standards`: https://www.who.int/tools/child-growth-standards/standards
- CDC, uso de curvas OMS para nascimento a 2 anos: https://www.cdc.gov/growth-chart-training/hcp/using-growth-charts/who-using.html
- INTERGROWTH-21st, crescimento pós-natal de prematuros até 64 semanas: https://intergrowth21.com/tools-resources/postnatal-growth-preterm-infants
- Villar et al., `Postnatal growth standards for preterm infants`, Lancet Global Health, 2015: https://pubmed.ncbi.nlm.nih.gov/26475015/
- CDC, curvas para crianças com síndrome de Down e referência Zemel 2015: https://www.cdc.gov/birth-defects/hcp/down-syndrome-growth-charts/index.html
- Zemel et al., `Growth Charts for Children With Down Syndrome in the United States`: https://pmc.ncbi.nlm.nih.gov/articles/PMC5451269/
- Revisão sobre curvas em síndrome de Turner: https://pmc.ncbi.nlm.nih.gov/articles/PMC4052048/
- WHO, diretrizes para atividade física, comportamento sedentário e sono em menores de 5 anos: https://www.who.int/publications/i/item/9789241550536
- AAP, `Media and Young Minds`: https://publications.aap.org/pediatrics/article/138/5/e20162591/60503/Media-and-Young-Minds
- Bristol Stool Form Scale em contexto pediátrico, Stanford Medicine: https://med.stanford.edu/pediatricsurgery/Conditions/BowelManagement/bristol-stool-form-scale.html
- Ministério da Saúde, `Guia Alimentar para Crianças Brasileiras Menores de 2 Anos`: https://www.gov.br/saude/pt-br/composicao/saps/promocao-da-saude/guias-alimentares/publicacoes/guia_da_crianca_2019.pdf
- Ministério da Saúde, fundamentos e evidências do guia alimentar: https://bvsms.saude.gov.br/bvs/publicacoes/diretrizes_recomendacoes_guia_alimentar_criancas_revisada.pdf
- OMS, `Guideline for complementary feeding of infants and young children 6-23 months of age`: https://www.who.int/publications/i/item/9789240081864
- Sociedade Brasileira de Pediatria, `Alimentação Complementar para o Lactente Saudável`: https://www.sbp.com.br/departamentos/aleitamento-materno/documentos-cientificos/

## Próximo recorte de implementação recomendado

### Recorte 1: ajustes rápidos de UX/conteúdo

- DEV-01.
- ALI-01 a ALI-06.
- SON-01, SON-05, SON-06, SON-07 e SON-08.
- TEL-01 e TEL-04.

Motivo: são mudanças de alto impacto percebido, baixo risco clínico e já apontadas diretamente pela doutora.

### Recorte 2: módulo de trânsito intestinal

- INT-01 a INT-06.

Motivo: retira constipação/diarreia da alimentação e cria um espaço próprio para um dado muito relevante de rotina infantil.

### Recorte 3: crescimento avançado

- MED-05 a MED-08.
- CRE-02 e CRE-03.
- OUT-03 e OUT-04.

Motivo: antes de T21/Turner, precisamos deixar prematuridade e fonte da medida clinicamente sólidas.
