package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.DietType;
import com.jumio.roms.domain.enums.ItemCategory;
import com.jumio.roms.domain.enums.MenuType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
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
}
