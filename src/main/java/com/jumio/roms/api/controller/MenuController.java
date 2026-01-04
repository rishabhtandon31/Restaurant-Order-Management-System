package com.jumio.roms.api.controller;

import com.jumio.roms.api.dto.menu.*;
import com.jumio.roms.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // Menu Items
    @PostMapping("/branches/{branchId}/menu-items")
    public MenuItemResponse createMenuItem(@PathVariable UUID branchId, @Valid @RequestBody MenuItemUpsertRequest req) {
        return menuService.createMenuItem(branchId, req);
    }

    @PutMapping("/menu-items/{itemId}")
    public MenuItemResponse updateMenuItem(@PathVariable UUID itemId, @Valid @RequestBody MenuItemUpsertRequest req) {
        return menuService.updateMenuItem(itemId, req);
    }

    @PatchMapping("/menu-items/{itemId}/availability")
    public MenuItemResponse updateItemAvailability(@PathVariable UUID itemId, @RequestBody AvailabilityUpdateRequest req) {
        return menuService.updateAvailability(itemId, req.isAvailable());
    }

    @PostMapping("/branches/{branchId}/menu-items/bulk")
    public List<MenuItemResponse> bulkUpsert(@PathVariable UUID branchId, @Valid @RequestBody List<MenuItemUpsertRequest> reqs) {
        return menuService.bulkUpsert(branchId, reqs);
    }

    // Combos
    @PostMapping("/branches/{branchId}/combos")
    public ComboResponse createCombo(@PathVariable UUID branchId, @Valid @RequestBody ComboCreateRequest req) {
        return menuService.createCombo(branchId, req);
    }

    // Active menu snapshot
    @GetMapping("/branches/{branchId}/menu")
    public ActiveMenuResponse getActiveMenu(@PathVariable UUID branchId,
                                           @RequestParam(name = "at", required = false) Instant at) {
        return menuService.getActiveMenu(branchId, at);
    }
}
