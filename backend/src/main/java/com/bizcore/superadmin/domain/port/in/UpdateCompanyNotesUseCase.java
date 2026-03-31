package com.bizcore.superadmin.domain.port.in;

import java.util.UUID;

public interface UpdateCompanyNotesUseCase {
    void updateNotes(UUID companyId, String adminNotes);
}
