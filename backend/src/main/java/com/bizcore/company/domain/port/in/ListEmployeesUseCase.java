package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListEmployeesUseCase {
    PageResponse<EmployeeResponse> list(UUID tenantId, Pageable pageable);
}
