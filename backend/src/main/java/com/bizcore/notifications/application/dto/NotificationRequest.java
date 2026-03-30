package com.bizcore.notifications.application.dto;

import com.bizcore.notifications.domain.model.NotificationChannel;
import com.bizcore.notifications.domain.model.NotificationType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record NotificationRequest(
        UUID tenantId,
        NotificationType type,
        Set<NotificationChannel> channels,
        String recipientEmail,
        String recipientPhone,
        Map<String, String> params
) {}
