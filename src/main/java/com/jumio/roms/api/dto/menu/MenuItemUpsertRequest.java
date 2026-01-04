package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.DietType;
import com.jumio.roms.domain.enums.ItemCategory;
import com.jumio.roms.domain.enums.MenuType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuItemUpsertRequest {

    private UUID id; // optional for bulk upsert

    @NotNull(message = "menuType is required")
    private MenuType menuType;

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be > 0")
    private BigDecimal price;

    @Min(value = 1, message = "prepTimeMinutes must be >= 1")
    private int prepTimeMinutes;

    @NotNull(message = "category is required")
    private ItemCategory category;

    @NotNull(message = "dietType is required")
    private DietType dietType;

    private boolean available = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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
