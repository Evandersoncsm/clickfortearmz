package com.app.and.application.port.in;

import com.app.and.domain.model.Relatorio;

/**
 * Porta de entrada: gera o relatório da última contagem ou de um dia específico.
 */
public interface GerarRelatorioUseCase {

    /** Relatório do dia mais recente importado. */
    Relatorio gerar();

    /** Relatório de um dia específico (pela chave). */
    Relatorio gerar(String chave);
}
