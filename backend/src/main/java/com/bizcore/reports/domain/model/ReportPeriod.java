package com.bizcore.reports.domain.model;

import java.time.LocalDate;

public record ReportPeriod(LocalDate from, LocalDate to) {

    public static ReportPeriod currentMonth() {
        LocalDate now = LocalDate.now();
        return new ReportPeriod(now.withDayOfMonth(1), now);
    }
}
