package com.bizcore.sales.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    Optional<CustomerJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("""
            SELECT c FROM CustomerJpaEntity c
            WHERE c.tenantId = :tenantId
              AND (:search IS NULL
                   OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.lastName)  LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.email)     LIKE LOWER(CONCAT('%', :search, '%'))
                   OR c.phone            LIKE CONCAT('%', :search, '%'))
            """)
    Page<CustomerJpaEntity> search(@Param("tenantId") UUID tenantId,
                                   @Param("search") String search,
                                   Pageable pageable);
}
