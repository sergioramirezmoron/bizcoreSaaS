package com.bizcore.superadmin.domain.port.in;

import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.superadmin.application.dto.CompanySummaryDto;
import org.springframework.data.domain.Pageable;

public interface ListCompaniesUseCase {
    PageResponse<CompanySummaryDto> list(String search, SubscriptionPlan plan, SubscriptionStatus status, Pageable pageable);
}
