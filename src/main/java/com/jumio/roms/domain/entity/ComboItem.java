package com.jumio.roms.domain.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "combo_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_combo_item", columnNames = {"combo_id", "menu_item_id"}))
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

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Combo getCombo() { return combo; }
    public void setCombo(Combo combo) { this.combo = combo; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
