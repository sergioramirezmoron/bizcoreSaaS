package com.bizcore.superadmin.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImpersonateRequest(@NotNull UUID targetUserId) {}
