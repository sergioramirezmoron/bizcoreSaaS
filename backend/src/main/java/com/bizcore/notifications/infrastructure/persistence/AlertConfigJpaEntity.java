package com.bizcore.notifications.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_configs")
public class AlertConfigJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "threshold_value", precision = 10, scale = 2)
    private java.math.BigDecimal thresholdValue;

    @Column(name = "notify_email", nullable = false)
    private boolean notifyEmail;

    @Column(name = "notify_whatsapp", nullable = false)
    private boolean notifyWhatsapp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AlertConfigJpaEntity() {}

    public static AlertConfigJpaEntity defaults(UUID tenantId, String alertType) {
        AlertConfigJpaEntity e = new AlertConfigJpaEntity();
        e.tenantId       = tenantId;
        e.alertType      = alertType;
        e.enabled        = true;
        e.notifyEmail    = true;
        e.notifyWhatsapp = false;
        e.createdAt      = Instant.now();
        return e;
    }

    // Getters
    public UUID getId()                              { return id; }
    public UUID getTenantId()                        { return tenantId; }
    public String getAlertType()                     { return alertType; }
    public boolean isEnabled()                       { return enabled; }
    public java.math.BigDecimal getThresholdValue()  { return thresholdValue; }
    public boolean isNotifyEmail()                   { return notifyEmail; }
    public boolean isNotifyWhatsapp()                { return notifyWhatsapp; }
    public Instant getCreatedAt()                    { return createdAt; }

    // Setters
    public void setEnabled(boolean v)                           { this.enabled = v; }
    public void setThresholdValue(java.math.BigDecimal v)       { this.thresholdValue = v; }
    public void setNotifyEmail(boolean v)                       { this.notifyEmail = v; }
    public void setNotifyWhatsapp(boolean v)                    { this.notifyWhatsapp = v; }
}
