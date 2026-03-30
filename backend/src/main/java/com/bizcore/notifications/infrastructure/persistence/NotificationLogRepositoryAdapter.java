package com.bizcore.notifications.infrastructure.persistence;

import com.bizcore.notifications.domain.model.NotificationLog;
import com.bizcore.notifications.domain.port.out.NotificationLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationLogRepositoryAdapter implements NotificationLogRepositoryPort {

    private final NotificationLogJpaRepository jpaRepository;

    @Override
    public NotificationLog save(NotificationLog log) {
        return jpaRepository.save(NotificationLogJpaEntity.from(log)).toDomain();
    }

    @Override
    public Page<NotificationLog> findByTenantId(UUID tenantId, Pageable pageable) {
        return jpaRepository.findByTenantId(tenantId, pageable)
                .map(NotificationLogJpaEntity::toDomain);
    }
}
