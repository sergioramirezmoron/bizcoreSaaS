package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.BusinessTypeResponse;
import com.bizcore.company.domain.port.in.ListBusinessTypesUseCase;
import com.bizcore.company.domain.port.out.BusinessTypeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListBusinessTypesUseCaseImpl implements ListBusinessTypesUseCase {

    private final BusinessTypeRepositoryPort businessTypeRepo;

    @Override
    public List<BusinessTypeResponse> listAll() {
        return businessTypeRepo.findAllActive().stream()
                .map(bt -> new BusinessTypeResponse(bt.id(), bt.code(), bt.name(), bt.description(), bt.icon()))
                .toList();
    }
}
