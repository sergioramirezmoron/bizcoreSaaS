package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.RegisterCompanyRequest;
import com.bizcore.company.application.dto.RegisterCompanyResponse;

public interface RegisterCompanyUseCase {
    RegisterCompanyResponse register(RegisterCompanyRequest request);
}
