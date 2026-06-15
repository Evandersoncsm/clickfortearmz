package com.app.and.application.port.in;

import com.app.and.domain.model.ProgramacaoProducao;

import java.io.InputStream;

/**
 * Porta de entrada: importa a planilha de acompanhamento de produção.
 */
public interface ImportarProducaoUseCase {

    ProgramacaoProducao importar(InputStream workbook);
}
