package com.bizcore.auth.domain.port.in;

import com.bizcore.auth.application.dto.RefreshRequest;
import com.bizcore.auth.application.dto.TokenResponse;

public interface RefreshTokenUseCase {
    TokenResponse refresh(RefreshRequest request);
}
