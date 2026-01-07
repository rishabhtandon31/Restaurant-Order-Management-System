package com.jumio.roms.service;

import com.jumio.roms.api.dto.order.BillResponse;
import com.jumio.roms.api.dto.order.OrderCreateRequest;
import com.jumio.roms.api.dto.order.OrderResponse;
import com.jumio.roms.domain.entity.OrderEntity;
import com.jumio.roms.domain.enums.OrderStatus;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID branchId, OrderCreateRequest req);
    OrderEntity getOrderWithLinesOrThrow(UUID orderId);
    OrderResponse getOrder(UUID orderId);
    OrderResponse transition(UUID orderId, OrderStatus target);
    OrderResponse cancelOrder(UUID orderId);
    OrderResponse cancelLineItem(UUID orderId, UUID lineItemId);
    BillResponse getBill(UUID orderId);
}
