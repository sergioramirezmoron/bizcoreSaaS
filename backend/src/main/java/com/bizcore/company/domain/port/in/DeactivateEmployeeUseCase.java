package com.bizcore.company.domain.port.in;

import java.util.UUID;

public interface DeactivateEmployeeUseCase {
    void deactivate(UUID tenantId, UUID employeeId);
}
