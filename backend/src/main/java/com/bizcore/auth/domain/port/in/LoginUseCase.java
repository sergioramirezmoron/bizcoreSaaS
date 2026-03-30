package com.bizcore.auth.domain.port.in;

import com.bizcore.auth.application.dto.LoginRequest;
import com.bizcore.auth.application.dto.TokenResponse;

public interface LoginUseCase {
    TokenResponse login(LoginRequest request);
}
