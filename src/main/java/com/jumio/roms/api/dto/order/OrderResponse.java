package com.jumio.roms.api.dto.order;

import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.LineItemStatus;
import com.jumio.roms.domain.enums.OrderStatus;
import com.jumio.roms.domain.enums.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public LineItemKind getKind() { return kind; }
        public void setKind(LineItemKind kind) { this.kind = kind; }

        public UUID getRefId() { return refId; }
        public void setRefId(UUID refId) { this.refId = refId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getLineSubtotal() { return lineSubtotal; }
        public void setLineSubtotal(BigDecimal lineSubtotal) { this.lineSubtotal = lineSubtotal; }

        public BigDecimal getLineDiscount() { return lineDiscount; }
        public void setLineDiscount(BigDecimal lineDiscount) { this.lineDiscount = lineDiscount; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }

        public LineItemStatus getStatus() { return status; }
        public void setStatus(LineItemStatus status) { this.status = status; }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public List<LineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<LineItem> lineItems) { this.lineItems = lineItems; }
}
