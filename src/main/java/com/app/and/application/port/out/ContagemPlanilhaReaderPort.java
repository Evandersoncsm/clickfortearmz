package com.app.and.application.port.out;

import com.app.and.domain.model.ContagemDiaria;

import java.io.InputStream;

/**
 * Porta de saída: lê a contagem do dia mais recente de um workbook .xlsx.
 */
public interface ContagemPlanilhaReaderPort {

    ContagemDiaria lerDiaMaisRecente(InputStream workbook);
}
