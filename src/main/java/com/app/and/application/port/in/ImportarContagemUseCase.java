package com.app.and.application.port.in;

import com.app.and.domain.model.ContagemDiaria;

import java.io.InputStream;

/**
 * Porta de entrada: importa o workbook de contagem e persiste o dia mais recente.
 */
public interface ImportarContagemUseCase {

    /**
     * @param workbook conteúdo do arquivo .xlsx de contagem mensal
     * @return a contagem do dia mais atual (última aba) importada
     */
    ContagemDiaria importar(InputStream workbook);
}
