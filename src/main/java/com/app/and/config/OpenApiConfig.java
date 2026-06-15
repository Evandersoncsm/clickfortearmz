package com.app.and.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clickForteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ClickForte Armazem - Contrato HTTP")
                        .version("1.0")
                        .description("Documentacao das paginas MVC, importacoes de planilhas e configuracao "
                                + "da integracao Omie. As respostas sao HTML ou redirecionamentos; "
                                + "consulte docs/ARCHITECTURE.md para a estrutura interna do sistema.")
                        .contact(new Contact().name("ClickForte")))
                .externalDocs(new ExternalDocumentation()
                        .description("Arquitetura e guia de evolucao do projeto")
                        .url("https://github.com/Evandersoncsm/clickfortearmz/blob/main/docs/ARCHITECTURE.md"))
                .components(new Components()
                        .addSecuritySchemes("sessionCookie", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Sessao criada pelo formulario /login. Necessaria apenas em /integracoes.")));
    }

    @Bean
    public GroupedOpenApi webApplicationApi() {
        return GroupedOpenApi.builder()
                .group("clickforte-web")
                .displayName("Aplicacao web ClickForte")
                .pathsToMatch("/", "/login", "/importar", "/relatorio", "/producao/**", "/integracoes/**")
                .build();
    }
}
