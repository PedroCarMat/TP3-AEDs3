# TP03 – Lista Invertida

**Alunos participantes:** Isabel Cristina, Laura Bargas, Pedro Mattar e Yuri Penido

🎥 [Vídeo de demonstração](https://youtu.be/TT9y1vTKzuc)


## Descrição

O sistema de busca por palavras-chave (TP03) é uma evolução do sistema acadêmico para gestão de cursos livres da PUC Minas. Esta versão implementa um **Índice Invertido** associado ao modelo estatístico **TF×IDF** (*Term Frequency — Inverse Document Frequency*), permitindo buscas textuais inteligentes pelos nomes dos cursos disponíveis, retornando resultados ordenados por relevância.

O sistema utiliza persistência em arquivos binários de acesso aleatório (`RandomAccessFile`), estendendo as estruturas de dados já existentes (Tabelas Hash Extensíveis e Árvores B+) com a inclusão de **Listas Invertidas** para indexação e busca de termos.


## Estrutura de Classes

### Novas Classes (TP03)

- **`ElementoLista`** — Entidade que encapsula os dados fundamentais de indexação de cada termo, armazenando o `id` do curso e a `frequencia` de ocorrência do termo naquele curso (TF).

<img width="778" height="595" alt="Screenshot 2026-05-31 142314" src="https://github.com/user-attachments/assets/6ad53118-eb34-43ae-9e66-32f08d4ce96f" />


- **`ListaInvertida`** — Estrutura em disco responsável pelo CRUD de termos, gerenciando o arquivo de dicionário (`arqDicionario`) e o arquivo de blocos encadeados (`arqBlocos`), manipulando arrays e instâncias de `ElementoLista`.

<img width="624" height="689" alt="Screenshot 2026-05-31 142344" src="https://github.com/user-attachments/assets/c0eb8a69-ede8-4785-8160-b5992c81323c" />


### Modificações em Classes Existentes

- **`ArquivoCurso`** — Modificado para sincronizar o índice textual de forma imediata e transparente:
  - **Inclusão:** aciona `incrementaEntidades()` e persiste os termos via `ListaInvertida.create()`.
  - **Exclusão:** executa `decrementaEntidades()` e limpa referências via `ListaInvertida.delete()`.
  - **Atualização:** sincroniza termos alterados via `ListaInvertida.update()`.
  - Implementa também o filtro de **stopwords**.

  
<img width="705" height="480" alt="Screenshot 2026-05-31 142359" src="https://github.com/user-attachments/assets/8ac33ca3-a1bc-4c0e-9fe3-686d5c545141" />
<img width="706" height="382" alt="Screenshot 2026-05-31 142413" src="https://github.com/user-attachments/assets/cd2d82d0-2e91-4da6-b196-0922a43ab362" />


- **`ControleInscricao`** — Gerencia a lógica de busca por palavras-chave: processa a string do usuário, recupera arrays de `ElementoLista` via `ListaInvertida.read()`, calcula o IDF em tempo de execução, consolida pontuações por ID e gera o ranking ordenado de forma decrescente.

<img width="706" height="222" alt="Screenshot 2026-05-31 142431" src="https://github.com/user-attachments/assets/9db4e7f5-31eb-4229-a7f3-346728c77c98" />


- **`VisaoInscricao`** — Interface de terminal atualizada para capturar a consulta por termos e exibir a listagem final de cursos na ordem precisa de relevância.

<img width="703" height="402" alt="Screenshot 2026-05-31 142446" src="https://github.com/user-attachments/assets/f6a16bea-2d7a-4120-9538-e24eea15f1dc" />


## Operações Implementadas

### Processamento e Normalização de Termos
Utilização de `split` associado ao método estático `ParNomeId.transforma(String str)` para tokenizar o nome de cada curso em letras minúsculas e sem acentuação, com filtro de stopwords.

<img width="705" height="420" alt="Screenshot 2026-05-31 142502" src="https://github.com/user-attachments/assets/e2f4228e-01f5-4f14-b186-8308d6a3073a" />


### Cálculo Dinâmico de IDF
O peso estatístico de cada termo é calculado em tempo de execução:
$$\text{IDF} = \log_{10}\left(\frac{\text{Total de Cursos}}{\text{Cursos com o Termo}}\right) + 1$$
Utiliza `numeroEntidades()` da classe `ListaInvertida`, garantindo calibração precisa após modificações no arquivo.

<img width="709" height="124" alt="Screenshot 2026-05-31 142517" src="https://github.com/user-attachments/assets/cab03f96-bc5f-4fc9-8f59-750e226f68e7" />


### Ranking Acumulativo de Múltiplos Termos
Em buscas com múltiplas palavras-chave, o sistema varre as listas de cada termo e acumula os scores parciais de TF×IDF para registros com IDs coincidentes, posicionando organicamente no topo os cursos com maior correspondência textual.

<img width="712" height="290" alt="Screenshot 2026-05-31 142531" src="https://github.com/user-attachments/assets/22d8c1f1-e0ad-4ab4-b34a-09e91f88b1f8" />

## Checklist

- O índice invertido com os termos dos nomes dos cursos foi criado usando a classe `ListaInvertida`
- `SIM`
- É possível buscar cursos por palavras no menu de inscrição
- `SIM`
- O trabalho compila corretamente
- `SIM`
- O trabalho está completo e funcionando sem erros de execução
- `SIM`
- O trabalho é original e não cópia de outro grupo
- `SIM`
