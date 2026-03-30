package com.bizcore.notifications.application.dto;

import com.bizcore.notifications.domain.model.NotificationChannel;
import com.bizcore.notifications.domain.model.NotificationLog;
import com.bizcore.notifications.domain.model.NotificationStatus;
import com.bizcore.notifications.domain.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationLogResponse(
        UUID id,
        NotificationType type,
        NotificationChannel channel,
        String recipient,
        NotificationStatus status,
        String errorMessage,
        Instant sentAt
) {
    public static NotificationLogResponse from(NotificationLog log) {
        return new NotificationLogResponse(log.id(), log.type(), log.channel(),
                log.recipient(), log.status(), log.errorMessage(), log.sentAt());
    }
}
