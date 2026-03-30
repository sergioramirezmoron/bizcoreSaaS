package com.bizcore.catalog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProductImage(
        UUID id,
        UUID tenantId,
        UUID productId,
        String r2Key,       // clave en Cloudflare R2 (para borrar)
        String imageUrl,    // URL pública
        long sizeBytes,
        int sortOrder,      // 0 = imagen principal
        Instant uploadedAt
) {}
