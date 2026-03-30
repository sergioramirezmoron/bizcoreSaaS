package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.RegisterCompanyRequest;
import com.bizcore.company.application.dto.RegisterCompanyResponse;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.model.EmployeeCreation;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.company.domain.port.in.RegisterCompanyUseCase;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import com.bizcore.company.domain.port.out.EmployeeRepositoryPort;
import com.bizcore.company.domain.port.out.PasswordEncoderPort;
import com.bizcore.shared.domain.UserRole;
import com.bizcore.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterCompanyUseCaseImpl implements RegisterCompanyUseCase {

    private static final int TRIAL_DAYS = 14;

    private final CompanyRepositoryPort companyRepo;
    private final EmployeeRepositoryPort employeeRepo;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public RegisterCompanyResponse register(RegisterCompanyRequest request) {
        if (employeeRepo.existsByEmail(request.ownerEmail())) {
            throw new ConflictException("El email ya está registrado: " + request.ownerEmail());
        }

        String timezone = request.timezone() != null ? request.timezone() : "Europe/Madrid";

        Company company = companyRepo.save(new Company(
                null,
                request.companyName(),
                request.businessTypeId(),
                null,   // taxId
                null,   // phone
                null,   // address
                timezone,
                null,   // logoUrl
                request.ownerEmail(),
                SubscriptionPlan.TRIAL,
                SubscriptionStatus.TRIAL,
                OffsetDateTime.now().plusDays(TRIAL_DAYS),
                null,   // stripeCustomerId
                null,   // stripeSubscriptionId
                2,      // maxEmployees (plan TRIAL)
                1,      // maxBranches
                50,     // maxProducts
                2,      // maxProductImages
                true,
                null,
                null
        ));

        String passwordHash = passwordEncoder.encode(request.ownerPassword());

        var owner = employeeRepo.create(new EmployeeCreation(
                company.id(),
                request.ownerEmail(),
                passwordHash,
                request.ownerFirstName(),
                request.ownerLastName(),
                request.ownerPhone(),
                UserRole.OWNER
        ));

        return new RegisterCompanyResponse(
                company.id(),
                company.name(),
                owner.id(),
                owner.email(),
                owner.firstName(),
                owner.lastName()
        );
    }
}
