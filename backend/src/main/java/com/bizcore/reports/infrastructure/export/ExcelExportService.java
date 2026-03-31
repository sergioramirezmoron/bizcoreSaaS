package com.bizcore.reports.infrastructure.export;

import com.bizcore.reports.application.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ExcelExportService {

    public byte[] exportOrders(List<OrderReportRow> rows, String period) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Pedidos");
            CellStyle headerStyle = buildHeaderStyle(wb);

            // Title
            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("Reporte de Pedidos — " + period);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Headers
            Row header = sheet.createRow(1);
            String[] headers = {"Nº Pedido", "Estado", "Total (€)", "Método pago", "Fecha"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int rowIdx = 2;
            for (OrderReportRow r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.orderNumber());
                row.createCell(1).setCellValue(r.status());
                row.createCell(2).setCellValue(r.total().doubleValue());
                row.createCell(3).setCellValue(r.paymentMethod() != null ? r.paymentMethod() : "");
                row.createCell(4).setCellValue(r.createdAt().toString());
            }

            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de pedidos", e);
        }
    }

    public byte[] exportStock(List<StockReportRow> rows) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Stock");
            CellStyle headerStyle = buildHeaderStyle(wb);
            CellStyle alertStyle = buildAlertStyle(wb);

            Row header = sheet.createRow(0);
            String[] headers = {"Producto", "SKU", "Stock actual", "Stock mínimo", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (StockReportRow r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.productName());
                row.createCell(1).setCellValue(r.sku() != null ? r.sku() : "");
                row.createCell(2).setCellValue(r.quantity().doubleValue());
                row.createCell(3).setCellValue(r.minQuantity().doubleValue());
                Cell statusCell = row.createCell(4);
                if (r.belowMinimum()) {
                    statusCell.setCellValue("⚠ BAJO MÍNIMO");
                    statusCell.setCellStyle(alertStyle);
                } else {
                    statusCell.setCellValue("OK");
                }
            }

            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de stock", e);
        }
    }

    public byte[] exportExpenses(List<ExpenseReportRow> rows, String period) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Gastos");
            CellStyle headerStyle = buildHeaderStyle(wb);

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("Reporte de Gastos — " + period);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            Row header = sheet.createRow(1);
            String[] headers = {"Nº Gasto", "Categoría", "Descripción", "Importe (€)", "Estado", "Fecha venc."};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 2;
            for (ExpenseReportRow r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.expenseNumber());
                row.createCell(1).setCellValue(r.category());
                row.createCell(2).setCellValue(r.description());
                row.createCell(3).setCellValue(r.amount().doubleValue());
                row.createCell(4).setCellValue(r.status());
                row.createCell(5).setCellValue(r.dueDate() != null ? r.dueDate().toString() : "");
            }

            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de gastos", e);
        }
    }

    public byte[] exportTopProducts(List<TopProductRow> rows, String period) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Top Productos");
            CellStyle headerStyle = buildHeaderStyle(wb);

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("Productos más vendidos — " + period);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            Row header = sheet.createRow(1);
            String[] headers = {"Producto", "Und. vendidas", "Ingresos (€)", "Nº pedidos"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 2;
            for (TopProductRow r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.productName());
                row.createCell(1).setCellValue(r.totalQuantitySold().doubleValue());
                row.createCell(2).setCellValue(r.totalRevenue().doubleValue());
                row.createCell(3).setCellValue(r.orderCount());
            }

            autoSizeColumns(sheet, headers.length);
            return toBytes(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de top productos", e);
        }
    }

    private CellStyle buildHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle buildAlertStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] toBytes(XSSFWorkbook wb) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }
}
