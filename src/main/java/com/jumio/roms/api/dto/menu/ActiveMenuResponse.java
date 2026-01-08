package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.MenuType;
import lombok.Data;

import java.util.List;

@Data
public class ActiveMenuResponse {
    private MenuType menuType;
    private List<MenuItemResponse> items;
    private List<ComboResponse> combos;
}
