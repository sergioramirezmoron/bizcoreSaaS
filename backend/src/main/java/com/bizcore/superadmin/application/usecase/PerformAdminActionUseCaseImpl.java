package com.bizcore.superadmin.application.usecase;

import com.bizcore.billing.infrastructure.stripe.PlanLimits;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.superadmin.application.dto.AdminActionDto;
import com.bizcore.superadmin.application.dto.AdminActionRequest;
import com.bizcore.superadmin.domain.model.AdminAction;
import com.bizcore.superadmin.domain.model.AdminActionType;
import com.bizcore.superadmin.domain.port.in.PerformAdminActionUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminActionJpaEntity;
import com.bizcore.superadmin.infrastructure.persistence.AdminActionJpaRepository;
import com.bizcore.superadmin.infrastructure.persistence.AdminCompanyJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PerformAdminActionUseCaseImpl implements PerformAdminActionUseCase {

    private final AdminCompanyJpaRepository companyRepo;
    private final AdminActionJpaRepository  actionRepo;
    private final ObjectMapper              objectMapper;

    @Override
    public AdminActionDto perform(UUID companyId, UUID performedBy, AdminActionRequest request) {
        if (!companyRepo.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found: " + companyId);
        }

        applyAction(companyId, request.actionType(), request.valueJson());

        AdminAction action = new AdminAction(
                UUID.randomUUID(),
                companyId,
                request.actionType(),
                request.description(),
                request.valueJson(),
                performedBy,
                Instant.now()
        );
        actionRepo.save(AdminActionJpaEntity.from(action));

        return new AdminActionDto(action.id(), action.actionType().name(),
                action.description(), action.performedBy(), action.createdAt());
    }

    private void applyAction(UUID companyId, AdminActionType type, String valueJson) {
        switch (type) {
            case SUSPEND ->
                companyRepo.updateStatusAndActive(companyId, SubscriptionStatus.SUSPENDED.name(), false);

            case REACTIVATE ->
                companyRepo.updateStatusAndActive(companyId, SubscriptionStatus.ACTIVE.name(), true);

            case PLAN_OVERRIDE -> {
                String planName = extractString(valueJson, "plan");
                SubscriptionPlan plan = SubscriptionPlan.valueOf(planName.toUpperCase());
                companyRepo.overridePlan(
                        companyId,
                        plan.name(),
                        true,
                        PlanLimits.maxEmployees(plan),
                        PlanLimits.maxBranches(plan),
                        PlanLimits.maxProducts(plan),
                        PlanLimits.maxProductImages(plan)
                );
            }

            case TRIAL_EXTEND -> {
                int days = extractInt(valueJson, "days");
                companyRepo.extendTrial(companyId, days);
            }

            // FREE_MONTHS, PERCENT_DISCOUNT, PROMO_CODE_MANUAL, NOTE → only logged
            default -> log.info("Admin action {} logged for company {}: {}",
                    type, companyId, valueJson);
        }
    }

    private String extractString(String json, String field) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.get(field).asText();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot extract field '" + field + "' from: " + json, e);
        }
    }

    private int extractInt(String json, String field) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.get(field).asInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot extract field '" + field + "' from: " + json, e);
        }
    }
}
