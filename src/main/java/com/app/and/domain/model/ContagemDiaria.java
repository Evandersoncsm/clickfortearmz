package com.app.and.domain.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Agregado da contagem de um dia: corresponde a uma aba da planilha.
 */
public class ContagemDiaria {

    private final String aba;
    private final LocalDate dataContagem;
    private final List<ItemContagem> itens;

    public ContagemDiaria(String aba, LocalDate dataContagem, List<ItemContagem> itens) {
        this.aba = aba;
        this.dataContagem = dataContagem;
        this.itens = List.copyOf(itens);
    }

    public String getAba() {
        return aba;
    }

    public LocalDate getDataContagem() {
        return dataContagem;
    }

    public List<ItemContagem> getItens() {
        return itens;
    }

    public boolean isVazia() {
        return itens.isEmpty();
    }
}
