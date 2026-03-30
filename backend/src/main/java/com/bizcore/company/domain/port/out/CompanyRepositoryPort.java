package com.bizcore.company.domain.port.out;

import com.bizcore.company.domain.model.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepositoryPort {
    Company save(Company company);
    Optional<Company> findById(UUID id);
}
