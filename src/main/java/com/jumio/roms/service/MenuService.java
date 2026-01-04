package com.jumio.roms.service;

import com.jumio.roms.api.dto.menu.*;
import com.jumio.roms.domain.entity.*;
import com.jumio.roms.domain.enums.MenuType;
import com.jumio.roms.exception.BusinessRuleException;
import com.jumio.roms.exception.NotFoundException;
import com.jumio.roms.repo.BranchRepository;
import com.jumio.roms.repo.ComboRepository;
import com.jumio.roms.repo.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final BranchRepository branchRepo;
    private final MenuItemRepository menuItemRepo;
    private final ComboRepository comboRepo;
    private final TimeSlotService timeSlotService;

    public MenuService(BranchRepository branchRepo,
                       MenuItemRepository menuItemRepo,
                       ComboRepository comboRepo,
                       TimeSlotService timeSlotService) {
        this.branchRepo = branchRepo;
        this.menuItemRepo = menuItemRepo;
        this.comboRepo = comboRepo;
        this.timeSlotService = timeSlotService;
    }

    public Branch getBranchOrThrow(UUID branchId) {
        return branchRepo.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found: " + branchId));
    }

    public MenuItem getMenuItemOrThrow(UUID id) {
        return menuItemRepo.findById(id).orElseThrow(() -> new NotFoundException("Menu item not found: " + id));
    }

    public Combo getComboOrThrow(UUID id) {
        return comboRepo.findWithItemsById(id).orElseThrow(() -> new NotFoundException("Combo not found: " + id));
    }

    @Transactional
    public MenuItemResponse createMenuItem(UUID branchId, MenuItemUpsertRequest req) {
        Branch branch = getBranchOrThrow(branchId);
        MenuItem mi = new MenuItem();
        mi.setBranch(branch);
        applyMenuItem(mi, req);
        MenuItem saved = menuItemRepo.save(mi);
        log.info("Created menu item {} for branch {}", saved.getId(), branchId);
        return toResponse(saved);
    }

    @Transactional
    public MenuItemResponse updateMenuItem(UUID itemId, MenuItemUpsertRequest req) {
        MenuItem mi = getMenuItemOrThrow(itemId);
        applyMenuItem(mi, req);
        MenuItem saved = menuItemRepo.save(mi);
        log.info("Updated menu item {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public MenuItemResponse updateAvailability(UUID itemId, boolean available) {
        MenuItem mi = getMenuItemOrThrow(itemId);
        mi.setAvailable(available);
        MenuItem saved = menuItemRepo.save(mi);
        log.info("Set menu item {} availability={}", saved.getId(), available);
        return toResponse(saved);
    }

    @Transactional
    public List<MenuItemResponse> bulkUpsert(UUID branchId, List<MenuItemUpsertRequest> reqs) {
        Branch branch = getBranchOrThrow(branchId);

        List<MenuItem> result = new ArrayList<>();
        for (MenuItemUpsertRequest req : reqs) {
            MenuItem mi;
            if (req.getId() != null) {
                mi = menuItemRepo.findById(req.getId())
                        .orElseThrow(() -> new NotFoundException("Menu item not found: " + req.getId()));
                if (!mi.getBranch().getId().equals(branchId)) {
                    throw new BusinessRuleException("Menu item does not belong to branch: " + req.getId());
                }
            } else {
                mi = new MenuItem();
                mi.setBranch(branch);
            }
            applyMenuItem(mi, req);
            result.add(menuItemRepo.save(mi));
        }
        log.info("Bulk upserted {} menu items for branch {}", result.size(), branchId);
        return result.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ComboResponse createCombo(UUID branchId, ComboCreateRequest req) {
        Branch branch = getBranchOrThrow(branchId);

        Combo combo = new Combo();
        combo.setBranch(branch);
        combo.setMenuType(req.getMenuType());
        combo.setName(req.getName());
        combo.setDescription(req.getDescription());
        combo.setDiscountedPrice(req.getDiscountedPrice());
        combo.setAvailable(req.isAvailable());

        // Build items
        combo.clearItems();
        for (ComboCreateRequest.ComboItemRequest itemReq : req.getItems()) {
            MenuItem mi = getMenuItemOrThrow(itemReq.getMenuItemId());
            if (!mi.getBranch().getId().equals(branchId)) {
                throw new BusinessRuleException("Combo item belongs to different branch: " + mi.getId());
            }
            if (mi.getMenuType() != req.getMenuType()) {
                throw new BusinessRuleException("Combo item menuType mismatch. Item=" + mi.getMenuType() + ", combo=" + req.getMenuType());
            }
            ComboItem ci = new ComboItem();
            ci.setMenuItem(mi);
            ci.setQuantity(itemReq.getQuantity());
            combo.addItem(ci);
        }

        Combo saved = comboRepo.save(combo);
        log.info("Created combo {} for branch {}", saved.getId(), branchId);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ActiveMenuResponse getActiveMenu(UUID branchId, Instant at) {
        Branch branch = getBranchOrThrow(branchId);
        Instant instant = (at != null) ? at : Instant.now();
        MenuType type = timeSlotService.menuTypeAt(instant);

        List<MenuItemResponse> items = menuItemRepo.findByBranch_IdAndMenuTypeAndAvailable(branch.getId(), type, true)
                .stream().map(this::toResponse).collect(Collectors.toList());

        List<ComboResponse> combos = comboRepo.findByBranch_IdAndMenuTypeAndAvailable(branch.getId(), type, true)
                .stream().map(this::toResponse).collect(Collectors.toList());

        ActiveMenuResponse resp = new ActiveMenuResponse();
        resp.setMenuType(type);
        resp.setItems(items);
        resp.setCombos(combos);
        return resp;
    }

    private void applyMenuItem(MenuItem mi, MenuItemUpsertRequest req) {
        mi.setMenuType(req.getMenuType());
        mi.setName(req.getName());
        mi.setDescription(req.getDescription());
        mi.setPrice(req.getPrice());
        mi.setPrepTimeMinutes(req.getPrepTimeMinutes());
        mi.setCategory(req.getCategory());
        mi.setDietType(req.getDietType());
        mi.setAvailable(req.isAvailable());
    }

    private MenuItemResponse toResponse(MenuItem mi) {
        MenuItemResponse r = new MenuItemResponse();
        r.setId(mi.getId());
        r.setBranchId(mi.getBranch().getId());
        r.setMenuType(mi.getMenuType());
        r.setName(mi.getName());
        r.setDescription(mi.getDescription());
        r.setPrice(mi.getPrice());
        r.setPrepTimeMinutes(mi.getPrepTimeMinutes());
        r.setCategory(mi.getCategory());
        r.setDietType(mi.getDietType());
        r.setAvailable(mi.isAvailable());
        return r;
    }

    private ComboResponse toResponse(Combo combo) {
        ComboResponse r = new ComboResponse();
        r.setId(combo.getId());
        r.setBranchId(combo.getBranch().getId());
        r.setMenuType(combo.getMenuType());
        r.setName(combo.getName());
        r.setDescription(combo.getDescription());
        r.setDiscountedPrice(combo.getDiscountedPrice());
        r.setAvailable(combo.isAvailable());

        List<ComboResponse.ComboItem> items = new ArrayList<>();
        for (ComboItem ci : combo.getItems()) {
            ComboResponse.ComboItem i = new ComboResponse.ComboItem();
            i.setMenuItemId(ci.getMenuItem().getId());
            i.setMenuItemName(ci.getMenuItem().getName());
            i.setQuantity(ci.getQuantity());
            items.add(i);
        }
        r.setItems(items);
        return r;
    }
}
