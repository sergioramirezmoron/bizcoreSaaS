package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.Refund;
import com.bizcore.sales.domain.port.out.RefundRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefundRepositoryAdapter implements RefundRepositoryPort {

    private final RefundJpaRepository jpaRepository;

    @Override
    public Refund save(Refund refund) {
        return jpaRepository.save(RefundJpaEntity.from(refund)).toDomain();
    }

    @Override
    public List<Refund> findAllByOrderId(UUID tenantId, UUID orderId) {
        return jpaRepository.findAllByOrderIdAndTenantId(orderId, tenantId).stream()
                .map(RefundJpaEntity::toDomain).toList();
    }

    @Override
    public BigDecimal sumRefundedAmount(UUID tenantId, UUID orderId) {
        return jpaRepository.sumRefundedAmount(orderId, tenantId);
    }
}
