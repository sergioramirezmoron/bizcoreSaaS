package com.bizcore.notifications.domain.port.out;

import com.bizcore.notifications.domain.model.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationLogRepositoryPort {
    NotificationLog save(NotificationLog log);
    Page<NotificationLog> findByTenantId(UUID tenantId, Pageable pageable);
}
