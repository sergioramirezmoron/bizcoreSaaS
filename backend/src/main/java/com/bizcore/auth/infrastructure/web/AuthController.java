package com.bizcore.auth.infrastructure.web;

import com.bizcore.auth.application.dto.*;
import com.bizcore.auth.domain.port.in.LoginUseCase;
import com.bizcore.auth.domain.port.in.LogoutUseCase;
import com.bizcore.auth.domain.port.in.RefreshTokenUseCase;
import com.bizcore.auth.domain.port.out.UserRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación y gestión de tokens")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UserRepositoryPort userRepo;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest body,
            HttpServletRequest request
    ) {
        LoginRequest enriched = new LoginRequest(
                body.email(),
                body.password(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
        return ResponseEntity.ok(ApiResponse.ok(loginUseCase.login(enriched)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token usando refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshRequest body
    ) {
        return ResponseEntity.ok(ApiResponse.ok(refreshTokenUseCase.refresh(body)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión y revocar refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshRequest body
    ) {
        logoutUseCase.logout(body.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada correctamente"));
    }

    @GetMapping("/me")
    @Operation(summary = "Información del usuario autenticado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserInfoResponse>> me(
            @AuthenticationPrincipal UUID userId
    ) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return ResponseEntity.ok(ApiResponse.ok(new UserInfoResponse(
                user.id(),
                user.tenantId(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.role()
        )));
    }
}
