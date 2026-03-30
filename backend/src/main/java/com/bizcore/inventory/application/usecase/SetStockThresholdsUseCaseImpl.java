package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.SetThresholdsRequest;
import com.bizcore.inventory.application.dto.StockItemResponse;
import com.bizcore.inventory.domain.model.StockItem;
import com.bizcore.inventory.domain.port.in.SetStockThresholdsUseCase;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
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
public class SetStockThresholdsUseCaseImpl implements SetStockThresholdsUseCase {

    private final StockItemRepositoryPort repository;

    @Override
    @Transactional
    public StockItemResponse setThresholds(UUID tenantId, UUID stockItemId, SetThresholdsRequest request) {
        StockItem existing = repository.findById(tenantId, stockItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));

        BigDecimal min = request.minQuantity() != null ? request.minQuantity() : existing.minQuantity();
        BigDecimal max = request.maxQuantity();

        if (max != null && min != null && max.compareTo(min) < 0) {
            throw new BusinessException("max_quantity must be >= min_quantity");
        }

        StockItem updated = new StockItem(
                existing.id(), existing.tenantId(),
                existing.productId(), existing.variantId(), existing.branchId(),
                existing.quantity(), min, max,
                request.location() != null ? request.location() : existing.location(),
                Instant.now()
        );
        return StockItemResponse.from(repository.save(updated));
    }
}
