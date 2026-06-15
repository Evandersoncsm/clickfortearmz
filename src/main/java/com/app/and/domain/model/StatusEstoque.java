package com.app.and.domain.model;

import java.math.BigDecimal;

/**
 * Classificação do nível de estoque, derivada da cobertura (meses de estoque).
 * Replica a regra da planilha: cobertura &gt; 0,6 = OK; &gt; 0,3 = REGULAR;
 * caso contrário CRITICO. Sem cobertura calculável = INDEFINIDO.
 */
public enum StatusEstoque {

    OK,
    REGULAR,
    CRITICO,
    INDEFINIDO;

    private static final BigDecimal LIMITE_OK = new BigDecimal("0.6");
    private static final BigDecimal LIMITE_REGULAR = new BigDecimal("0.3");

    public static StatusEstoque deCobertura(BigDecimal cobertura) {
        if (cobertura == null) {
            return INDEFINIDO;
        }
        if (cobertura.compareTo(LIMITE_OK) > 0) {
            return OK;
        }
        if (cobertura.compareTo(LIMITE_REGULAR) > 0) {
            return REGULAR;
        }
        return CRITICO;
    }
}
