package com.bizcore.company.domain.port.out;

import com.bizcore.company.domain.model.BusinessType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessTypeRepositoryPort {
    List<BusinessType> findAllActive();
    Optional<BusinessType> findById(UUID id);
}
