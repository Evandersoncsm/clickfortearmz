# ClickForte Armazem

Aplicacao web para importar planilhas Excel de contagem de estoque e programacao de producao, gerar relatorios e armazenar as credenciais da integracao Omie.

## Executar localmente

Requisitos: Java 21. O Maven Wrapper ja esta no repositorio.

```powershell
.\mvnw.cmd spring-boot:run
```

Acessos locais:

- Aplicacao: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs/clickforte-web
- Console H2: http://localhost:8080/h2-console

Credenciais locais padrao da area protegida: `admin@local` / `dev-change-me`. Nao use esses valores em producao.

## Documentacao

- [Arquitetura e guia de evolucao](docs/ARCHITECTURE.md)
- [Instrucoes para agentes de IA](AGENTS.md)
- O Swagger descreve o contrato HTTP das telas e formularios MVC.

## Testes e build

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

## Configuracao

As configuracoes ficam em `src/main/resources/application.properties`. Em producao, defina no ambiente:

| Variavel | Uso |
|---|---|
| `PORT` | Porta HTTP; local usa `8080` |
| `SPRING_DATASOURCE_URL` | URL JDBC do H2 ou banco configurado |
| `DB_PASSWORD` | Senha do banco |
| `APP_AUTH_USERNAME` | Usuario da area protegida |
| `APP_AUTH_PASSWORD` | Senha da area protegida |
| `APP_CRYPTO_PASSWORD` | Senha usada para cifrar o App Secret da Omie |
| `APP_CRYPTO_SALT` | Salt da criptografia; mantenha fixo apos salvar segredos |

Nunca adicione valores reais dessas variaveis ao Git.

O Render ativa o perfil `prod`. Nesse perfil, a aplicacao nao inicia se uma
variavel sensivel estiver ausente e o console H2 fica desabilitado.

O arquivo `.env.example` contem apenas nomes e valores ficticios. Arquivos
`.env`, bancos H2 e certificados privados sao ignorados pelo Git.

Ative o bloqueio local de segredos uma vez por clone:

```powershell
git config core.hooksPath .githooks
```

O hook verifica o conteudo preparado antes do commit. O workflow
`.github/workflows/security.yml` executa Gitleaks novamente em pushes e pull requests.
