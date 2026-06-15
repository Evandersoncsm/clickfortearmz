package com.app.and.infrastructure.spreadsheet;

import com.app.and.application.port.out.ContagemPlanilhaReaderPort;
import com.app.and.domain.model.ContagemDiaria;
import com.app.and.domain.model.ItemContagem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lê o workbook .xlsx de contagem de estoque e devolve a contagem do dia
 * mais recente, que corresponde à última aba (a mais à direita).
 *
 * <p>Layout esperado (índices 0-based) — coluna A vazia:
 * <ul>
 *   <li>Linha 0: datas (col. 7 = data da contagem atual)</li>
 *   <li>Linha 1: cabeçalho</li>
 *   <li>Linha 2+: dados</li>
 * </ul>
 * B=SKU, C=COD.PROD, D=Contagem anterior, E=RECEB, F=PROD., G=FTI,
 * H=Contagem Atual, I=Unidade, J=consumo de referência (MAIO),
 * K=Período de Estoque, L=STATUS.
 */
@Component
public class ApachePoiContagemReader implements ContagemPlanilhaReaderPort {

    private static final int LINHA_DATA = 0;
    private static final int PRIMEIRA_LINHA_DADOS = 2;

    private static final int COL_SKU = 1;
    private static final int COL_COD_PROD = 2;
    private static final int COL_CONTAGEM_ANTERIOR = 3;
    private static final int COL_RECEB = 4;
    private static final int COL_PROD = 5;
    private static final int COL_FTI = 6;
    private static final int COL_CONTAGEM_ATUAL = 7;
    private static final int COL_CONSUMO_REF = 9;

    @Override
    public ContagemDiaria lerDiaMaisRecente(InputStream workbookStream) {
        try (Workbook workbook = new XSSFWorkbook(workbookStream)) {
            int totalAbas = workbook.getNumberOfSheets();
            if (totalAbas == 0) {
                throw new PlanilhaInvalidaException("O arquivo não possui nenhuma aba.", null);
            }
            Sheet sheet = workbook.getSheetAt(totalAbas - 1);
            LocalDate data = lerDataContagem(sheet);
            List<ItemContagem> itens = lerItens(sheet);
            return new ContagemDiaria(sheet.getSheetName(), data, itens);
        } catch (PlanilhaInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanilhaInvalidaException("Falha ao ler a planilha de contagem: " + e.getMessage(), e);
        }
    }

    private LocalDate lerDataContagem(Sheet sheet) {
        Row linhaData = sheet.getRow(LINHA_DATA);
        return linhaData == null ? null : PlanilhaCellUtils.data(linhaData, COL_CONTAGEM_ATUAL);
    }

    private List<ItemContagem> lerItens(Sheet sheet) {
        List<ItemContagem> itens = new ArrayList<>();
        for (int i = PRIMEIRA_LINHA_DADOS; i <= sheet.getLastRowNum(); i++) {
            Row linha = sheet.getRow(i);
            if (PlanilhaCellUtils.linhaVazia(linha)) {
                continue;
            }
            String sku = PlanilhaCellUtils.texto(linha, COL_SKU);
            if (sku.isBlank()) {
                continue;
            }
            itens.add(new ItemContagem(
                    sku,
                    PlanilhaCellUtils.texto(linha, COL_COD_PROD),
                    PlanilhaCellUtils.inteiro(linha, COL_CONTAGEM_ANTERIOR),
                    PlanilhaCellUtils.inteiro(linha, COL_RECEB),
                    PlanilhaCellUtils.inteiro(linha, COL_PROD),
                    PlanilhaCellUtils.inteiro(linha, COL_FTI),
                    PlanilhaCellUtils.inteiro(linha, COL_CONTAGEM_ATUAL),
                    PlanilhaCellUtils.inteiro(linha, COL_CONSUMO_REF)));
        }
        return itens;
    }
}
