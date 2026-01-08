package com.jumio.roms.api.dto.order;

import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.LineItemStatus;
import com.jumio.roms.domain.enums.OrderStatus;
import com.jumio.roms.domain.enums.OrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private UUID branchId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private OrderType orderType;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal taxableAmount;
    private BigDecimal tax;
    private BigDecimal deliveryCharge;
    private BigDecimal grandTotal;

    private List<LineItem> lineItems;

    @Data
    public static class LineItem {
        private UUID id;
        private LineItemKind kind;
        private UUID refId;
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineSubtotal;
        private BigDecimal lineDiscount;
        private String instructions;
        private LineItemStatus status;
    }
}
