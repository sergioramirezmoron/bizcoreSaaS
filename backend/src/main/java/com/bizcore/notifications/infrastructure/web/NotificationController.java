package com.bizcore.notifications.infrastructure.web;

import com.bizcore.notifications.application.dto.NotificationLogResponse;
import com.bizcore.notifications.domain.port.out.NotificationLogRepositoryPort;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Historial de notificaciones enviadas")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationLogRepositoryPort logRepository;

    @GetMapping("/logs")
    @Operation(summary = "Listar historial de notificaciones del tenant")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<NotificationLogResponse>>> logs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                logRepository.findByTenantId(TenantContext.getTenantId(), pageable)
                        .map(NotificationLogResponse::from)
        )));
    }
}
