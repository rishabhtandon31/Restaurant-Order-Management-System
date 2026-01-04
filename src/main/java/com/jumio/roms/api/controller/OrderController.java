package com.jumio.roms.api.controller;

import com.jumio.roms.api.dto.order.BillResponse;
import com.jumio.roms.api.dto.order.OrderCreateRequest;
import com.jumio.roms.api.dto.order.OrderResponse;
import com.jumio.roms.domain.enums.OrderStatus;
import com.jumio.roms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/branches/{branchId}/orders")
    public OrderResponse create(@PathVariable UUID branchId, @Valid @RequestBody OrderCreateRequest req) {
        return orderService.createOrder(branchId, req);
    }

    @GetMapping("/orders/{orderId}")
    public OrderResponse get(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/orders/{orderId}/bill")
    public BillResponse bill(@PathVariable UUID orderId) {
        return orderService.getBill(orderId);
    }

    @PostMapping("/orders/{orderId}/accept")
    public OrderResponse accept(@PathVariable UUID orderId) {
        return orderService.transition(orderId, OrderStatus.ACCEPTED);
    }

    @PostMapping("/orders/{orderId}/prepare")
    public OrderResponse preparing(@PathVariable UUID orderId) {
        return orderService.transition(orderId, OrderStatus.PREPARING);
    }

    @PostMapping("/orders/{orderId}/ready")
    public OrderResponse ready(@PathVariable UUID orderId) {
        return orderService.transition(orderId, OrderStatus.READY);
    }

    @PostMapping("/orders/{orderId}/deliver")
    public OrderResponse deliver(@PathVariable UUID orderId) {
        return orderService.transition(orderId, OrderStatus.DELIVERED);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public OrderResponse cancel(@PathVariable UUID orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PostMapping("/orders/{orderId}/items/{lineItemId}/cancel")
    public OrderResponse cancelItem(@PathVariable UUID orderId, @PathVariable UUID lineItemId) {
        return orderService.cancelLineItem(orderId, lineItemId);
    }
}
