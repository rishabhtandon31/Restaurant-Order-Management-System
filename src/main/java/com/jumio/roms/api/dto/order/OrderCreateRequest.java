package com.jumio.roms.api.dto.order;

import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
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

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "kind is required")
        private LineItemKind kind;

        // menuItemId or comboId based on kind
        @NotNull(message = "refId is required")
        private UUID refId;

        @Min(value = 1, message = "quantity must be >= 1")
        private int quantity = 1;

        private String instructions;
    }
}
