package com.app.and.domain.model;

import java.time.LocalDate;

/**
 * Um item da programação de produção (uma linha de uma das linhas de produção
 * de um fabricante).
 */
public class ItemProducao {

    private final String fabricante;
    private final String linha;
    private final String produto;
    private final int quantidade;
    private final int faltaEmbalar;
    private final LocalDate previsaoSaida;
    private final StatusProducao status;

    public ItemProducao(String fabricante, String linha, String produto, int quantidade,
                        int faltaEmbalar, LocalDate previsaoSaida, StatusProducao status) {
        if (produto == null || produto.isBlank()) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        this.fabricante = fabricante == null ? "" : fabricante.trim();
        this.linha = linha == null ? "" : linha.trim();
        this.produto = produto.trim();
        this.quantidade = quantidade;
        this.faltaEmbalar = faltaEmbalar;
        this.previsaoSaida = previsaoSaida;
        this.status = status == null ? StatusProducao.INDEFINIDO : status;
    }

    public String getFabricante() {
        return fabricante;
    }

    public String getLinha() {
        return linha;
    }

    public String getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getFaltaEmbalar() {
        return faltaEmbalar;
    }

    public LocalDate getPrevisaoSaida() {
        return previsaoSaida;
    }

    public StatusProducao getStatus() {
        return status;
    }
}
