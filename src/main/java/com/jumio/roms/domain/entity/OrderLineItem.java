package com.jumio.roms.domain.entity;

import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.LineItemStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_line_items", indexes = {
        @Index(name = "idx_order_line_items_order", columnList = "order_id")
})
public class OrderLineItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineItemKind kind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_id")
    private Combo combo;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineSubtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineDiscount;

    @Column(length = 2000)
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineItemStatus status = LineItemStatus.ACTIVE;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public LineItemKind getKind() { return kind; }
    public void setKind(LineItemKind kind) { this.kind = kind; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public Combo getCombo() { return combo; }
    public void setCombo(Combo combo) { this.combo = combo; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineSubtotal() { return lineSubtotal; }
    public void setLineSubtotal(BigDecimal lineSubtotal) { this.lineSubtotal = lineSubtotal; }

    public BigDecimal getLineDiscount() { return lineDiscount; }
    public void setLineDiscount(BigDecimal lineDiscount) { this.lineDiscount = lineDiscount; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LineItemStatus getStatus() { return status; }
    public void setStatus(LineItemStatus status) { this.status = status; }
}
