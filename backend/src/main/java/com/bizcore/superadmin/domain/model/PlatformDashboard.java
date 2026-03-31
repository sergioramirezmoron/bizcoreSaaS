package com.bizcore.superadmin.domain.model;

public record PlatformDashboard(
        long totalCompanies,
        long activeCompanies,
        long trialCompanies,
        long pastDueCompanies,
        long cancelledCompanies,
        long suspendedCompanies,
        long newCompaniesLast30Days,
        long churned30Days,
        double trialToPaydConversionRate,
        long basicPlanCount,
        long standardPlanCount,
        long premiumPlanCount
) {}
