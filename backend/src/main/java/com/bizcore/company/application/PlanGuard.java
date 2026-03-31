package com.bizcore.company.application;

import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import com.bizcore.shared.exception.ForbiddenException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Valida que el tenant tenga el plan mínimo requerido.
 * Lanza {@link ForbiddenException} si el plan es insuficiente.
 */
@Component
@RequiredArgsConstructor
public class PlanGuard {

    private final CompanyRepositoryPort companyRepo;

    /** Requiere plan Standard o superior (Standard, Premium). */
    public void requireStandard(UUID tenantId) {
        require(tenantId, SubscriptionPlan.STANDARD);
    }

    /** Requiere plan Premium. */
    public void requirePremium(UUID tenantId) {
        require(tenantId, SubscriptionPlan.PREMIUM);
    }

    private void require(UUID tenantId, SubscriptionPlan minimum) {
        Company company = companyRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", tenantId));

        if (!meetsMinimum(company.plan(), minimum)) {
            throw new ForbiddenException(
                    "Esta funcionalidad requiere el plan " + minimum.name() + " o superior. " +
                    "Tu plan actual es " + company.plan().name() + "."
            );
        }
    }

    private boolean meetsMinimum(SubscriptionPlan actual, SubscriptionPlan minimum) {
        return planLevel(actual) >= planLevel(minimum);
    }

    private int planLevel(SubscriptionPlan plan) {
        return switch (plan) {
            case TRIAL   -> 0;
            case BASIC   -> 1;
            case STANDARD -> 2;
            case PREMIUM  -> 3;
        };
    }
}
