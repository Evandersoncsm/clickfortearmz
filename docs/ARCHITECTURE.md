# Arquitetura do ClickForte Armazem

## Objetivo

O sistema importa duas planilhas `.xlsx`:

- Contagem de estoque: persiste dias de contagem e gera indicadores de reposicao.
- Programacao de producao: mostra itens, quantidades, embalagem, previsao e status.

Tambem guarda App Key e App Secret da Omie. O cliente que chama a API Omie ainda nao foi implementado.

## Stack

- Java 21
- Spring Boot 4.1
- Spring MVC e Thymeleaf
- Spring Security com formulario e sessao
- Spring Data JPA e H2
- Apache POI para arquivos Excel
- springdoc-openapi e Swagger UI
- Maven Wrapper, Docker e Render

## Estrutura

```text
src/main/java/com/app/and
|-- AndApplication.java
|-- config
|   |-- OpenApiConfig.java
|   `-- SecurityConfig.java
|-- domain/model                 # Regras e modelos sem framework
|-- application
|   |-- port/in                  # Casos de uso chamados pela web
|   |-- port/out                 # Contratos de banco e planilhas
|   `-- service                  # Implementacao dos casos de uso
|-- infrastructure
|   |-- persistence             # Adaptadores de persistencia
|   |   `-- jpa                 # Entidades e repositories Spring Data
|   `-- spreadsheet             # Leitores Apache POI
|-- integracao                  # Configuracao e criptografia da Omie
`-- web                         # Controllers MVC

src/main/resources
|-- application.properties
|-- static/css e static/js
`-- templates                   # Paginas Thymeleaf
```

## Dependencias entre camadas

```text
HTTP/Thymeleaf -> portas de entrada -> servicos -> dominio
                                         |
                                         v
                                  portas de saida
                                         |
                                         v
                            JPA/H2 ou Apache POI
```

O dominio fica no centro e nao conhece Spring nem infraestrutura. Controllers dependem das interfaces de caso de uso, e os servicos dependem das portas de saida.

## Fluxos principais

### Contagem de estoque

1. `POST /importar` recebe `planilha` como multipart.
2. `ImportarContagemService` chama `ContagemPlanilhaReaderPort`.
3. `ApachePoiContagemReader` abre a ultima aba do workbook.
4. Cada linha valida vira `ItemContagem`; cobertura e status sao calculados no dominio.
5. `JpaContagemRepository` faz upsert pela data ISO ou, sem data, pelo nome da aba.
6. `GET /relatorio?dia=...` monta `Relatorio` a partir do dia escolhido.

Layout esperado, com indices humanos do Excel:

| Coluna | Conteudo |
|---|---|
| B | SKU |
| C | Codigo do produto |
| D | Contagem anterior |
| E | Recebimento |
| F | Producao/saida |
| G | FTI |
| H | Contagem atual e data na primeira linha |
| J | Consumo de referencia |

Dados comecam na linha 3. Colunas de formulas, cobertura e status nao sao usadas como fonte da regra.

### Programacao de producao

1. `POST /producao/importar` recebe `planilha` como multipart.
2. `ImportarProducaoService` chama `ProducaoPlanilhaReaderPort`.
3. `ApachePoiProducaoReader` le a primeira aba e blocos por fabricante.
4. Cada bloco pode ter Linha 1 nas colunas B-F e Linha 2 nas colunas H-L.
5. `InMemoryProducaoRepository` guarda apenas a ultima importacao.
6. `GET /producao` exibe os totais e agrupamentos.

Esta informacao nao e persistente. Um restart, deploy ou nova instancia perde a programacao importada.

### Integracao Omie

1. `GET /integracoes` mostra App Key e apenas informa se existe segredo.
2. A tela permite cadastrar e selecionar varias contas Omie por nome e identificador.
3. `POST /integracoes` cria ou atualiza uma conta e mantem o segredo atual quando o campo vier vazio.
4. `POST /integracoes/excluir` remove somente a conta selecionada.
5. `CryptoService` cifra separadamente o App Secret de cada conta antes da persistencia.
6. `IntegracaoService.credenciais(contaId)` e o ponto de acesso futuro para o cliente Omie.

`GET /omie` apresenta o catalogo de telas sugeridas e `GET /omie/{slug}` abre um prototipo navegavel.
O catalogo em `OmieTelaCatalogo` centraliza endpoint, chamada principal, filtros, colunas e acoes esperadas.
Essas paginas sao especificacoes para desenvolvimento: os formularios e botoes permanecem desabilitados ate a
implementacao de um `OmieClient` executado somente no servidor.

As chamadas futuras para a API Omie devem usar `POST` com `Content-Type: application/json`, mesmo em
operacoes de consulta. O corpo segue o contrato `call`, `app_key`, `app_secret` e `param`. Cada familia
de servicos possui um endpoint proprio, definido na documentacao oficial da Omie. O App Secret deve ser
injetado apenas no cliente executado no servidor e nunca enviado para templates ou JavaScript.

## Rotas HTTP

| Metodo | Rota | Protecao | Resultado |
|---|---|---|---|
| GET | `/` | Publica | Pagina inicial |
| POST | `/importar` | Publica + CSRF | Importa contagem e redireciona |
| GET | `/relatorio` | Publica | Relatorio do ultimo dia ou do parametro `dia` |
| POST | `/producao/importar` | Publica + CSRF | Importa producao e redireciona |
| GET | `/producao` | Publica | Ultima programacao em memoria |
| GET | `/login` | Publica | Formulario de login |
| GET | `/integracoes` | Sessao | Configuracao Omie |
| POST | `/integracoes` | Sessao + CSRF | Cria ou atualiza uma conta Omie |
| POST | `/integracoes/excluir` | Sessao + CSRF | Exclui uma conta Omie |
| GET | `/omie` | Publica | Catalogo de telas sugeridas para a API Omie |
| GET | `/omie/{slug}` | Publica | Prototipo e contrato da tela Omie selecionada |
| GET | `/swagger-ui.html` | Publica | Swagger UI |
| GET | `/v3/api-docs/clickforte-web` | Publica | Contrato OpenAPI JSON |
| GET | `/h2-console` | Sessao | Console do banco, habilitado por configuracao |

As rotas atuais sao MVC e respondem HTML ou `302`. Uma futura API JSON deve usar `/api/...`, DTOs e `@RestController` para nao misturar os dois contratos.

## Persistencia

Tabelas atuais:

- `contagem`: cabecalho de um dia, chave unica, aba e data.
- `item_contagem`: itens ligados a uma contagem.
- `integracao`: uma linha por conta Omie, com nome, App Key e App Secret cifrado.

O schema e atualizado por `spring.jpa.hibernate.ddl-auto=update`. Em evolucoes maiores, adote Flyway antes de depender de migracoes de producao.

No Render free, o H2 dentro do container e efemero. Um redeploy pode apagar contagens e credenciais da Omie. Para persistencia real, use disco persistente ou PostgreSQL.

## Seguranca

- Login por usuario unico em memoria, configurado por variaveis de ambiente.
- Senha de login codificada com BCrypt durante o boot.
- App Secret cifrado no banco; ele nao aparece na tela nem no OpenAPI.
- CSRF permanece ativo, exceto no console H2.
- Nunca registre App Secret, senha do banco ou chaves de criptografia em logs.
- Em producao, prefira desabilitar o H2 Console.

## Como recriar o sistema

Para iniciar um projeto equivalente do zero:

1. Crie um Spring Boot 4.1 com Java 21, Web MVC, Thymeleaf, Security, Validation e Data JPA.
2. Adicione H2, Apache POI e `springdoc-openapi-starter-webmvc-ui` 3.x.
3. Reproduza primeiro os modelos de dominio e suas regras calculadas.
4. Defina portas de entrada e saida antes dos adaptadores.
5. Implemente leitores de planilha com os layouts descritos acima.
6. Implemente persistencia das contagens por upsert e decida se producao deve continuar em memoria.
7. Implemente controllers MVC, templates, login, criptografia e OpenAPI.
8. Configure variaveis de ambiente, Docker e a plataforma de deploy.
9. Cubra leitores, regras de dominio, repositories e seguranca com testes.

## Pontos de evolucao

- Implementar `OmieClient` usando `IntegracaoService`, timeouts e tratamento de erros.
- Persistir programacao de producao.
- Migrar H2 para PostgreSQL e adotar Flyway.
- Desabilitar H2 Console no perfil de producao.
- Criar endpoints JSON sob `/api` caso exista integracao com frontend ou terceiros.
- Aumentar testes de dominio, controllers, seguranca e persistencia.
