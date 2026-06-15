package com.app.and.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Uma linha da contagem de estoque de um dia, correspondente a um SKU.
 * A cobertura (meses de estoque) e o status são derivados das contagens,
 * replicando as fórmulas da planilha — não dependem das células de fórmula.
 */
public class ItemContagem {

    private final String sku;
    private final String codigoProduto;
    private final int contagemAnterior;
    private final int recebimento;
    private final int producao;
    private final int fti;
    private final int contagemAtual;
    private final int consumoReferencia;
    private final BigDecimal periodoEstoque;
    private final StatusEstoque status;

    public ItemContagem(String sku, String codigoProduto, int contagemAnterior, int recebimento,
                        int producao, int fti, int contagemAtual, int consumoReferencia) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU é obrigatório");
        }
        this.sku = sku.trim();
        this.codigoProduto = codigoProduto == null ? "" : codigoProduto.trim();
        this.contagemAnterior = contagemAnterior;
        this.recebimento = recebimento;
        this.producao = producao;
        this.fti = fti;
        this.contagemAtual = contagemAtual;
        this.consumoReferencia = consumoReferencia;
        this.periodoEstoque = calcularCobertura(contagemAtual, consumoReferencia);
        this.status = StatusEstoque.deCobertura(this.periodoEstoque);
    }

    /** Cobertura = contagem atual / consumo de referência. Null quando não há consumo (divisão inválida). */
    private static BigDecimal calcularCobertura(int atual, int consumo) {
        if (consumo <= 0) {
            return null;
        }
        return BigDecimal.valueOf(atual).divide(BigDecimal.valueOf(consumo), 6, RoundingMode.HALF_UP);
    }

    /** Movimentação líquida do dia (positivo = entrada, negativo = saída). */
    public int getVariacao() {
        return contagemAtual - contagemAnterior;
    }

    public boolean precisaReposicao() {
        return status == StatusEstoque.CRITICO || status == StatusEstoque.REGULAR;
    }

    public String getSku() {
        return sku;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public int getContagemAnterior() {
        return contagemAnterior;
    }

    public int getRecebimento() {
        return recebimento;
    }

    public int getProducao() {
        return producao;
    }

    public int getFti() {
        return fti;
    }

    public int getContagemAtual() {
        return contagemAtual;
    }

    public int getConsumoReferencia() {
        return consumoReferencia;
    }

    /** Cobertura em meses; {@link BigDecimal#ZERO} quando indefinida (sem consumo). */
    public BigDecimal getPeriodoEstoque() {
        return periodoEstoque == null ? BigDecimal.ZERO : periodoEstoque;
    }

    public StatusEstoque getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemContagem that)) return false;
        return sku.equals(that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}
