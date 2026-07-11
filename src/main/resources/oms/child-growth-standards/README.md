# WHO Child Growth Standards

Dados extraidos das tabelas expandidas oficiais da OMS (WHO Child Growth Standards), com os parametros LMS por dia para 0 a 5 anos.

Arquivos usados pelo Pueria:

- peso_idade_meninos_0_5.csv: PESO_IDADE, MASCULINO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/weight-for-age/expanded-tables/wfa-boys-zscore-expanded-tables.xlsx?sfvrsn=65cce121_10
- peso_idade_meninas_0_5.csv: PESO_IDADE, FEMININO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/weight-for-age/expanded-tables/wfa-girls-zscore-expanded-tables.xlsx?sfvrsn=f01bc813_10
- comprimento_idade_meninos_0_5.csv: COMPRIMENTO_IDADE, MASCULINO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/length-height-for-age/expandable-tables/lhfa-boys-zscore-expanded-tables.xlsx?sfvrsn=7b4a3428_12
- comprimento_idade_meninas_0_5.csv: COMPRIMENTO_IDADE, FEMININO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/length-height-for-age/expandable-tables/lhfa-girls-zscore-expanded-tables.xlsx?sfvrsn=27f1e2cb_10
- perimetro_cefalico_idade_meninos_0_5.csv: PERIMETRO_CEFALICO_IDADE, MASCULINO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/head-circumference-for-age/expanded-tables/hcfa-boys-zscore-expanded-tables.xlsx?sfvrsn=2ab1bec8_8
- perimetro_cefalico_idade_meninas_0_5.csv: PERIMETRO_CEFALICO_IDADE, FEMININO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/head-circumference-for-age/expanded-tables/hcfa-girls-zscore-expanded-tables.xlsx?sfvrsn=3a34b8b0_8
- peso_comprimento_meninos_0_2.csv: PESO_COMPRIMENTO, MASCULINO, 131 linhas de 45 a 110 cm, em intervalos de 0,5 cm. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/weight-for-length-height/wfl_boys_0-to-2-years_zscores.xlsx?sfvrsn=e27a9da3_7
- peso_comprimento_meninas_0_2.csv: PESO_COMPRIMENTO, FEMININO, 131 linhas de 45 a 110 cm, em intervalos de 0,5 cm. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/weight-for-length-height/wfl_girls_0-to-2-years_zscores.xlsx?sfvrsn=288bc4e4_7
- imc_idade_meninos_0_5.csv: IMC_IDADE, MASCULINO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/body-mass-index-for-age/expanded-tables/bfa-boys-zscore-expanded-tables.xlsx?sfvrsn=f8e1fbe2_10
- imc_idade_meninas_0_5.csv: IMC_IDADE, FEMININO, 1857 linhas. Fonte: https://cdn.who.int/media/docs/default-source/child-growth/child-growth-standards/indicators/body-mass-index-for-age/expanded-tables/bfa-girls-zscore-expanded-tables.xlsx?sfvrsn=ae4cb8d1_12

O calculo de z-score usa a formula LMS:

- L != 0: z = ((valor / M)^L - 1) / (L * S)
- L = 0: z = ln(valor / M) / S

Nao editar manualmente estes CSVs. Regenerar a partir das URLs em fontes.json.
