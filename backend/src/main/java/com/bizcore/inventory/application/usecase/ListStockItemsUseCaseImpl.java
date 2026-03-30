package com.bizcore.inventory.application.usecase;

import com.bizcore.inventory.application.dto.StockItemResponse;
import com.bizcore.inventory.domain.port.in.ListStockItemsUseCase;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListStockItemsUseCaseImpl implements ListStockItemsUseCase {

    private final StockItemRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public Page<StockItemResponse> list(UUID tenantId, UUID productId, UUID variantId, UUID branchId, Pageable pageable) {
        return repository.findAll(tenantId, productId, variantId, branchId, pageable)
                .map(StockItemResponse::from);
    }
}
