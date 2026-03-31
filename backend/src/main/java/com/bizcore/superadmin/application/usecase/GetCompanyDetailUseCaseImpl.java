package com.bizcore.superadmin.application.usecase;

import com.bizcore.company.infrastructure.persistence.CompanyJpaEntity;
import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.superadmin.application.dto.AdminActionDto;
import com.bizcore.superadmin.application.dto.CompanyDetailDto;
import com.bizcore.superadmin.domain.port.in.GetCompanyDetailUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminActionJpaEntity;
import com.bizcore.superadmin.infrastructure.persistence.AdminActionJpaRepository;
import com.bizcore.superadmin.infrastructure.persistence.AdminCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCompanyDetailUseCaseImpl implements GetCompanyDetailUseCase {

    private final AdminCompanyJpaRepository companyRepo;
    private final AdminActionJpaRepository actionRepo;

    @Override
    public CompanyDetailDto get(UUID companyId) {
        CompanyJpaEntity c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));

        List<AdminActionDto> recentActions = actionRepo
                .findByCompanyIdOrderByCreatedAtDesc(companyId, PageRequest.of(0, 10))
                .stream()
                .map(this::toActionDto)
                .toList();

        return new CompanyDetailDto(
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getTaxId(),
                c.getPhone(),
                c.getAddress(),
                c.getTimezone(),
                c.getPlan().name(),
                c.getSubscriptionStatus().name(),
                c.getPlanExpiresAt(),
                c.getStripeCustomerId(),
                c.getStripeSubscriptionId(),
                c.getAdminNotes(),
                c.isPlanOverrideByAdmin(),
                c.isActive(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                c.getMaxEmployees(),
                c.getMaxBranches(),
                c.getMaxProducts(),
                recentActions
        );
    }

    private AdminActionDto toActionDto(AdminActionJpaEntity a) {
        var d = a.toDomain();
        return new AdminActionDto(d.id(), d.actionType().name(), d.description(), d.performedBy(), d.createdAt());
    }
}
