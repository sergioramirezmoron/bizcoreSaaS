package com.bizcore.notifications.application.usecase;

import com.bizcore.notifications.application.dto.NotificationRequest;
import com.bizcore.notifications.application.template.EmailTemplateBuilder;
import com.bizcore.notifications.application.template.WhatsAppTemplateBuilder;
import com.bizcore.notifications.domain.model.NotificationChannel;
import com.bizcore.notifications.domain.model.NotificationLog;
import com.bizcore.notifications.domain.model.NotificationStatus;
import com.bizcore.notifications.domain.port.in.SendNotificationUseCase;
import com.bizcore.notifications.domain.port.out.EmailPort;
import com.bizcore.notifications.domain.port.out.NotificationLogRepositoryPort;
import com.bizcore.notifications.domain.port.out.WhatsAppPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationUseCaseImpl implements SendNotificationUseCase {

    private final EmailPort emailPort;
    private final WhatsAppPort whatsAppPort;
    private final NotificationLogRepositoryPort logRepository;

    @Override
    @Transactional
    public void send(NotificationRequest request) {
        for (NotificationChannel channel : request.channels()) {
            switch (channel) {
                case EMAIL     -> sendEmail(request);
                case WHATSAPP  -> sendWhatsApp(request);
            }
        }
    }

    private void sendEmail(NotificationRequest req) {
        if (req.recipientEmail() == null || req.recipientEmail().isBlank()) return;

        String subject = EmailTemplateBuilder.subject(req.type(), req.params());
        String html    = EmailTemplateBuilder.html(req.type(), req.params());

        NotificationStatus status;
        String error = null;
        try {
            emailPort.send(req.recipientEmail(), subject, html);
            status = NotificationStatus.SENT;
            log.debug("Email [{}] enviado a {}", req.type(), req.recipientEmail());
        } catch (Exception e) {
            status = NotificationStatus.FAILED;
            error = e.getMessage();
            log.warn("Error enviando email [{}] a {}: {}", req.type(), req.recipientEmail(), e.getMessage());
        }

        logRepository.save(new NotificationLog(UUID.randomUUID(), req.tenantId(),
                req.type(), NotificationChannel.EMAIL, req.recipientEmail(),
                status, error, Instant.now()));
    }

    private void sendWhatsApp(NotificationRequest req) {
        if (req.recipientPhone() == null || req.recipientPhone().isBlank()) return;

        String message = WhatsAppTemplateBuilder.message(req.type(), req.params());

        NotificationStatus status;
        String error = null;
        try {
            whatsAppPort.send(req.recipientPhone(), message);
            status = NotificationStatus.SENT;
            log.debug("WhatsApp [{}] enviado a {}", req.type(), req.recipientPhone());
        } catch (Exception e) {
            status = NotificationStatus.FAILED;
            error = e.getMessage();
            log.warn("Error enviando WhatsApp [{}] a {}: {}", req.type(), req.recipientPhone(), e.getMessage());
        }

        logRepository.save(new NotificationLog(UUID.randomUUID(), req.tenantId(),
                req.type(), NotificationChannel.WHATSAPP, req.recipientPhone(),
                status, error, Instant.now()));
    }
}
