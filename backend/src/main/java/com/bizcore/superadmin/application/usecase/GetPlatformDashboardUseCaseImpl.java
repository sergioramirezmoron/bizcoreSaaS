package com.bizcore.superadmin.application.usecase;

import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.superadmin.domain.model.PlatformDashboard;
import com.bizcore.superadmin.domain.port.in.GetPlatformDashboardUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPlatformDashboardUseCaseImpl implements GetPlatformDashboardUseCase {

    private final AdminCompanyJpaRepository companyRepo;

    @Override
    public PlatformDashboard getDashboard() {
        long total      = companyRepo.count();
        long active     = companyRepo.countBySubscriptionStatus(SubscriptionStatus.ACTIVE);
        long trial      = companyRepo.countBySubscriptionStatus(SubscriptionStatus.TRIAL);
        long pastDue    = companyRepo.countBySubscriptionStatus(SubscriptionStatus.PAST_DUE);
        long cancelled  = companyRepo.countBySubscriptionStatus(SubscriptionStatus.CANCELLED);
        long suspended  = companyRepo.countBySubscriptionStatus(SubscriptionStatus.SUSPENDED);
        long newLast30  = companyRepo.countNewLast30Days();
        long churned30  = companyRepo.countChurnedLast30Days();

        long basic    = companyRepo.countByPlan(SubscriptionPlan.BASIC);
        long standard = companyRepo.countByPlan(SubscriptionPlan.STANDARD);
        long premium  = companyRepo.countByPlan(SubscriptionPlan.PREMIUM);

        double conversion = (trial + active) > 0
                ? (double) active / (trial + active) * 100
                : 0.0;

        return new PlatformDashboard(total, active, trial, pastDue, cancelled, suspended,
                newLast30, churned30, conversion, basic, standard, premium);
    }
}
