package com.bizcore.superadmin.domain.port.in;

import com.bizcore.superadmin.application.dto.CompanyDetailDto;

import java.util.UUID;

public interface GetCompanyDetailUseCase {
    CompanyDetailDto get(UUID companyId);
}
