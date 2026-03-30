package com.bizcore.billing.application.usecase;

import com.bizcore.billing.application.dto.CheckoutRequest;
import com.bizcore.billing.application.dto.CheckoutResponse;
import com.bizcore.billing.domain.model.PromoCode;
import com.bizcore.billing.domain.model.PromoCodeStatus;
import com.bizcore.billing.domain.port.in.CreateCheckoutSessionUseCase;
import com.bizcore.billing.domain.port.out.BillingCompanyPort;
import com.bizcore.billing.domain.port.out.PromoCodeRepositoryPort;
import com.bizcore.billing.domain.port.out.StripePort;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCheckoutSessionUseCaseImpl implements CreateCheckoutSessionUseCase {

    private final BillingCompanyPort billingCompanyPort;
    private final StripePort stripePort;
    private final PromoCodeRepositoryPort promoCodeRepository;

    @Override
    @Transactional(readOnly = true)
    public CheckoutResponse createSession(UUID tenantId, CheckoutRequest request) {
        Company company = billingCompanyPort.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        if (!company.active()) {
            throw new BusinessException("La empresa está inactiva");
        }

        if (request.plan() == SubscriptionPlan.TRIAL) {
            throw new BusinessException("No se puede contratar el plan TRIAL");
        }

        String couponId = null;
        if (request.promoCode() != null && !request.promoCode().isBlank()) {
            PromoCode promo = promoCodeRepository.findByCode(request.promoCode().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Código promocional no válido"));
            if (promo.status() != PromoCodeStatus.ACTIVE
                    || (promo.expiresAt() != null && LocalDate.now().isAfter(promo.expiresAt()))
                    || (promo.maxUses() > 0 && promo.currentUses() >= promo.maxUses())) {
                throw new BusinessException("El código promocional ha expirado o ya no es válido");
            }
            couponId = promo.stripeCouponId();
        }

        String url = stripePort.createCheckoutSession(
                tenantId, request.plan(),
                request.successUrl(), request.cancelUrl(),
                couponId);

        return new CheckoutResponse(url);
    }
}
