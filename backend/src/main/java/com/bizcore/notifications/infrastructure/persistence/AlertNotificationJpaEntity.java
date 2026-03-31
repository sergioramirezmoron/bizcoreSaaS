package com.bizcore.notifications.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_notifications")
public class AlertNotificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private Instant sentAt;

    protected AlertNotificationJpaEntity() {}

    public static AlertNotificationJpaEntity create(UUID tenantId, String alertType,
                                                     String title, String message, UUID referenceId) {
        AlertNotificationJpaEntity e = new AlertNotificationJpaEntity();
        e.tenantId    = tenantId;
        e.alertType   = alertType;
        e.title       = title;
        e.message     = message;
        e.referenceId = referenceId;
        e.read        = false;
        e.sentAt      = Instant.now();
        return e;
    }

    // Getters
    public UUID getId()          { return id; }
    public UUID getTenantId()    { return tenantId; }
    public String getAlertType() { return alertType; }
    public String getTitle()     { return title; }
    public String getMessage()   { return message; }
    public UUID getReferenceId() { return referenceId; }
    public boolean isRead()      { return read; }
    public Instant getSentAt()   { return sentAt; }

    public void markRead()       { this.read = true; }
}
