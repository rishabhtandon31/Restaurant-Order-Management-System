package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.DietType;
import com.jumio.roms.domain.enums.ItemCategory;
import com.jumio.roms.domain.enums.MenuType;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuItemResponse {
    private UUID id;
    private UUID branchId;
    private MenuType menuType;
    private String name;
    private String description;
    private BigDecimal price;
    private int prepTimeMinutes;
    private ItemCategory category;
    private DietType dietType;
    private boolean available;

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

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(int prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }

    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }

    public DietType getDietType() { return dietType; }
    public void setDietType(DietType dietType) { this.dietType = dietType; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
