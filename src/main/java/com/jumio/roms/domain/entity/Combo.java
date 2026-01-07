package com.jumio.roms.domain.entity;

import com.jumio.roms.domain.enums.MenuType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "combos", indexes = {
        @Index(name = "idx_combos_branch_menu", columnList = "branch_id, menuType"),
        @Index(name = "idx_combos_avail", columnList = "available")
})
@Getter
@Setter
@NoArgsConstructor
public class Combo {
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
    private BigDecimal discountedPrice;

    @Column(nullable = false)
    private boolean available = true;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComboItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addItem(ComboItem item) {
        items.add(item);
        item.setCombo(this);
    }

    public void clearItems() {
        items.clear();
    }
}
