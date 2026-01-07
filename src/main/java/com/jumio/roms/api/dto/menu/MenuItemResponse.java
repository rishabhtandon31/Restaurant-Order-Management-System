package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.DietType;
import com.jumio.roms.domain.enums.ItemCategory;
import com.jumio.roms.domain.enums.MenuType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
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
}
