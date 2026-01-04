package com.jumio.roms.repo;

import com.jumio.roms.domain.entity.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, UUID> { }
