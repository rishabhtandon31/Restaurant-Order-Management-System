package com.jumio.roms.repo;

import com.jumio.roms.domain.entity.Combo;
import com.jumio.roms.domain.enums.MenuType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComboRepository extends JpaRepository<Combo, UUID> {

    @EntityGraph(attributePaths = {"items", "items.menuItem"})
    Optional<Combo> findWithItemsById(UUID id);

    @EntityGraph(attributePaths = {"items", "items.menuItem"})
    List<Combo> findByBranch_IdAndMenuTypeAndAvailable(UUID branchId, MenuType menuType, boolean available);

    @EntityGraph(attributePaths = {"items", "items.menuItem"})
    List<Combo> findByBranch_IdAndMenuType(UUID branchId, MenuType menuType);
}
