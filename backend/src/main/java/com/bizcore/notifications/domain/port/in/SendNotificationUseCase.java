package com.bizcore.notifications.domain.port.in;

import com.bizcore.notifications.application.dto.NotificationRequest;

public interface SendNotificationUseCase {
    void send(NotificationRequest request);
}
