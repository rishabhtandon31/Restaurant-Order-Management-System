package com.jumio.roms.service.impl;

import com.jumio.roms.config.AppProperties;
import com.jumio.roms.domain.entity.Combo;
import com.jumio.roms.domain.entity.ComboItem;
import com.jumio.roms.domain.entity.OrderEntity;
import com.jumio.roms.domain.entity.OrderLineItem;
import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.LineItemStatus;
import com.jumio.roms.domain.enums.OrderType;
import com.jumio.roms.exception.BusinessRuleException;
import com.jumio.roms.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingServiceImpl implements PricingService {

    private final AppProperties props;

    public PricingServiceImpl(AppProperties props) {
        this.props = props;
    }

    @Override
    public BigDecimal money(BigDecimal x) {
        if (x == null) return BigDecimal.ZERO;
        return x.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Pricing model:
     * - Subtotal is computed on ORIGINAL prices.
     * - Discounts are applied (combos).
     * - Service tax (18% by default) on (subtotal - discount).
     * - Delivery charge added for DELIVERY orders (configurable).
     */
    @Override
    public void priceOrder(OrderEntity order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        for (OrderLineItem li : order.getLineItems()) {
            if (li.getStatus() == LineItemStatus.CANCELLED) {
                li.setUnitPrice(money(BigDecimal.ZERO));
                li.setLineSubtotal(money(BigDecimal.ZERO));
                li.setLineDiscount(money(BigDecimal.ZERO));
                continue;
            }

            if (li.getKind() == LineItemKind.ITEM) {
                if (li.getMenuItem() == null) throw new BusinessRuleException("Line item missing menuItem");
                BigDecimal unitPrice = li.getMenuItem().getPrice();
                li.setUnitPrice(money(unitPrice));

                BigDecimal lineSubtotal = money(unitPrice.multiply(BigDecimal.valueOf(li.getQuantity())));
                li.setLineSubtotal(lineSubtotal);
                li.setLineDiscount(money(BigDecimal.ZERO));

                subtotal = subtotal.add(lineSubtotal);

            } else { // COMBO
                if (li.getCombo() == null) throw new BusinessRuleException("Line item missing combo");
                Combo combo = li.getCombo();

                // Original sum of items (per combo unit)
                BigDecimal sumOfItems = BigDecimal.ZERO;
                for (ComboItem ci : combo.getItems()) {
                    sumOfItems = sumOfItems.add(ci.getMenuItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
                }
                sumOfItems = money(sumOfItems);

                // Discount per unit = sumOfItems - discountedPrice
                BigDecimal perUnitDiscount = sumOfItems.subtract(combo.getDiscountedPrice());
                if (perUnitDiscount.signum() < 0) perUnitDiscount = BigDecimal.ZERO;
                perUnitDiscount = money(perUnitDiscount);

                BigDecimal qty = BigDecimal.valueOf(li.getQuantity());

                // Store unitPrice as ORIGINAL unit price for transparency in bill
                li.setUnitPrice(sumOfItems);

                BigDecimal lineSubtotal = money(sumOfItems.multiply(qty));
                BigDecimal lineDiscount = money(perUnitDiscount.multiply(qty));

                li.setLineSubtotal(lineSubtotal);
                li.setLineDiscount(lineDiscount);

                subtotal = subtotal.add(lineSubtotal);
                discount = discount.add(lineDiscount);
            }
        }

        subtotal = money(subtotal);
        discount = money(discount);

        BigDecimal taxable = subtotal.subtract(discount);
        if (taxable.signum() < 0) taxable = BigDecimal.ZERO;
        taxable = money(taxable);

        BigDecimal tax = money(taxable.multiply(props.getPricing().getServiceTaxRate()));

        BigDecimal deliveryCharge = BigDecimal.ZERO;
        if (order.getOrderType() == OrderType.DELIVERY) {
            deliveryCharge = money(props.getPricing().getDeliveryCharge());
        }

        BigDecimal grand = money(taxable.add(tax).add(deliveryCharge));

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTaxableAmount(taxable);
        order.setTax(tax);
        order.setDeliveryCharge(deliveryCharge);
        order.setGrandTotal(grand);
    }
}

