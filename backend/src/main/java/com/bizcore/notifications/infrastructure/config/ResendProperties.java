package com.bizcore.notifications.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bizcore.resend")
@Getter
@Setter
public class ResendProperties {
    private String apiKey;
    private String fromEmail;
}
