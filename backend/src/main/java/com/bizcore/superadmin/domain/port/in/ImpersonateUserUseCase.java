package com.bizcore.superadmin.domain.port.in;

import com.bizcore.superadmin.application.dto.ImpersonationTokenResponse;

import java.util.UUID;

public interface ImpersonateUserUseCase {
    ImpersonationTokenResponse impersonate(UUID companyId, UUID targetUserId);
}
