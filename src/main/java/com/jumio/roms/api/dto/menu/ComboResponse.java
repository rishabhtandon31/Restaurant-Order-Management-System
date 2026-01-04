package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.MenuType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ComboResponse {
    private UUID id;
    private UUID branchId;
    private MenuType menuType;
    private String name;
    private String description;
    private BigDecimal discountedPrice;
    private boolean available;
    private List<ComboItem> items;

    public static class ComboItem {
        private UUID menuItemId;
        private String menuItemName;
        private int quantity;

        public UUID getMenuItemId() { return menuItemId; }
        public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }

        public String getMenuItemName() { return menuItemName; }
        public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }

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

    public List<ComboItem> getItems() { return items; }
    public void setItems(List<ComboItem> items) { this.items = items; }
}
