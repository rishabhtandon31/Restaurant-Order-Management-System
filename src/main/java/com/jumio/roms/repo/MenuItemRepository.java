package com.jumio.roms.repo;

import com.jumio.roms.domain.entity.MenuItem;
import com.jumio.roms.domain.enums.MenuType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByBranch_IdAndMenuTypeAndAvailable(UUID branchId, MenuType menuType, boolean available);
    List<MenuItem> findByBranch_IdAndMenuType(UUID branchId, MenuType menuType);
}
