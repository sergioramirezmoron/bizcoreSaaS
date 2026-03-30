package com.bizcore.company.domain.port.out;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
}
