package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.BusinessTypeResponse;

import java.util.List;

public interface ListBusinessTypesUseCase {
    List<BusinessTypeResponse> listAll();
}
