package com.app.and;

import com.app.and.domain.model.ContagemDiaria;
import com.app.and.infrastructure.spreadsheet.ApachePoiContagemReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApachePoiContagemReaderTest {

    @Test
    @EnabledIfSystemProperty(named = "planilha", matches = ".+")
    void leDiaMaisRecenteDoArquivoReal() throws Exception {
        String caminho = System.getProperty("planilha");
        try (InputStream in = new FileInputStream(caminho)) {
            ContagemDiaria contagem = new ApachePoiContagemReader().lerDiaMaisRecente(in);
            assertNotNull(contagem);
            assertFalse(contagem.isVazia());
            System.out.printf("Aba=%s data=%s itens=%d%n",
                    contagem.getAba(), contagem.getDataContagem(), contagem.getItens().size());
        }
    }
}
