package com.bizcore.reports.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseReportRow(
        UUID expenseId,
        String expenseNumber,
        String category,
        String description,
        BigDecimal amount,
        String status,
        LocalDate dueDate
) {}
