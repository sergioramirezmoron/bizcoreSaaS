package com.bizcore.company.infrastructure.persistence;

import com.bizcore.company.domain.model.Branch;
import com.bizcore.company.domain.port.out.BranchRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchJpaRepository jpaRepository;

    @Override
    public Branch save(Branch branch) {
        return toDomain(jpaRepository.save(toEntity(branch)));
    }

    @Override
    public Optional<Branch> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public List<Branch> findAllByTenantId(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    @Override
    public int countByTenantId(UUID tenantId) {
        return jpaRepository.countByTenantId(tenantId);
    }

    @Override
    public void delete(Branch branch) {
        jpaRepository.deleteById(branch.id());
    }

    private Branch toDomain(BranchJpaEntity e) {
        return new Branch(e.getId(), e.getTenantId(), e.getName(), e.getPhone(), e.getAddress(), e.isMain(), e.getCreatedAt());
    }

    private BranchJpaEntity toEntity(Branch b) {
        return BranchJpaEntity.builder()
                .id(b.id())
                .tenantId(b.tenantId())
                .name(b.name())
                .phone(b.phone())
                .address(b.address())
                .main(b.main())
                .build();
    }
}
