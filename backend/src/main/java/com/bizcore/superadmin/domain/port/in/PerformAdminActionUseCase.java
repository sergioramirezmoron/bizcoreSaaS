package com.bizcore.superadmin.domain.port.in;

import com.bizcore.superadmin.application.dto.AdminActionDto;
import com.bizcore.superadmin.application.dto.AdminActionRequest;

import java.util.UUID;

public interface PerformAdminActionUseCase {
    AdminActionDto perform(UUID companyId, UUID performedBy, AdminActionRequest request);
}
