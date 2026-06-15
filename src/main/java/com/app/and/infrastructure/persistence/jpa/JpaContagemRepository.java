package com.app.and.infrastructure.persistence.jpa;

import com.app.and.application.port.out.ContagemRepositoryPort;
import com.app.and.domain.model.ContagemDiaria;
import com.app.and.domain.model.DiaContagem;
import com.app.and.domain.model.ItemContagem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de saída sobre H2/JPA. Faz upsert por dia: importar um dia já
 * existente substitui seus itens em vez de duplicar.
 */
@Repository
public class JpaContagemRepository implements ContagemRepositoryPort {

    private final ContagemJpaRepository jpa;

    public JpaContagemRepository(ContagemJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public void salvar(ContagemDiaria contagem) {
        String chave = chaveDe(contagem);
        ContagemEntity entity = jpa.findByChave(chave).orElseGet(ContagemEntity::new);
        entity.setChave(chave);
        entity.setAba(contagem.getAba());
        entity.setDataContagem(contagem.getDataContagem());
        entity.getItens().clear();
        for (ItemContagem item : contagem.getItens()) {
            entity.getItens().add(paraEntity(item, entity));
        }
        jpa.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContagemDiaria> ultima() {
        return jpa.findAllByOrderByDataContagemDesc().stream().findFirst().map(this::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContagemDiaria> porChave(String chave) {
        return jpa.findByChave(chave).map(this::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiaContagem> listarDias() {
        return jpa.findAllByOrderByDataContagemDesc().stream()
                .map(e -> new DiaContagem(e.getChave(), e.getAba(), e.getDataContagem(), e.getItens().size()))
                .toList();
    }

    private String chaveDe(ContagemDiaria contagem) {
        return contagem.getDataContagem() != null
                ? contagem.getDataContagem().toString()
                : contagem.getAba();
    }

    private ItemContagemEntity paraEntity(ItemContagem item, ContagemEntity contagem) {
        ItemContagemEntity e = new ItemContagemEntity();
        e.setContagem(contagem);
        e.setSku(item.getSku());
        e.setCodigoProduto(item.getCodigoProduto());
        e.setContagemAnterior(item.getContagemAnterior());
        e.setRecebimento(item.getRecebimento());
        e.setProducao(item.getProducao());
        e.setFti(item.getFti());
        e.setContagemAtual(item.getContagemAtual());
        e.setConsumoReferencia(item.getConsumoReferencia());
        return e;
    }

    private ContagemDiaria paraDominio(ContagemEntity e) {
        List<ItemContagem> itens = e.getItens().stream()
                .map(i -> new ItemContagem(
                        i.getSku(), i.getCodigoProduto(), i.getContagemAnterior(), i.getRecebimento(),
                        i.getProducao(), i.getFti(), i.getContagemAtual(), i.getConsumoReferencia()))
                .toList();
        return new ContagemDiaria(e.getAba(), e.getDataContagem(), itens);
    }
}
