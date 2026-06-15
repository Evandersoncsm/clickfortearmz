package com.app.and.infrastructure.spreadsheet;

/**
 * Lançada quando uma planilha não pode ser lida ou possui dados inválidos.
 */
public class PlanilhaInvalidaException extends RuntimeException {

    public PlanilhaInvalidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
