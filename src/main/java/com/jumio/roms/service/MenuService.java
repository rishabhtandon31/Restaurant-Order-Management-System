package com.jumio.roms.service;

import com.jumio.roms.api.dto.menu.*;
import com.jumio.roms.domain.entity.Combo;
import com.jumio.roms.domain.entity.MenuItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MenuService {
    MenuItem getMenuItemOrThrow(UUID id);
    Combo getComboOrThrow(UUID id);
    MenuItemResponse createMenuItem(UUID branchId, MenuItemUpsertRequest req);
    MenuItemResponse updateMenuItem(UUID itemId, MenuItemUpsertRequest req);
    MenuItemResponse updateAvailability(UUID itemId, boolean available);
    List<MenuItemResponse> bulkUpsert(UUID branchId, List<MenuItemUpsertRequest> reqs);
    ComboResponse createCombo(UUID branchId, ComboCreateRequest req);
    ActiveMenuResponse getActiveMenu(UUID branchId, Instant at);
}
