package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.StockItemResponse;
import com.bizcore.inventory.domain.port.in.GetStockItemUseCase;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStockItemUseCaseImpl implements GetStockItemUseCase {

    private final StockItemRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public StockItemResponse get(UUID tenantId, UUID stockItemId) {
        return repository.findById(tenantId, stockItemId)
                .map(StockItemResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));
    }
}
