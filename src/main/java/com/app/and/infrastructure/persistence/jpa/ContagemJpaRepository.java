package com.app.and.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContagemJpaRepository extends JpaRepository<ContagemEntity, Long> {

    Optional<ContagemEntity> findByChave(String chave);

    /** Dias ordenados do mais recente para o mais antigo (nulos por último). */
    List<ContagemEntity> findAllByOrderByDataContagemDesc();
}
