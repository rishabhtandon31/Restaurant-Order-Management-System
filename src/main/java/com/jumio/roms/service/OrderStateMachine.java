package com.jumio.roms.service;

import com.jumio.roms.domain.enums.OrderStatus;
import com.jumio.roms.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class OrderStateMachine {

    private final Map<OrderStatus, Set<OrderStatus>> allowed = Map.of(
            OrderStatus.CREATED, EnumSet.of(OrderStatus.ACCEPTED, OrderStatus.CANCELLED),
            OrderStatus.ACCEPTED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, EnumSet.of(OrderStatus.READY, OrderStatus.CANCELLED),
            OrderStatus.READY, EnumSet.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class)
    );

    public void validateTransition(OrderStatus from, OrderStatus to) {
        if (!allowed.getOrDefault(from, EnumSet.noneOf(OrderStatus.class)).contains(to)) {
            throw new BusinessRuleException("Invalid transition: " + from + " -> " + to);
        }
    }
}
