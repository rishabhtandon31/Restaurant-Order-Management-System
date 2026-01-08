package com.jumio.roms.api.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class BillResponse {
    private UUID orderId;
    private List<Line> lines;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal taxableAmount;
    private BigDecimal serviceTaxRate;
    private BigDecimal tax;
    private BigDecimal deliveryCharge;
    private BigDecimal grandTotal;

    @Data
    public static class Line {
        private UUID lineItemId;
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineSubtotal;
        private BigDecimal lineDiscount;
        private BigDecimal netAmount;
    }
}
