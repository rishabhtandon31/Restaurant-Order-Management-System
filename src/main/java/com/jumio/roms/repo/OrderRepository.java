package com.jumio.roms.repo;

import com.jumio.roms.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @EntityGraph(attributePaths = {"lineItems", "lineItems.menuItem", "lineItems.combo"})
    Optional<OrderEntity> findWithLineItemsById(UUID id);
}
