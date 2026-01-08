package com.jumio.roms.service;

import com.jumio.roms.domain.entity.OrderEntity;

import java.math.BigDecimal;

public interface PricingService {
    void priceOrder(OrderEntity order);
    BigDecimal money(BigDecimal x);
}
