package com.bizcore.superadmin.application.dto;

public record ImpersonationTokenResponse(String accessToken, String targetUserEmail, String targetCompanyName) {}
