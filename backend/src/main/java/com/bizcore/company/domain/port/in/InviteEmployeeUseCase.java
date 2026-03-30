package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.company.application.dto.InviteEmployeeRequest;

import java.util.UUID;

public interface InviteEmployeeUseCase {
    EmployeeResponse invite(UUID tenantId, InviteEmployeeRequest request);
}
