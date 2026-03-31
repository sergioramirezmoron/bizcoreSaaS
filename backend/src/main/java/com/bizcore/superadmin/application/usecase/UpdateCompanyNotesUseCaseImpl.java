package com.bizcore.superadmin.application.usecase;

import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.superadmin.domain.port.in.UpdateCompanyNotesUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCompanyNotesUseCaseImpl implements UpdateCompanyNotesUseCase {

    private final AdminCompanyJpaRepository companyRepo;

    @Override
    public void updateNotes(UUID companyId, String adminNotes) {
        if (!companyRepo.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found: " + companyId);
        }
        companyRepo.updateAdminNotes(companyId, adminNotes);
    }
}
