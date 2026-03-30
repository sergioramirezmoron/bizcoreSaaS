package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.CompanyResponse;

import java.util.UUID;

public interface GetCompanyUseCase {
    CompanyResponse getCompany(UUID tenantId);
}
