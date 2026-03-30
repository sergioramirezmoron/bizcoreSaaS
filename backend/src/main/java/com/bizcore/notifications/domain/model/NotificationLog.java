package com.bizcore.notifications.domain.model;

import java.time.Instant;
import java.util.UUID;

public record NotificationLog(
        UUID id,
        UUID tenantId,
        NotificationType type,
        NotificationChannel channel,
        String recipient,
        NotificationStatus status,
        String errorMessage,
        Instant sentAt
) {}
