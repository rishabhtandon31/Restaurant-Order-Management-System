package com.jumio.roms;

import com.jumio.roms.domain.enums.OrderStatus;
import com.jumio.roms.exception.BusinessRuleException;
import com.jumio.roms.service.OrderStateMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStateMachineTest {

    @Test
    void allowsHappyPathTransitions() {
        OrderStateMachine sm = new OrderStateMachine();
        assertDoesNotThrow(() -> sm.validateTransition(OrderStatus.CREATED, OrderStatus.ACCEPTED));
        assertDoesNotThrow(() -> sm.validateTransition(OrderStatus.ACCEPTED, OrderStatus.PREPARING));
        assertDoesNotThrow(() -> sm.validateTransition(OrderStatus.PREPARING, OrderStatus.READY));
        assertDoesNotThrow(() -> sm.validateTransition(OrderStatus.READY, OrderStatus.DELIVERED));
    }

    @Test
    void rejectsInvalidTransition() {
        OrderStateMachine sm = new OrderStateMachine();
        assertThrows(BusinessRuleException.class, () -> sm.validateTransition(OrderStatus.CREATED, OrderStatus.DELIVERED));
        assertThrows(BusinessRuleException.class, () -> sm.validateTransition(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
    }
}
