package com.app.and.infrastructure.persistence;

import com.app.and.application.port.out.ProducaoRepositoryPort;
import com.app.and.domain.model.ProgramacaoProducao;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class InMemoryProducaoRepository implements ProducaoRepositoryPort {

    private final AtomicReference<ProgramacaoProducao> ultima = new AtomicReference<>();

    @Override
    public void salvar(ProgramacaoProducao programacao) {
        ultima.set(programacao);
    }

    @Override
    public Optional<ProgramacaoProducao> ultima() {
        return Optional.ofNullable(ultima.get());
    }
}
