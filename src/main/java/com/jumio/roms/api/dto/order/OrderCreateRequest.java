package com.jumio.roms.api.dto.order;

import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderCreateRequest {

    @NotBlank(message = "customerName is required")
    private String customerName;

    @NotBlank(message = "customerPhone is required")
    private String customerPhone;

    private String customerAddress;

    @NotNull(message = "orderType is required")
    private OrderType orderType;

    // Optional. If absent, server uses now.
    private Instant orderTime;

    @NotEmpty(message = "items is required")
    private List<OrderItemRequest> items;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }

    public Instant getOrderTime() { return orderTime; }
    public void setOrderTime(Instant orderTime) { this.orderTime = orderTime; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public static class OrderItemRequest {
        @NotNull(message = "kind is required")
        private LineItemKind kind;

        // menuItemId or comboId based on kind
        @NotNull(message = "refId is required")
        private UUID refId;

        @Min(value = 1, message = "quantity must be >= 1")
        private int quantity = 1;

        private String instructions;

        public LineItemKind getKind() { return kind; }
        public void setKind(LineItemKind kind) { this.kind = kind; }

        public UUID getRefId() { return refId; }
        public void setRefId(UUID refId) { this.refId = refId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }
}
