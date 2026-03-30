package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.InitStockItemRequest;
import com.bizcore.inventory.application.dto.StockItemResponse;
import com.bizcore.inventory.domain.model.MovementType;
import com.bizcore.inventory.domain.model.StockItem;
import com.bizcore.inventory.domain.model.StockMovement;
import com.bizcore.inventory.domain.port.in.InitStockItemUseCase;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import com.bizcore.inventory.domain.port.out.StockMovementRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitStockItemUseCaseImpl implements InitStockItemUseCase {

    private final StockItemRepositoryPort stockItemRepository;
    private final StockMovementRepositoryPort movementRepository;

    @Override
    @Transactional
    public StockItemResponse init(UUID tenantId, UUID userId, InitStockItemRequest request) {
        if (request.productId() == null && request.variantId() == null) {
            throw new BusinessException("productId or variantId is required");
        }
        if (stockItemRepository.existsByKey(tenantId, request.productId(), request.variantId(), request.branchId())) {
            throw new ConflictException("Stock item already exists for this product/variant and branch");
        }

        BigDecimal minQty = request.minQuantity() != null ? request.minQuantity() : BigDecimal.ZERO;

        StockItem item = new StockItem(
                UUID.randomUUID(),
                tenantId,
                request.productId(),
                request.variantId(),
                request.branchId(),
                request.initialQuantity(),
                minQty,
                request.maxQuantity(),
                request.location(),
                Instant.now()
        );
        StockItem saved = stockItemRepository.save(item);

        if (request.initialQuantity().compareTo(BigDecimal.ZERO) > 0) {
            StockMovement movement = new StockMovement(
                    UUID.randomUUID(),
                    tenantId,
                    saved.id(),
                    MovementType.ADJUSTMENT,
                    request.initialQuantity(),
                    BigDecimal.ZERO,
                    request.initialQuantity(),
                    null,
                    "Stock inicial",
                    userId,
                    Instant.now()
            );
            movementRepository.save(movement);
        }

        return StockItemResponse.from(saved);
    }
}
