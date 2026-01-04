package com.jumio.roms.api.dto.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    public static class Line {
        private UUID lineItemId;
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineSubtotal;
        private BigDecimal lineDiscount;
        private BigDecimal netAmount;

        public UUID getLineItemId() { return lineItemId; }
        public void setLineItemId(UUID lineItemId) { this.lineItemId = lineItemId; }

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

        public BigDecimal getNetAmount() { return netAmount; }
        public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public List<Line> getLines() { return lines; }
    public void setLines(List<Line> lines) { this.lines = lines; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }

    public BigDecimal getServiceTaxRate() { return serviceTaxRate; }
    public void setServiceTaxRate(BigDecimal serviceTaxRate) { this.serviceTaxRate = serviceTaxRate; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
}
