package com.app.and.infrastructure.spreadsheet;

import com.app.and.application.port.out.ProducaoPlanilhaReaderPort;
import com.app.and.domain.model.ItemProducao;
import com.app.and.domain.model.ProgramacaoProducao;
import com.app.and.domain.model.StatusProducao;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Lê a planilha de acompanhamento de produção (primeira aba).
 *
 * <p>Layout: blocos por fabricante (linha com o nome do fabricante), cada bloco
 * com duas programações lado a lado — Linha 1 (colunas B–F) e Linha 2 (colunas H–L).
 * Cada programação tem cabeçalho "Produto | Quantidade | Falta Embalar |
 * Previsão de saída | Status" e termina numa linha de totais (sem produto).
 */
@Component
public class ApachePoiProducaoReader implements ProducaoPlanilhaReaderPort {

    // Linha 1
    private static final int L1_PRODUTO = 1;
    private static final int L1_QUANTIDADE = 2;
    private static final int L1_FALTA = 3;
    private static final int L1_PREVISAO = 4;
    private static final int L1_STATUS = 5;
    // Linha 2
    private static final int L2_PRODUTO = 7;
    private static final int L2_QUANTIDADE = 8;
    private static final int L2_FALTA = 9;
    private static final int L2_PREVISAO = 10;
    private static final int L2_STATUS = 11;

    @Override
    public ProgramacaoProducao ler(InputStream workbookStream) {
        try (Workbook workbook = new XSSFWorkbook(workbookStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new PlanilhaInvalidaException("O arquivo não possui nenhuma aba.", null);
            }
            return new ProgramacaoProducao(lerItens(workbook.getSheetAt(0)));
        } catch (PlanilhaInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanilhaInvalidaException("Falha ao ler a planilha de produção: " + e.getMessage(), e);
        }
    }

    private List<ItemProducao> lerItens(Sheet sheet) {
        List<ItemProducao> itens = new ArrayList<>();
        String fabricante = "";
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row linha = sheet.getRow(i);
            if (linha == null) {
                continue;
            }
            String c1 = PlanilhaCellUtils.texto(linha, L1_PRODUTO);

            if (ehCabecalho(c1)) {
                i = lerBloco(sheet, i + 1, fabricante, itens);
                continue;
            }
            if (ehFabricante(linha, c1)) {
                fabricante = c1;
            }
        }
        return itens;
    }

    /** Lê as linhas de dados de um bloco e devolve o índice da última linha consumida. */
    private int lerBloco(Sheet sheet, int inicio, String fabricante, List<ItemProducao> itens) {
        int i = inicio;
        for (; i <= sheet.getLastRowNum(); i++) {
            Row linha = sheet.getRow(i);
            if (linha == null) {
                break;
            }
            String prod1 = PlanilhaCellUtils.texto(linha, L1_PRODUTO);
            String prod2 = PlanilhaCellUtils.texto(linha, L2_PRODUTO);
            if (prod1.isBlank() && prod2.isBlank()) {
                break;
            }
            if (!prod1.isBlank()) {
                itens.add(montar(fabricante, "Linha 1", linha, prod1,
                        L1_QUANTIDADE, L1_FALTA, L1_PREVISAO, L1_STATUS));
            }
            if (!prod2.isBlank()) {
                itens.add(montar(fabricante, "Linha 2", linha, prod2,
                        L2_QUANTIDADE, L2_FALTA, L2_PREVISAO, L2_STATUS));
            }
        }
        return i;
    }

    private ItemProducao montar(String fabricante, String linhaProd, Row linha, String produto,
                                int colQtd, int colFalta, int colPrev, int colStatus) {
        return new ItemProducao(
                fabricante,
                linhaProd,
                produto,
                PlanilhaCellUtils.inteiro(linha, colQtd),
                PlanilhaCellUtils.inteiro(linha, colFalta),
                PlanilhaCellUtils.data(linha, colPrev),
                StatusProducao.de(PlanilhaCellUtils.texto(linha, colStatus)));
    }

    private boolean ehCabecalho(String c1) {
        return "Produto".equalsIgnoreCase(c1.trim());
    }

    private boolean ehFabricante(Row linha, String c1) {
        if (c1.isBlank()) {
            return false;
        }
        String t = c1.trim();
        if (t.equalsIgnoreCase("Produto") || t.toLowerCase().startsWith("programa")) {
            return false;
        }
        // Nome do fabricante ocupa só a primeira coluna do bloco.
        return PlanilhaCellUtils.texto(linha, L1_QUANTIDADE).isBlank();
    }
}
