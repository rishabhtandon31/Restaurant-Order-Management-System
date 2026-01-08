package com.jumio.roms.domain.entity;

import com.jumio.roms.domain.enums.DietType;
import com.jumio.roms.domain.enums.ItemCategory;
import com.jumio.roms.domain.enums.MenuType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "menu_items", indexes = {
        @Index(name = "idx_menu_items_branch_menu", columnList = "branch_id, menuType"),
        @Index(name = "idx_menu_items_avail", columnList = "available")
})
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuType menuType;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int prepTimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietType dietType;

    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
