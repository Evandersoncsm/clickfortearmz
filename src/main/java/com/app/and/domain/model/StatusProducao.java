package com.app.and.domain.model;

import java.text.Normalizer;

/**
 * Situação de um item na programação de produção.
 */
public enum StatusProducao {

    EMBALANDO,
    EM_PRODUCAO,
    INDEFINIDO;

    public static StatusProducao de(String bruto) {
        if (bruto == null || bruto.isBlank()) {
            return INDEFINIDO;
        }
        String n = Normalizer.normalize(bruto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase();
        if (n.startsWith("EMBAL")) {
            return EMBALANDO;
        }
        if (n.startsWith("EM PROD") || n.startsWith("PROD")) {
            return EM_PRODUCAO;
        }
        return INDEFINIDO;
    }

    /** Rótulo amigável para exibição. */
    public String getRotulo() {
        return switch (this) {
            case EMBALANDO -> "Embalando";
            case EM_PRODUCAO -> "Em Produção";
            case INDEFINIDO -> "Indefinido";
        };
    }
}
