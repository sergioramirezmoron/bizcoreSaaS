package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.AdjustStockRequest;
import com.bizcore.inventory.application.dto.StockMovementResponse;
import com.bizcore.inventory.domain.model.MovementType;
import com.bizcore.inventory.domain.model.StockItem;
import com.bizcore.inventory.domain.model.StockMovement;
import com.bizcore.inventory.domain.port.in.AdjustStockUseCase;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import com.bizcore.inventory.domain.port.out.StockMovementRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdjustStockUseCaseImpl implements AdjustStockUseCase {

    private final StockItemRepositoryPort stockItemRepository;
    private final StockMovementRepositoryPort movementRepository;

    @Override
    @Transactional
    public StockMovementResponse adjust(UUID tenantId, UUID stockItemId, UUID userId, AdjustStockRequest request) {
        StockItem existing = stockItemRepository.findById(tenantId, stockItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));

        BigDecimal newQty = existing.quantity().add(request.quantityChange());
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Stock cannot go below zero (current: %s, change: %s)"
                    .formatted(existing.quantity(), request.quantityChange()));
        }

        StockItem updated = new StockItem(
                existing.id(), existing.tenantId(),
                existing.productId(), existing.variantId(), existing.branchId(),
                newQty, existing.minQuantity(), existing.maxQuantity(),
                existing.location(), Instant.now()
        );
        stockItemRepository.save(updated);

        StockMovement movement = new StockMovement(
                UUID.randomUUID(),
                tenantId,
                stockItemId,
                MovementType.ADJUSTMENT,
                request.quantityChange(),
                existing.quantity(),
                newQty,
                null,
                request.notes(),
                userId,
                Instant.now()
        );
        return StockMovementResponse.from(movementRepository.save(movement));
    }
}
