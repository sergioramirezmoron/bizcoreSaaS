package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.StockItemResponse;
import com.bizcore.inventory.domain.port.in.ListLowStockUseCase;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListLowStockUseCaseImpl implements ListLowStockUseCase {

    private final StockItemRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public List<StockItemResponse> listLowStock(UUID tenantId, UUID branchId) {
        return repository.findLowStock(tenantId, branchId)
                .stream()
                .map(StockItemResponse::from)
                .toList();
    }
}
