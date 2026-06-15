package com.app.and.domain.model;

import java.time.LocalDate;

/**
 * Resumo de um dia de contagem persistido, para listagem (modal de dias).
 *
 * @param chave       identificador único do dia (data ISO ou nome da aba)
 * @param aba         nome da aba de origem
 * @param data        data da contagem (pode ser nula)
 * @param totalItens  quantidade de SKUs daquele dia
 */
public record DiaContagem(String chave, String aba, LocalDate data, int totalItens) {
}
