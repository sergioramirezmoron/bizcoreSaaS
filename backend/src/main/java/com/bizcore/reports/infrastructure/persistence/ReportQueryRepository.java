package com.bizcore.reports.infrastructure.persistence;

import com.bizcore.reports.application.dto.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReportQueryRepository {

    private final EntityManager em;

    @SuppressWarnings("unchecked")
    public List<OrderReportRow> findOrdersByPeriod(UUID tenantId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT o.id, o.order_number, o.status, o.total, o.payment_method, o.created_at
                FROM orders o
                WHERE o.tenant_id = :tenantId
                  AND o.created_at >= :from
                  AND o.created_at <= :to
                ORDER BY o.created_at DESC
                """;
        return em.createNativeQuery(sql)
                .setParameter("tenantId", tenantId)
                .setParameter("from", from.atStartOfDay().toInstant(ZoneOffset.UTC))
                .setParameter("to", to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new OrderReportRow(
                            UUID.fromString(r[0].toString()),
                            (String) r[1],
                            (String) r[2],
                            new java.math.BigDecimal(r[3].toString()),
                            r[4] != null ? r[4].toString() : null,
                            ((java.sql.Timestamp) r[5]).toInstant()
                    );
                })
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<StockReportRow> findStockReport(UUID tenantId) {
        String sql = """
                SELECT p.id, p.name, p.sku, si.quantity, si.min_quantity
                FROM stock_items si
                JOIN products p ON p.id = si.product_id
                WHERE si.tenant_id = :tenantId
                  AND p.is_active = TRUE
                ORDER BY p.name ASC
                """;
        return em.createNativeQuery(sql)
                .setParameter("tenantId", tenantId)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    var qty = new java.math.BigDecimal(r[3].toString());
                    var minQty = r[4] != null ? new java.math.BigDecimal(r[4].toString()) : java.math.BigDecimal.ZERO;
                    return new StockReportRow(
                            UUID.fromString(r[0].toString()),
                            (String) r[1],
                            r[2] != null ? (String) r[2] : null,
                            qty,
                            minQty,
                            qty.compareTo(minQty) <= 0
                    );
                })
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<ExpenseReportRow> findExpensesByPeriod(UUID tenantId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT e.id, e.expense_number, e.category, e.description, e.amount, e.status, e.due_date
                FROM expenses e
                WHERE e.tenant_id = :tenantId
                  AND e.created_at >= :from
                  AND e.created_at <= :to
                ORDER BY e.created_at DESC
                """;
        return em.createNativeQuery(sql)
                .setParameter("tenantId", tenantId)
                .setParameter("from", from.atStartOfDay().toInstant(ZoneOffset.UTC))
                .setParameter("to", to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new ExpenseReportRow(
                            UUID.fromString(r[0].toString()),
                            (String) r[1],
                            (String) r[2],
                            (String) r[3],
                            new java.math.BigDecimal(r[4].toString()),
                            (String) r[5],
                            r[6] != null ? ((java.sql.Date) r[6]).toLocalDate() : null
                    );
                })
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<TopProductRow> findTopProducts(UUID tenantId, LocalDate from, LocalDate to, int limit) {
        String sql = """
                SELECT oi.product_id, oi.product_name,
                       SUM(oi.quantity)   AS total_qty,
                       SUM(oi.line_total) AS total_revenue,
                       COUNT(DISTINCT oi.order_id) AS order_count
                FROM order_items oi
                JOIN orders o ON o.id = oi.order_id
                WHERE oi.tenant_id = :tenantId
                  AND o.status = 'COMPLETED'
                  AND o.created_at >= :from
                  AND o.created_at <= :to
                GROUP BY oi.product_id, oi.product_name
                ORDER BY total_qty DESC
                LIMIT :limit
                """;
        return em.createNativeQuery(sql)
                .setParameter("tenantId", tenantId)
                .setParameter("from", from.atStartOfDay().toInstant(ZoneOffset.UTC))
                .setParameter("to", to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .setParameter("limit", limit)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new TopProductRow(
                            r[0] != null ? UUID.fromString(r[0].toString()) : null,
                            (String) r[1],
                            new java.math.BigDecimal(r[2].toString()),
                            new java.math.BigDecimal(r[3].toString()),
                            ((Number) r[4]).longValue()
                    );
                })
                .toList();
    }
}
