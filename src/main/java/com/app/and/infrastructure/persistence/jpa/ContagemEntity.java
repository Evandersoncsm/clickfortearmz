package com.app.and.infrastructure.persistence.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Contagem de um dia persistida. A chave identifica o dia (upsert por dia).
 */
@Entity
@Table(name = "contagem")
public class ContagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String chave;

    private String aba;

    private LocalDate dataContagem;

    @OneToMany(mappedBy = "contagem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ordem")
    private List<ItemContagemEntity> itens = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getAba() {
        return aba;
    }

    public void setAba(String aba) {
        this.aba = aba;
    }

    public LocalDate getDataContagem() {
        return dataContagem;
    }

    public void setDataContagem(LocalDate dataContagem) {
        this.dataContagem = dataContagem;
    }

    public List<ItemContagemEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemContagemEntity> itens) {
        this.itens = itens;
    }
}
