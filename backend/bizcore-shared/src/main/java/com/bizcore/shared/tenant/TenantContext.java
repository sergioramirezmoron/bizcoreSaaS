package com.bizcore.shared.tenant;

import java.util.UUID;

/**
 * Almacena el tenantId del request actual en un ThreadLocal.
 * Se inicializa en JwtAuthFilter y se limpia al finalizar el request.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
