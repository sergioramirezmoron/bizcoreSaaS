package com.bizcore.superadmin.infrastructure.persistence;

import com.bizcore.superadmin.domain.model.AdminAction;
import com.bizcore.superadmin.domain.model.AdminActionType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_actions")
public class AdminActionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private AdminActionType actionType;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String value;

    @Column(name = "performed_by", nullable = false)
    private UUID performedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AdminActionJpaEntity() {}

    public static AdminActionJpaEntity from(AdminAction a) {
        AdminActionJpaEntity e = new AdminActionJpaEntity();
        e.id = a.id();
        e.companyId = a.companyId();
        e.actionType = a.actionType();
        e.description = a.description();
        e.value = a.valueJson();
        e.performedBy = a.performedBy();
        e.createdAt = a.createdAt();
        return e;
    }

    public AdminAction toDomain() {
        return new AdminAction(id, companyId, actionType, description, value, performedBy, createdAt);
    }
}
