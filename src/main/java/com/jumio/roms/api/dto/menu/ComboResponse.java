package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.MenuType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ComboResponse {
    private UUID id;
    private UUID branchId;
    private MenuType menuType;
    private String name;
    private String description;
    private BigDecimal discountedPrice;
    private boolean available;
    private List<ComboItem> items;

    @Data
    public static class ComboItem {
        private UUID menuItemId;
        private String menuItemName;
        private int quantity;
    }
}
