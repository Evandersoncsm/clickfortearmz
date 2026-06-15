package com.app.and.integracao;

/** Credenciais decifradas para uso exclusivo de clientes executados no servidor. */
public record CredenciaisOmie(Long contaId, String nome, String appKey, String appSecret) {
}
