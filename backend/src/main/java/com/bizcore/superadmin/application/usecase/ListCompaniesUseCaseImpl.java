package com.bizcore.superadmin.application.usecase;

import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.company.infrastructure.persistence.CompanyJpaEntity;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.superadmin.application.dto.CompanySummaryDto;
import com.bizcore.superadmin.domain.port.in.ListCompaniesUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCompaniesUseCaseImpl implements ListCompaniesUseCase {

    private final AdminCompanyJpaRepository companyRepo;

    @Override
    public PageResponse<CompanySummaryDto> list(String search, SubscriptionPlan plan,
                                                 SubscriptionStatus status, Pageable pageable) {
        Page<CompanyJpaEntity> page = companyRepo.searchCompanies(
                search != null && !search.isBlank() ? search : null,
                plan,
                status,
                pageable
        );
        return PageResponse.from(page.map(this::toSummary));
    }

    private CompanySummaryDto toSummary(CompanyJpaEntity c) {
        return new CompanySummaryDto(
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getPlan().name(),
                c.getSubscriptionStatus().name(),
                c.isActive(),
                c.getPlanExpiresAt(),
                c.getCreatedAt()
        );
    }
}
