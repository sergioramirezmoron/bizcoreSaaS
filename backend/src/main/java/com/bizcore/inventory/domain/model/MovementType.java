package com.bizcore.inventory.domain.model;

public enum MovementType {
    ORDER,       // salida por pedido de venta
    PURCHASE,    // entrada por compra a proveedor
    ADJUSTMENT,  // ajuste manual
    RETURN,      // devolución de cliente
    TRANSFER     // transferencia entre sucursales
}
