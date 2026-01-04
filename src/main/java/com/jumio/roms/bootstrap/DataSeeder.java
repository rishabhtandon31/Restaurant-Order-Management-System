package com.jumio.roms.bootstrap;

import com.jumio.roms.domain.entity.Branch;
import com.jumio.roms.domain.entity.Combo;
import com.jumio.roms.domain.entity.ComboItem;
import com.jumio.roms.domain.entity.MenuItem;
import com.jumio.roms.domain.enums.DietType;
import com.jumio.roms.domain.enums.ItemCategory;
import com.jumio.roms.domain.enums.MenuType;
import com.jumio.roms.repo.BranchRepository;
import com.jumio.roms.repo.ComboRepository;
import com.jumio.roms.repo.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final BranchRepository branchRepo;
    private final MenuItemRepository menuItemRepo;
    private final ComboRepository comboRepo;

    public DataSeeder(BranchRepository branchRepo, MenuItemRepository menuItemRepo, ComboRepository comboRepo) {
        this.branchRepo = branchRepo;
        this.menuItemRepo = menuItemRepo;
        this.comboRepo = comboRepo;
    }

    @Override
    public void run(String... args) {
        if (branchRepo.count() > 0) return;

        Branch b = new Branch();
        b.setName("Jumio Diner");
        b.setAddress("Noida Sector 62");
        b = branchRepo.save(b);

        // Lunch items
        MenuItem paneer = new MenuItem();
        paneer.setBranch(b);
        paneer.setMenuType(MenuType.LUNCH);
        paneer.setName("Paneer Butter Masala");
        paneer.setDescription("Creamy paneer curry");
        paneer.setPrice(new BigDecimal("320.00"));
        paneer.setPrepTimeMinutes(15);
        paneer.setCategory(ItemCategory.MAIN_COURSE);
        paneer.setDietType(DietType.VEG);
        paneer.setAvailable(true);
        paneer = menuItemRepo.save(paneer);

        MenuItem naan = new MenuItem();
        naan.setBranch(b);
        naan.setMenuType(MenuType.LUNCH);
        naan.setName("Butter Naan");
        naan.setDescription("Soft naan with butter");
        naan.setPrice(new BigDecimal("60.00"));
        naan.setPrepTimeMinutes(5);
        naan.setCategory(ItemCategory.MAIN_COURSE);
        naan.setDietType(DietType.VEG);
        naan.setAvailable(true);
        naan = menuItemRepo.save(naan);

        MenuItem gulab = new MenuItem();
        gulab.setBranch(b);
        gulab.setMenuType(MenuType.LUNCH);
        gulab.setName("Gulab Jamun");
        gulab.setDescription("Sweet dessert");
        gulab.setPrice(new BigDecimal("90.00"));
        gulab.setPrepTimeMinutes(2);
        gulab.setCategory(ItemCategory.DESSERT);
        gulab.setDietType(DietType.VEG);
        gulab.setAvailable(true);
        gulab = menuItemRepo.save(gulab);

        // Combo: Paneer + 2 Naan (discounted)
        Combo combo = new Combo();
        combo.setBranch(b);
        combo.setMenuType(MenuType.LUNCH);
        combo.setName("Paneer Combo");
        combo.setDescription("Paneer Butter Masala + 2 Butter Naan");
        combo.setDiscountedPrice(new BigDecimal("410.00")); // original: 320 + 2*60 = 440
        combo.setAvailable(true);

        ComboItem c1 = new ComboItem();
        c1.setMenuItem(paneer);
        c1.setQuantity(1);
        combo.addItem(c1);

        ComboItem c2 = new ComboItem();
        c2.setMenuItem(naan);
        c2.setQuantity(2);
        combo.addItem(c2);

        comboRepo.save(combo);

        log.info("Seeded demo data. BranchId={}", b.getId());
    }
}
