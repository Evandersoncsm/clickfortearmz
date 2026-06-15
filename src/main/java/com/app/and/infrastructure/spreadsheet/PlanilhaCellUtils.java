package com.app.and.infrastructure.spreadsheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Funções utilitárias para extrair valores de células de forma tolerante
 * (texto, número ou data), evitando duplicação entre os leitores.
 */
final class PlanilhaCellUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private PlanilhaCellUtils() {
    }

    static String texto(Row linha, int coluna) {
        Cell cell = linha.getCell(coluna);
        if (cell == null) {
            return "";
        }
        return FORMATTER.formatCellValue(cell).trim();
    }

    static BigDecimal numero(Row linha, int coluna) {
        Cell cell = linha.getCell(coluna);
        if (cell == null) {
            return BigDecimal.ZERO;
        }
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING -> parseDecimal(cell.getStringCellValue());
                case FORMULA -> numeroDeFormula(cell);
                default -> BigDecimal.ZERO;
            };
        } catch (RuntimeException e) {
            // Células com erro de fórmula (ex.: #DIV/0!) ou texto não numérico viram zero.
            return BigDecimal.ZERO;
        }
    }

    private static BigDecimal numeroDeFormula(Cell cell) {
        return switch (cell.getCachedFormulaResultType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> parseDecimal(cell.getStringCellValue());
            default -> BigDecimal.ZERO;
        };
    }

    static int inteiro(Row linha, int coluna) {
        return numero(linha, coluna).intValue();
    }

    static LocalDate data(Row linha, int coluna) {
        Cell cell = linha.getCell(coluna);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC
                && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        String valor = FORMATTER.formatCellValue(cell).trim();
        return valor.isBlank() ? null : LocalDate.parse(valor);
    }

    static boolean linhaVazia(Row linha) {
        if (linha == null) {
            return true;
        }
        for (Cell cell : linha) {
            if (cell != null && !FORMATTER.formatCellValue(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static BigDecimal parseDecimal(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(valor.trim().replace(".", "").replace(",", "."));
    }
}
