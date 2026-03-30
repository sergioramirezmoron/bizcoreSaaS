package com.bizcore.shared.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

/**
 * Inyecta el tenantId en la sesión PostgreSQL antes de cada método @Transactional.
 *
 * El orden 10 garantiza que este aspecto corre DENTRO de la transacción
 * (el TransactionInterceptor se configura con order=5 en bizcore-bootstrap).
 * set_config con is_local=true es equivalente a SET LOCAL y se resetea al final
 * de la transacción, lo cual es correcto para RLS por conexión.
 */
@Aspect
@Component
@Order(10)
@Slf4j
public class TenantAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)" +
            " || @within(org.springframework.transaction.annotation.Transactional)")
    public void setTenantContext() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            return;
        }
        UUID tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return;
        }
        entityManager
                .createNativeQuery("SELECT set_config('app.current_tenant', :tenant, true)")
                .setParameter("tenant", tenantId.toString())
                .getSingleResult();
    }
}
