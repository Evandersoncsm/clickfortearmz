package com.app.and.application.port.out;

import com.app.and.domain.model.ProgramacaoProducao;

import java.io.InputStream;

/**
 * Porta de saída: lê a programação de produção de um workbook .xlsx.
 */
public interface ProducaoPlanilhaReaderPort {

    ProgramacaoProducao ler(InputStream workbook);
}
