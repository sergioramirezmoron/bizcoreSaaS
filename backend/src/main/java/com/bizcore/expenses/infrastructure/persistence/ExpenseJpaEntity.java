package com.bizcore.expenses.infrastructure.persistence;

import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class ExpenseJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "expense_number", nullable = false, length = 30)
    private String expenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseCategory category;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseStatus status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "created_by_id", nullable = false)
    private UUID createdById;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ExpenseJpaEntity() {}

    public static ExpenseJpaEntity from(Expense e) {
        ExpenseJpaEntity entity = new ExpenseJpaEntity();
        entity.id = e.id();
        entity.tenantId = e.tenantId();
        entity.branchId = e.branchId();
        entity.expenseNumber = e.expenseNumber();
        entity.category = e.category();
        entity.description = e.description();
        entity.amount = e.amount();
        entity.status = e.status();
        entity.dueDate = e.dueDate();
        entity.paidAt = e.paidAt();
        entity.supplierName = e.supplierName();
        entity.attachmentUrl = e.attachmentUrl();
        entity.notes = e.notes();
        entity.createdById = e.createdById();
        entity.createdAt = e.createdAt();
        entity.updatedAt = e.updatedAt();
        return entity;
    }

    public Expense toDomain() {
        return new Expense(id, tenantId, branchId, expenseNumber, category, description,
                amount, status, dueDate, paidAt, supplierName, attachmentUrl,
                notes, createdById, createdAt, updatedAt);
    }
}
