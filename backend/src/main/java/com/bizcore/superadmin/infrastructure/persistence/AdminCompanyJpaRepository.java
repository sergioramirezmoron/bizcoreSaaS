package com.bizcore.superadmin.infrastructure.persistence;

import com.bizcore.company.infrastructure.persistence.CompanyJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AdminCompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {

    @Query("""
            SELECT c FROM CompanyJpaEntity c
            WHERE (:search IS NULL
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:plan IS NULL OR c.plan = :plan)
              AND (:status IS NULL OR c.subscriptionStatus = :status)
            """)
    Page<CompanyJpaEntity> searchCompanies(
            @Param("search") String search,
            @Param("plan") com.bizcore.company.domain.model.SubscriptionPlan plan,
            @Param("status") com.bizcore.company.domain.model.SubscriptionStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(c) FROM CompanyJpaEntity c WHERE c.subscriptionStatus = :status")
    long countBySubscriptionStatus(@Param("status") com.bizcore.company.domain.model.SubscriptionStatus status);

    @Query("SELECT COUNT(c) FROM CompanyJpaEntity c WHERE c.plan = :plan AND c.active = true")
    long countByPlan(@Param("plan") com.bizcore.company.domain.model.SubscriptionPlan plan);

    @Query(value = "SELECT COUNT(*) FROM companies WHERE created_at >= NOW() - INTERVAL '30 days'",
           nativeQuery = true)
    long countNewLast30Days();

    @Query(value = """
           SELECT COUNT(*) FROM companies
           WHERE subscription_status = 'CANCELLED'
             AND updated_at >= NOW() - INTERVAL '30 days'
           """, nativeQuery = true)
    long countChurnedLast30Days();

    @Modifying
    @Query(value = """
           UPDATE companies SET admin_notes = :notes, updated_at = now() WHERE id = :id
           """, nativeQuery = true)
    void updateAdminNotes(@Param("id") UUID id, @Param("notes") String notes);

    @Modifying
    @Query(value = """
           UPDATE companies
           SET subscription_status = :status, is_active = :active, updated_at = now()
           WHERE id = :id
           """, nativeQuery = true)
    void updateStatusAndActive(@Param("id") UUID id, @Param("status") String status, @Param("active") boolean active);

    @Modifying
    @Query(value = """
           UPDATE companies
           SET plan = CAST(:plan AS subscription_plan),
               plan_override_by_admin = :override,
               max_employees = :maxEmp,
               max_branches  = :maxBr,
               max_products  = :maxProd,
               max_product_images = :maxImg,
               updated_at = now()
           WHERE id = :id
           """, nativeQuery = true)
    void overridePlan(
            @Param("id") UUID id,
            @Param("plan") String plan,
            @Param("override") boolean override,
            @Param("maxEmp") int maxEmployees,
            @Param("maxBr") int maxBranches,
            @Param("maxProd") int maxProducts,
            @Param("maxImg") int maxProductImages
    );

    @Modifying
    @Query(value = """
           UPDATE companies
           SET plan_expires_at = plan_expires_at + (:days || ' days')::interval,
               updated_at = now()
           WHERE id = :id
           """, nativeQuery = true)
    void extendTrial(@Param("id") UUID id, @Param("days") int days);
}
