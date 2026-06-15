package com.app.and.domain.model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Relatório derivado de uma {@link ContagemDiaria}. Concentra todas as
 * visões pedidas: situação por SKU, itens que precisam de reposição,
 * resumo por status e variação em relação à contagem anterior.
 */
public class Relatorio {

    private final ContagemDiaria contagem;

    public Relatorio(ContagemDiaria contagem) {
        this.contagem = contagem;
    }

    public boolean isVazio() {
        return contagem == null || contagem.isVazia();
    }

    public String getAba() {
        return contagem == null ? null : contagem.getAba();
    }

    public LocalDate getDataContagem() {
        return contagem == null ? null : contagem.getDataContagem();
    }

    /** Todos os itens, situação atual por SKU. */
    public List<ItemContagem> getItens() {
        return contagem == null ? List.of() : contagem.getItens();
    }

    /** Itens críticos e regulares, ordenados pela menor cobertura primeiro. */
    public List<ItemContagem> getItensParaReposicao() {
        return getItens().stream()
                .filter(ItemContagem::precisaReposicao)
                .sorted((a, b) -> a.getPeriodoEstoque().compareTo(b.getPeriodoEstoque()))
                .toList();
    }

    public int getTotalItens() {
        return getItens().size();
    }

    /** Quantidade de itens por status, na ordem CRITICO, REGULAR, OK, INDEFINIDO. */
    public Map<StatusEstoque, Long> getResumoPorStatus() {
        Map<StatusEstoque, Long> resumo = new LinkedHashMap<>();
        for (StatusEstoque status : List.of(
                StatusEstoque.CRITICO, StatusEstoque.REGULAR, StatusEstoque.OK, StatusEstoque.INDEFINIDO)) {
            long qtd = getItens().stream().filter(i -> i.getStatus() == status).count();
            if (qtd > 0) {
                resumo.put(status, qtd);
            }
        }
        return resumo;
    }

    public long quantidade(StatusEstoque status) {
        return getItens().stream().filter(i -> i.getStatus() == status).count();
    }

    /** Cobertura média de estoque (coluna Período de Estoque). */
    public java.math.BigDecimal getCoberturaMedia() {
        if (getTotalItens() == 0) {
            return java.math.BigDecimal.ZERO;
        }
        return getItens().stream()
                .map(ItemContagem::getPeriodoEstoque)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .divide(java.math.BigDecimal.valueOf(getTotalItens()), 2, java.math.RoundingMode.HALF_UP);
    }

    /** Os N itens com menor cobertura (mais urgentes para reposição). */
    public List<ItemContagem> getTopUrgentes(int n) {
        return getItens().stream()
                .sorted((a, b) -> a.getPeriodoEstoque().compareTo(b.getPeriodoEstoque()))
                .limit(n)
                .toList();
    }

    /** Os N itens com maior estoque atual (contagem atual). */
    public List<ItemContagem> getTopEstoque(int n) {
        return getItens().stream()
                .sorted((a, b) -> Integer.compare(b.getContagemAtual(), a.getContagemAtual()))
                .limit(n)
                .toList();
    }

    /** Os N itens com maior saída no dia (coluna PROD), excluindo zerados. */
    public List<ItemContagem> getTopSaidas(int n) {
        return getItens().stream()
                .filter(i -> i.getProducao() > 0)
                .sorted((a, b) -> Integer.compare(b.getProducao(), a.getProducao()))
                .limit(n)
                .toList();
    }

    public double percentual(StatusEstoque status) {
        if (getTotalItens() == 0) {
            return 0d;
        }
        long qtd = getItens().stream().filter(i -> i.getStatus() == status).count();
        return qtd * 100.0 / getTotalItens();
    }
}
