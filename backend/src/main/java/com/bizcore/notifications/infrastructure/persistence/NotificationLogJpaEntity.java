package com.bizcore.notifications.infrastructure.persistence;

import com.bizcore.notifications.domain.model.NotificationChannel;
import com.bizcore.notifications.domain.model.NotificationLog;
import com.bizcore.notifications.domain.model.NotificationStatus;
import com.bizcore.notifications.domain.model.NotificationType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_logs")
public class NotificationLogJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false, length = 255)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NotificationStatus status;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected NotificationLogJpaEntity() {}

    public static NotificationLogJpaEntity from(NotificationLog log) {
        NotificationLogJpaEntity e = new NotificationLogJpaEntity();
        e.id = log.id();
        e.tenantId = log.tenantId();
        e.type = log.type();
        e.channel = log.channel();
        e.recipient = log.recipient();
        e.status = log.status();
        e.errorMessage = log.errorMessage();
        e.sentAt = log.sentAt();
        return e;
    }

    public NotificationLog toDomain() {
        return new NotificationLog(id, tenantId, type, channel, recipient, status, errorMessage, sentAt);
    }
}
