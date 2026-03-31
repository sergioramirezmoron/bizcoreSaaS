package com.bizcore.superadmin.application.dto;

import com.bizcore.superadmin.domain.model.AdminActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminActionRequest(
        @NotNull AdminActionType actionType,
        @NotBlank String description,
        String valueJson  // optional JSON payload: {"months":2}, {"percent":20,"duration":3}, {"plan":"STANDARD"}, {"days":14}
) {}
