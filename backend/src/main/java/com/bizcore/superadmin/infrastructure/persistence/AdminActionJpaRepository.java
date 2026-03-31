package com.bizcore.superadmin.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdminActionJpaRepository extends JpaRepository<AdminActionJpaEntity, UUID> {

    List<AdminActionJpaEntity> findByCompanyIdOrderByCreatedAtDesc(UUID companyId, Pageable pageable);
}
