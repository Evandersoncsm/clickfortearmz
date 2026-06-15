package com.app.and.domain.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agregado da programação de produção importada, com totalizadores e visões.
 */
public class ProgramacaoProducao {

    private final List<ItemProducao> itens;

    public ProgramacaoProducao(List<ItemProducao> itens) {
        this.itens = List.copyOf(itens);
    }

    public List<ItemProducao> getItens() {
        return itens;
    }

    public boolean isVazia() {
        return itens.isEmpty();
    }

    public int getTotalItens() {
        return itens.size();
    }

    public int getTotalQuantidade() {
        return itens.stream().mapToInt(ItemProducao::getQuantidade).sum();
    }

    public int getTotalFaltaEmbalar() {
        return itens.stream().mapToInt(ItemProducao::getFaltaEmbalar).sum();
    }

    public long quantidade(StatusProducao status) {
        return itens.stream().filter(i -> i.getStatus() == status).count();
    }

    /** Quantidade (soma de peças) por fabricante, preservando a ordem de aparição. */
    public Map<String, Integer> getQuantidadePorFabricante() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        for (ItemProducao item : itens) {
            mapa.merge(item.getFabricante(), item.getQuantidade(), Integer::sum);
        }
        return mapa;
    }

    /** Itens agrupados por fabricante, preservando a ordem de aparição. */
    public Map<String, List<ItemProducao>> getItensPorFabricante() {
        Map<String, List<ItemProducao>> mapa = new LinkedHashMap<>();
        for (ItemProducao item : itens) {
            String fab = item.getFabricante().isBlank() ? "Sem fabricante" : item.getFabricante();
            mapa.computeIfAbsent(fab, k -> new ArrayList<>()).add(item);
        }
        return mapa;
    }

    /** Unidades já embaladas = quantidade total menos o que falta embalar (nunca negativo). */
    public int getTotalEmbalado() {
        return itens.stream()
                .mapToInt(i -> Math.max(0, i.getQuantidade() - i.getFaltaEmbalar()))
                .sum();
    }

    /** Soma de unidades (quantidade) por status, na ordem EMBALANDO, EM_PRODUCAO, INDEFINIDO. */
    public Map<StatusProducao, Integer> getUnidadesPorStatus() {
        Map<StatusProducao, Integer> mapa = new LinkedHashMap<>();
        for (StatusProducao s : List.of(
                StatusProducao.EMBALANDO, StatusProducao.EM_PRODUCAO, StatusProducao.INDEFINIDO)) {
            int soma = itens.stream().filter(i -> i.getStatus() == s).mapToInt(ItemProducao::getQuantidade).sum();
            if (soma > 0) {
                mapa.put(s, soma);
            }
        }
        return mapa;
    }

    /** Contagem de itens por status, na ordem EMBALANDO, EM_PRODUCAO, INDEFINIDO. */
    public Map<StatusProducao, Long> getResumoPorStatus() {
        Map<StatusProducao, Long> resumo = new LinkedHashMap<>();
        for (StatusProducao s : List.of(
                StatusProducao.EMBALANDO, StatusProducao.EM_PRODUCAO, StatusProducao.INDEFINIDO)) {
            long qtd = quantidade(s);
            if (qtd > 0) {
                resumo.put(s, qtd);
            }
        }
        return resumo;
    }
}
