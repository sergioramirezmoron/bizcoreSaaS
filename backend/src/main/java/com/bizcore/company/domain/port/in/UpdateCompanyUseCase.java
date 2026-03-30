package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.CompanyResponse;
import com.bizcore.company.application.dto.UpdateCompanyRequest;

import java.util.UUID;

public interface UpdateCompanyUseCase {
    CompanyResponse update(UUID tenantId, UpdateCompanyRequest request);
}
