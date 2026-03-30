package com.bizcore.auth.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bizcore.jwt")
@Getter
@Setter
public class JwtProperties {

    /** Clave secreta en Base64 o texto plano (mínimo 32 chars) */
    private String secret;

    /** Expiración del access token en segundos (default: 15 min) */
    private long accessTokenExpiration = 900;

    /** Expiración del refresh token en días (default: 30 días) */
    private long refreshTokenExpirationDays = 30;
}
