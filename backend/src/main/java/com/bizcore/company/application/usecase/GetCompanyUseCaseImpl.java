package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.CompanyResponse;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.port.in.GetCompanyUseCase;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCompanyUseCaseImpl implements GetCompanyUseCase {

    private final CompanyRepositoryPort companyRepo;

    @Override
    public CompanyResponse getCompany(UUID tenantId) {
        Company company = companyRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", tenantId));
        return toResponse(company);
    }

    static CompanyResponse toResponse(Company c) {
        return new CompanyResponse(
                c.id(), c.name(), c.businessTypeId(), c.taxId(),
                c.phone(), c.address(), c.timezone(), c.logoUrl(),
                c.email(),
                c.plan(), c.subscriptionStatus(), c.planExpiresAt(),
                c.maxEmployees(), c.maxBranches(), c.maxProducts(), c.maxProductImages(),
                c.active(), c.createdAt()
        );
    }
}
