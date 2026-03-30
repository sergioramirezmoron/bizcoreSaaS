package com.bizcore.expenses.application.dto;

import com.bizcore.expenses.domain.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
        UUID branchId,

        @NotNull
        ExpenseCategory category,

        @NotBlank
        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        LocalDate dueDate,
        String supplierName,
        String attachmentUrl,
        String notes
) {}
