package com.app.and.application.port.in;

import com.app.and.domain.model.ProgramacaoProducao;

/**
 * Porta de entrada: devolve a última programação de produção importada.
 */
public interface ConsultarProducaoUseCase {

    ProgramacaoProducao ultima();
}
