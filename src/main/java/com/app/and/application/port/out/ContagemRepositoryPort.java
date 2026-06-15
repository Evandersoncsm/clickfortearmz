package com.app.and.application.port.out;

import com.app.and.domain.model.ContagemDiaria;
import com.app.and.domain.model.DiaContagem;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída: persiste contagens. Upsert por dia — importar um dia já
 * existente apenas atualiza seus itens.
 */
public interface ContagemRepositoryPort {

    void salvar(ContagemDiaria contagem);

    /** A contagem do dia mais recente. */
    Optional<ContagemDiaria> ultima();

    /** Uma contagem específica pelo identificador do dia. */
    Optional<ContagemDiaria> porChave(String chave);

    /** Todos os dias persistidos, do mais recente para o mais antigo. */
    List<DiaContagem> listarDias();
}
