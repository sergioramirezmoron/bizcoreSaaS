package com.bizcore.billing.infrastructure.persistence;

import com.bizcore.billing.domain.port.out.BillingCompanyPort;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.infrastructure.persistence.CompanyJpaRepository;
import com.bizcore.company.infrastructure.persistence.CompanyRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador que reutiliza CompanyJpaRepository para las operaciones de billing.
 * Billing solo necesita findById y save — el CompanyRepositoryAdapter del módulo
 * company ya expone exactamente esas dos operaciones, pero para no crear dependencias
 * cruzadas entre adapters, este delegamos directamente al JpaRepository.
 */
@Repository
@RequiredArgsConstructor
public class BillingCompanyRepositoryAdapter implements BillingCompanyPort {

    private final CompanyRepositoryAdapter companyRepositoryAdapter;

    @Override
    public Optional<Company> findById(UUID tenantId) {
        return companyRepositoryAdapter.findById(tenantId);
    }

    @Override
    public Company save(Company company) {
        return companyRepositoryAdapter.save(company);
    }
}
