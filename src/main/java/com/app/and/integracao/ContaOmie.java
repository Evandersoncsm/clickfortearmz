package com.app.and.integracao;

/** Dados seguros para listar e editar uma conta, sem expor o App Secret. */
public record ContaOmie(Long id, String nome, String appKey, boolean temAppSecret) {

    public static ContaOmie nova() {
        return new ContaOmie(null, "", "", false);
    }
}
