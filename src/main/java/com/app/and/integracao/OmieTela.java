package com.app.and.integracao;

import java.util.List;

/** Especificacao visual de uma tela que sera integrada a um servico oficial da Omie. */
public record OmieTela(
        String slug,
        String grupo,
        String titulo,
        String descricao,
        String endpoint,
        String chamadaPrincipal,
        String documentacaoUrl,
        String prioridade,
        List<String> filtros,
        List<String> colunas,
        List<String> acoes) {

    public String exemploPost() {
        return """
                POST %s
                Content-Type: application/json

                {
                  "call": "%s",
                  "app_key": "OBTIDA_NO_SERVIDOR",
                  "app_secret": "OBTIDO_NO_SERVIDOR",
                  "param": [
                    {
                      "pagina": 1,
                      "registros_por_pagina": 50
                    }
                  ]
                }
                """.formatted(endpoint, chamadaPrincipal);
    }
}
