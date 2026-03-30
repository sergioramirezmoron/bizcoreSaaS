package com.bizcore.billing.infrastructure.persistence;

import com.bizcore.billing.domain.port.out.BillingStripeCustomerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BillingStripeCustomerRepositoryAdapter implements BillingStripeCustomerPort {

    private final BillingStripeCustomerJpaRepository jpaRepository;

    @Override
    public void save(String stripeCustomerId, UUID tenantId) {
        jpaRepository.save(new BillingStripeCustomerJpaEntity(stripeCustomerId, tenantId));
    }

    @Override
    public Optional<UUID> findTenantIdByStripeCustomerId(String stripeCustomerId) {
        return jpaRepository.findById(stripeCustomerId)
                .map(BillingStripeCustomerJpaEntity::getTenantId);
    }
}
