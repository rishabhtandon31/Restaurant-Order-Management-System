package com.jumio.roms;

import com.jumio.roms.config.AppProperties;
import com.jumio.roms.domain.entity.*;
import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.OrderType;
import com.jumio.roms.service.PricingService;
import com.jumio.roms.service.impl.PricingServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTest {

    @Test
    void comboPricingUsesOriginalSubtotalAndDiscount() {
        AppProperties props = new AppProperties();
        props.getPricing().setServiceTaxRate(new BigDecimal("0.18"));
        props.getPricing().setDeliveryCharge(new BigDecimal("40.00"));

        PricingService pricing = new PricingServiceImpl(props);

        Branch b = new Branch();
        b.setName("B1");
        b.setAddress("Addr");

        MenuItem a = new MenuItem();
        a.setBranch(b);
        a.setName("A");
        a.setPrice(new BigDecimal("100.00"));

        MenuItem c = new MenuItem();
        c.setBranch(b);
        c.setName("C");
        c.setPrice(new BigDecimal("50.00"));

        Combo combo = new Combo();
        combo.setBranch(b);
        combo.setName("Combo");
        combo.setDiscountedPrice(new BigDecimal("120.00")); // original: 150

        ComboItem i1 = new ComboItem();
        i1.setMenuItem(a);
        i1.setQuantity(1);
        combo.addItem(i1);

        ComboItem i2 = new ComboItem();
        i2.setMenuItem(c);
        i2.setQuantity(1);
        combo.addItem(i2);

        OrderEntity order = new OrderEntity();
        order.setBranch(b);
        order.setOrderType(OrderType.PICKUP);

        OrderLineItem li = new OrderLineItem();
        li.setKind(LineItemKind.COMBO);
        li.setCombo(combo);
        li.setQuantity(2);
        order.addLineItem(li);

        pricing.priceOrder(order);

        // Subtotal = 150*2 = 300
        assertEquals(new BigDecimal("300.00"), order.getSubtotal());
        // Discount = (150-120)*2 = 60
        assertEquals(new BigDecimal("60.00"), order.getDiscount());
        // Taxable = 240
        assertEquals(new BigDecimal("240.00"), order.getTaxableAmount());
        // Tax = 43.20
        assertEquals(new BigDecimal("43.20"), order.getTax());
        // Grand = 283.20
        assertEquals(new BigDecimal("283.20"), order.getGrandTotal());
    }
}
