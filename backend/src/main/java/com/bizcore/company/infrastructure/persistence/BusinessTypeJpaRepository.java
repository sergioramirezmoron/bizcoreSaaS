package com.bizcore.company.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusinessTypeJpaRepository extends JpaRepository<BusinessTypeJpaEntity, UUID> {
    List<BusinessTypeJpaEntity> findAllByActiveTrue();
}
