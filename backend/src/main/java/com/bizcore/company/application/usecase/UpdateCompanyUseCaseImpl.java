package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.CompanyResponse;
import com.bizcore.company.application.dto.UpdateCompanyRequest;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.port.in.UpdateCompanyUseCase;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCompanyUseCaseImpl implements UpdateCompanyUseCase {

    private final CompanyRepositoryPort companyRepo;

    @Override
    public CompanyResponse update(UUID tenantId, UpdateCompanyRequest req) {
        Company existing = companyRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", tenantId));

        Company updated = new Company(
                existing.id(),
                req.name() != null ? req.name() : existing.name(),
                existing.businessTypeId(),
                req.taxId() != null ? req.taxId() : existing.taxId(),
                req.phone() != null ? req.phone() : existing.phone(),
                req.address() != null ? req.address() : existing.address(),
                req.timezone() != null ? req.timezone() : existing.timezone(),
                req.logoUrl() != null ? req.logoUrl() : existing.logoUrl(),
                existing.email(),
                existing.plan(),
                existing.subscriptionStatus(),
                existing.planExpiresAt(),
                existing.stripeCustomerId(),
                existing.stripeSubscriptionId(),
                existing.maxEmployees(),
                existing.maxBranches(),
                existing.maxProducts(),
                existing.maxProductImages(),
                existing.active(),
                existing.createdAt(),
                null  // updatedAt se fija por trigger en DB
        );

        return GetCompanyUseCaseImpl.toResponse(companyRepo.save(updated));
    }
}
