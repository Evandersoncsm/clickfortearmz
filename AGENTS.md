# Guia para agentes de IA

Este arquivo e o ponto de entrada para qualquer agente que for criar funcionalidades ou atualizar o sistema.

## Leia nesta ordem

1. `README.md`
2. `docs/ARCHITECTURE.md`
3. `src/main/resources/application.properties`
4. Controller relacionado a mudanca
5. Porta de entrada em `application/port/in`
6. Servico em `application/service`
7. Dominio e adaptadores envolvidos

O contrato HTTP executavel esta em `/v3/api-docs/clickforte-web` e a interface visual em `/swagger-ui.html` quando a aplicacao estiver rodando.

## Regras arquiteturais

- `domain` nao deve importar Spring, JPA, Thymeleaf, Apache POI ou classes de infraestrutura.
- `application/port/in` define casos de uso oferecidos pelos controllers.
- `application/port/out` define dependencias externas exigidas pelos servicos.
- `application/service` orquestra portas e regras; nao deve conhecer HTML, `MultipartFile` ou entidades JPA.
- `web` converte requisicoes HTTP em chamadas de caso de uso e seleciona templates.
- `infrastructure` implementa banco e leitura de planilhas.
- Entidades JPA nao devem escapar para controllers ou para o dominio.
- A integracao Omie ainda e um modulo direto. Ao crescer, mova seus contratos para portas sem quebrar o comportamento atual.

## Invariantes atuais

- A contagem usa a ultima aba da planilha e faz upsert por data; sem data, usa o nome da aba.
- Cobertura e status de estoque sao calculados no dominio, nao lidos das formulas do Excel.
- A producao usa a primeira aba e fica somente em memoria; reiniciar o processo apaga a programacao.
- Cada conta Omie possui credenciais isoladas; o App Secret nunca deve ser exibido e e cifrado antes de ir ao banco.
- Toda consulta futura a Omie deve receber explicitamente o identificador da conta usada.
- `/omie` contem prototipos e contratos visuais baseados nos servicos oficiais. Nao trate dados de exemplo ou botoes desabilitados como uma integracao pronta.
- Ao implementar uma dessas telas, mantenha endpoint, chamada principal, filtros e colunas em `OmieTelaCatalogo` sincronizados com a documentacao oficial.
- Alterar `APP_CRYPTO_PASSWORD` ou `APP_CRYPTO_SALT` invalida segredos ja cifrados.
- `/integracoes/**` e `/h2-console/**` exigem login. `/omie/**`, as demais telas e a documentacao Swagger sao publicas.
- Os POSTs MVC usam CSRF e retornam redirecionamentos. Nao os trate como API JSON.

## Como implementar uma funcionalidade

1. Modele a regra em `domain` quando ela for regra de negocio.
2. Crie ou ajuste uma porta de entrada em `application/port/in`.
3. Implemente a orquestracao em `application/service`.
4. Crie portas de saida para banco, arquivo ou API externa.
5. Implemente adaptadores em `infrastructure`.
6. Exponha a funcionalidade em controller e template, ou em um novo `@RestController` sob `/api`.
7. Documente novas rotas com anotacoes OpenAPI.
8. Adicione testes proporcionais ao risco e execute `.\mvnw.cmd test`.
9. Execute `powershell -File scripts/check-staged-secrets.ps1` depois de preparar o commit.
10. Atualize `docs/ARCHITECTURE.md` quando mudar fluxos, persistencia, seguranca ou variaveis.

## Cuidados

- Nao versione `data/`, `target/`, planilhas reais, bancos ou segredos.
- Em um clone novo, ative a protecao local com `git config core.hooksPath .githooks`.
- Nunca contorne o hook com `--no-verify` em commits gerados por IA.
- Nao use os defaults locais de autenticacao e criptografia em producao.
- Preserve as alteracoes existentes do usuario e evite refatoracoes fora do pedido.
- Para uma API JSON nova, prefira DTOs proprios; nao serialize entidades JPA diretamente.
- Se tornar a producao persistente, substitua `InMemoryProducaoRepository` por um adaptador de `ProducaoRepositoryPort` e documente a migracao.
