package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.MenuType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    public MenuType getMenuType() { return menuType; }
    public void setMenuType(MenuType menuType) { this.menuType = menuType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public List<ComboItemRequest> getItems() { return items; }
    public void setItems(List<ComboItemRequest> items) { this.items = items; }

    public static class ComboItemRequest {
        @NotNull(message = "menuItemId is required")
        private UUID menuItemId;
        @Min(value = 1, message = "quantity must be >= 1")
        private int quantity = 1;

        public UUID getMenuItemId() { return menuItemId; }
        public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
