package com.bizcore.auth.domain.port.in;

public interface LogoutUseCase {
    void logout(String refreshToken);
}
