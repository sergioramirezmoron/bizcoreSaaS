package com.bizcore.notifications.infrastructure.adapter;

import com.bizcore.notifications.domain.port.out.EmailPort;
import com.bizcore.notifications.infrastructure.config.ResendProperties;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResendEmailAdapter implements EmailPort {

    private final ResendProperties resendProperties;

    @Override
    public void send(String to, String subject, String htmlBody) {
        String apiKey = resendProperties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Resend API key no configurada. Email a {} omitido.", to);
            return;
        }

        try {
            Resend resend = new Resend(apiKey);
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(resendProperties.getFromEmail())
                    .to(to)
                    .subject(subject)
                    .html(htmlBody)
                    .build();
            resend.emails().send(options);
        } catch (ResendException e) {
            throw new RuntimeException("Error enviando email via Resend: " + e.getMessage(), e);
        }
    }
}
