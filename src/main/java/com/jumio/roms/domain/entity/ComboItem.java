package com.jumio.roms.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "combo_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_combo_item", columnNames = {"combo_id", "menu_item_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ComboItem {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_id", nullable = false)
    private Combo combo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity = 1;
}
