package com.app.and.infrastructure.persistence.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Item (SKU) de uma contagem. Guarda apenas os dados brutos — cobertura e
 * status são derivados no domínio ao reconstruir o {@code ItemContagem}.
 */
@Entity
@Table(name = "item_contagem")
public class ItemContagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contagem_id")
    private ContagemEntity contagem;

    private String sku;
    private String codigoProduto;
    private int contagemAnterior;
    private int recebimento;
    private int producao;
    private int fti;
    private int contagemAtual;
    private int consumoReferencia;

    public void setContagem(ContagemEntity contagem) {
        this.contagem = contagem;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public int getContagemAnterior() {
        return contagemAnterior;
    }

    public void setContagemAnterior(int contagemAnterior) {
        this.contagemAnterior = contagemAnterior;
    }

    public int getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(int recebimento) {
        this.recebimento = recebimento;
    }

    public int getProducao() {
        return producao;
    }

    public void setProducao(int producao) {
        this.producao = producao;
    }

    public int getFti() {
        return fti;
    }

    public void setFti(int fti) {
        this.fti = fti;
    }

    public int getContagemAtual() {
        return contagemAtual;
    }

    public void setContagemAtual(int contagemAtual) {
        this.contagemAtual = contagemAtual;
    }

    public int getConsumoReferencia() {
        return consumoReferencia;
    }

    public void setConsumoReferencia(int consumoReferencia) {
        this.consumoReferencia = consumoReferencia;
    }
}
