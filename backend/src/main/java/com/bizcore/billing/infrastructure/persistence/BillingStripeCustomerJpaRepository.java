package com.bizcore.billing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingStripeCustomerJpaRepository
        extends JpaRepository<BillingStripeCustomerJpaEntity, String> {}
