package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.company.application.dto.UpdateEmployeeRequest;

import java.util.UUID;

public interface UpdateEmployeeUseCase {
    EmployeeResponse update(UUID tenantId, UUID employeeId, UpdateEmployeeRequest request);
}
