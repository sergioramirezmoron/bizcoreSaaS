package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.StockMovementResponse;
import com.bizcore.inventory.domain.port.in.ListStockMovementsUseCase;
import com.bizcore.inventory.domain.port.out.StockMovementRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListStockMovementsUseCaseImpl implements ListStockMovementsUseCase {

    private final StockMovementRepositoryPort movementRepository;
    private final StockItemRepositoryPort stockItemRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementResponse> list(UUID tenantId, UUID stockItemId, Pageable pageable) {
        stockItemRepository.findById(tenantId, stockItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));
        return movementRepository.findAllByStockItemId(tenantId, stockItemId, pageable)
                .map(StockMovementResponse::from);
    }
}
