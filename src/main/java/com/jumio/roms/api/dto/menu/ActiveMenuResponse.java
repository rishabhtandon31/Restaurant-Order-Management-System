package com.jumio.roms.api.dto.menu;

import com.jumio.roms.domain.enums.MenuType;

import java.util.List;

public class ActiveMenuResponse {
    private MenuType menuType;
    private List<MenuItemResponse> items;
    private List<ComboResponse> combos;

    public MenuType getMenuType() { return menuType; }
    public void setMenuType(MenuType menuType) { this.menuType = menuType; }

    public List<MenuItemResponse> getItems() { return items; }
    public void setItems(List<MenuItemResponse> items) { this.items = items; }

    public List<ComboResponse> getCombos() { return combos; }
    public void setCombos(List<ComboResponse> combos) { this.combos = combos; }
}
