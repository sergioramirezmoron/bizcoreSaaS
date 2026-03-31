package com.bizcore.reports.infrastructure.export;

import com.bizcore.reports.application.dto.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class PdfExportService {

    private static final DeviceRgb HEADER_BG  = new DeviceRgb(55, 65, 81);
    private static final DeviceRgb ALERT_COLOR = new DeviceRgb(220, 38, 38);

    // ── Orders ───────────────────────────────────────────────────────────────

    public byte[] exportOrders(List<OrderReportRow> rows, String period) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            PdfFont bold = boldFont();
            doc.add(new Paragraph("Reporte de Pedidos — " + period)
                    .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Total registros: " + rows.size()).setFontSize(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1.5f, 1.5f, 1.5f, 2}))
                    .useAllAvailableWidth();

            addHeaderCell(table, "Nº Pedido", bold);
            addHeaderCell(table, "Estado",    bold);
            addHeaderCell(table, "Total (€)", bold);
            addHeaderCell(table, "Pago",      bold);
            addHeaderCell(table, "Fecha",     bold);

            for (OrderReportRow r : rows) {
                addCell(table, r.orderNumber());
                addCell(table, r.status());
                addCell(table, r.total().toString());
                addCell(table, r.paymentMethod() != null ? r.paymentMethod() : "-");
                addCell(table, r.createdAt().toString().substring(0, 10));
            }
            doc.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de pedidos", e);
        }
        return out.toByteArray();
    }

    // ── Stock ─────────────────────────────────────────────────────────────────

    public byte[] exportStock(List<StockReportRow> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            PdfFont bold = boldFont();
            doc.add(new Paragraph("Reporte de Stock")
                    .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));

            long belowMin = rows.stream().filter(StockReportRow::belowMinimum).count();
            doc.add(new Paragraph("Total productos: " + rows.size() + " | Bajo mínimo: " + belowMin)
                    .setFontSize(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1.5f, 1.5f, 1.5f, 2}))
                    .useAllAvailableWidth();

            addHeaderCell(table, "Producto",     bold);
            addHeaderCell(table, "SKU",          bold);
            addHeaderCell(table, "Stock actual", bold);
            addHeaderCell(table, "Mínimo",       bold);
            addHeaderCell(table, "Estado",       bold);

            for (StockReportRow r : rows) {
                addCell(table, r.productName());
                addCell(table, r.sku() != null ? r.sku() : "-");
                addCell(table, r.quantity().toString());
                addCell(table, r.minQuantity().toString());
                Cell statusCell = new Cell().add(new Paragraph(r.belowMinimum() ? "BAJO" : "OK"));
                if (r.belowMinimum()) statusCell.setFontColor(ALERT_COLOR);
                table.addCell(statusCell);
            }
            doc.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de stock", e);
        }
        return out.toByteArray();
    }

    // ── Expenses ──────────────────────────────────────────────────────────────

    public byte[] exportExpenses(List<ExpenseReportRow> rows, String period) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            PdfFont bold = boldFont();
            doc.add(new Paragraph("Reporte de Gastos — " + period)
                    .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));

            java.math.BigDecimal totalAmount = rows.stream()
                    .map(ExpenseReportRow::amount)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            doc.add(new Paragraph("Total: " + totalAmount + " € | Registros: " + rows.size())
                    .setFontSize(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1.5f, 2.5f, 1.5f, 1.5f, 1.5f}))
                    .useAllAvailableWidth();

            addHeaderCell(table, "Nº Gasto",    bold);
            addHeaderCell(table, "Categoría",   bold);
            addHeaderCell(table, "Descripción", bold);
            addHeaderCell(table, "Importe (€)", bold);
            addHeaderCell(table, "Estado",      bold);
            addHeaderCell(table, "Vencimiento", bold);

            for (ExpenseReportRow r : rows) {
                addCell(table, r.expenseNumber());
                addCell(table, r.category());
                addCell(table, r.description());
                addCell(table, r.amount().toString());
                addCell(table, r.status());
                addCell(table, r.dueDate() != null ? r.dueDate().toString() : "-");
            }
            doc.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de gastos", e);
        }
        return out.toByteArray();
    }

    // ── Top Products ──────────────────────────────────────────────────────────

    public byte[] exportTopProducts(List<TopProductRow> rows, String period) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            PdfFont bold = boldFont();
            doc.add(new Paragraph("Productos más vendidos — " + period)
                    .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1.5f, 1.5f, 1.5f}))
                    .useAllAvailableWidth();

            addHeaderCell(table, "Producto",      bold);
            addHeaderCell(table, "Und. vendidas", bold);
            addHeaderCell(table, "Ingresos (€)",  bold);
            addHeaderCell(table, "Nº pedidos",    bold);

            for (TopProductRow r : rows) {
                addCell(table, r.productName());
                addCell(table, r.totalQuantitySold().toString());
                addCell(table, r.totalRevenue().toString());
                addCell(table, String.valueOf(r.orderCount()));
            }
            doc.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de top productos", e);
        }
        return out.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PdfFont boldFont() throws IOException {
        return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }

    private void addHeaderCell(Table table, String text, PdfFont bold) {
        table.addHeaderCell(
                new Cell().add(new Paragraph(text).setFont(bold))
                        .setBackgroundColor(HEADER_BG)
                        .setFontColor(ColorConstants.WHITE)
        );
    }

    private void addCell(Table table, String text) {
        table.addCell(new Cell().add(new Paragraph(text)));
    }
}
