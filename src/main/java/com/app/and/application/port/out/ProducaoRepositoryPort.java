package com.app.and.application.port.out;

import com.app.and.domain.model.ProgramacaoProducao;

import java.util.Optional;

/**
 * Porta de saída: guarda a última programação de produção importada.
 */
public interface ProducaoRepositoryPort {

    void salvar(ProgramacaoProducao programacao);

    Optional<ProgramacaoProducao> ultima();
}
