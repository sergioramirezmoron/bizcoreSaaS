package com.bizcore.company.infrastructure.persistence;

import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final CompanyJpaRepository jpaRepository;

    @Override
    public Company save(Company company) {
        return toDomain(jpaRepository.save(toEntity(company)));
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Company toDomain(CompanyJpaEntity e) {
        return new Company(
                e.getId(), e.getName(), e.getBusinessTypeId(), e.getTaxId(),
                e.getPhone(), e.getAddress(), e.getTimezone(), e.getLogoUrl(),
                e.getEmail(),
                e.getPlan(), e.getSubscriptionStatus(), e.getPlanExpiresAt(),
                e.getStripeCustomerId(), e.getStripeSubscriptionId(),
                e.getMaxEmployees(), e.getMaxBranches(), e.getMaxProducts(), e.getMaxProductImages(),
                e.isActive(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    private CompanyJpaEntity toEntity(Company c) {
        return CompanyJpaEntity.builder()
                .id(c.id())
                .name(c.name())
                .businessTypeId(c.businessTypeId())
                .taxId(c.taxId())
                .phone(c.phone())
                .address(c.address())
                .timezone(c.timezone() != null ? c.timezone() : "Europe/Madrid")
                .logoUrl(c.logoUrl())
                .email(c.email())
                .plan(c.plan())
                .subscriptionStatus(c.subscriptionStatus())
                .planExpiresAt(c.planExpiresAt())
                .stripeCustomerId(c.stripeCustomerId())
                .stripeSubscriptionId(c.stripeSubscriptionId())
                .maxEmployees(c.maxEmployees())
                .maxBranches(c.maxBranches())
                .maxProducts(c.maxProducts())
                .maxProductImages(c.maxProductImages())
                .active(c.active())
                .build();
    }
}
