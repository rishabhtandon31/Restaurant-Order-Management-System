package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.MenuType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ComboCreateRequest {
    @NotNull(message = "menuType is required")
    private MenuType menuType;

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "discountedPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "discountedPrice must be > 0")
    private BigDecimal discountedPrice;

    private boolean available = true;

    @NotEmpty(message = "items is required")
    private List<ComboItemRequest> items;

    @Data
    public static class ComboItemRequest {
        @NotNull(message = "menuItemId is required")
        private UUID menuItemId;
        
        @Min(value = 1, message = "quantity must be >= 1")
        private int quantity = 1;
    }
}
