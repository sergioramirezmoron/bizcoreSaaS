package com.bizcore.shared.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de auditoría por tenant.
 * Tabla sin RLS propia — el aislamiento se garantiza mediante tenant_id en queries.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String action;        // CREATE | UPDATE | DELETE | LOGIN | LOGOUT | etc.

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "old_value", columnDefinition = "jsonb")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "jsonb")
    private String newValue;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AuditLogJpaEntity() {}

    public static AuditLogJpaEntity of(UUID tenantId, UUID userId, String action,
                                       String entityType, UUID entityId,
                                       String newValue, String ipAddress) {
        AuditLogJpaEntity e = new AuditLogJpaEntity();
        e.tenantId   = tenantId;
        e.userId     = userId;
        e.action     = action;
        e.entityType = entityType;
        e.entityId   = entityId;
        e.newValue   = newValue;
        e.ipAddress  = ipAddress;
        e.createdAt  = Instant.now();
        return e;
    }

    // Getters
    public UUID getId()          { return id; }
    public UUID getTenantId()    { return tenantId; }
    public UUID getUserId()      { return userId; }
    public String getAction()    { return action; }
    public String getEntityType(){ return entityType; }
    public UUID getEntityId()    { return entityId; }
    public String getOldValue()  { return oldValue; }
    public String getNewValue()  { return newValue; }
    public String getIpAddress() { return ipAddress; }
    public Instant getCreatedAt(){ return createdAt; }

    public void setOldValue(String v) { this.oldValue = v; }
}
