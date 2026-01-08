package com.jumio.roms.domain.entity;

import com.jumio.roms.domain.enums.LineItemKind;
import com.jumio.roms.domain.enums.LineItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_line_items", indexes = {
        @Index(name = "idx_order_line_items_order", columnList = "order_id")
})
@Getter
@Setter
@NoArgsConstructor
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
}
