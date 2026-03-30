package com.bizcore.billing.domain.port.out;

import com.bizcore.company.domain.model.Company;

import java.util.Optional;
import java.util.UUID;

public interface BillingCompanyPort {
    Optional<Company> findById(UUID tenantId);
    Company save(Company company);
}
