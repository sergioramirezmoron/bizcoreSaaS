package com.bizcore.superadmin.application.usecase;

import com.bizcore.auth.domain.port.out.JwtPort;
import com.bizcore.auth.infrastructure.persistence.UserJpaEntity;
import com.bizcore.auth.infrastructure.persistence.UserJpaRepository;
import com.bizcore.company.infrastructure.persistence.CompanyJpaEntity;
import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.superadmin.application.dto.ImpersonationTokenResponse;
import com.bizcore.superadmin.domain.port.in.ImpersonateUserUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImpersonateUserUseCaseImpl implements ImpersonateUserUseCase {

    private final AdminCompanyJpaRepository companyRepo;
    private final UserJpaRepository         userRepo;
    private final JwtPort                   jwtPort;

    @Override
    public ImpersonationTokenResponse impersonate(UUID companyId, UUID targetUserId) {
        CompanyJpaEntity company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));

        UserJpaEntity user = userRepo.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));

        String token = jwtPort.generateAccessToken(
                user.getId(),
                user.getTenantId(),
                user.getEmail(),
                user.getRole()
        );

        return new ImpersonationTokenResponse(token, user.getEmail(), company.getName());
    }
}
