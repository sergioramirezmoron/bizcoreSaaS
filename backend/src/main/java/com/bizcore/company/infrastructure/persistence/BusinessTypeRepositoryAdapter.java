package com.bizcore.company.infrastructure.persistence;

import com.bizcore.company.domain.model.BusinessType;
import com.bizcore.company.domain.port.out.BusinessTypeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BusinessTypeRepositoryAdapter implements BusinessTypeRepositoryPort {

    private final BusinessTypeJpaRepository jpaRepository;

    @Override
    public List<BusinessType> findAllActive() {
        return jpaRepository.findAllByActiveTrue().stream()
                .map(e -> new BusinessType(e.getId(), e.getCode(), e.getName(), e.getDescription(), e.getIcon()))
                .toList();
    }

    @Override
    public Optional<BusinessType> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> new BusinessType(e.getId(), e.getCode(), e.getName(), e.getDescription(), e.getIcon()));
    }
}
