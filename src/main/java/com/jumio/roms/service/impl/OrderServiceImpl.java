package com.jumio.roms.service.impl;

import com.jumio.roms.api.dto.order.BillResponse;
import com.jumio.roms.api.dto.order.OrderCreateRequest;
import com.jumio.roms.api.dto.order.OrderResponse;
import com.jumio.roms.config.AppProperties;
import com.jumio.roms.domain.entity.*;
import com.jumio.roms.domain.enums.*;
import com.jumio.roms.exception.BusinessRuleException;
import com.jumio.roms.exception.NotFoundException;
import com.jumio.roms.repo.OrderLineItemRepository;
import com.jumio.roms.repo.OrderRepository;
import com.jumio.roms.repo.PaymentRepository;
import com.jumio.roms.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepo;
    private final OrderLineItemRepository lineRepo;
    private final PaymentRepository paymentRepo;
    private final BranchService branchService;
    private final MenuService menuService;
    private final TimeSlotService timeSlotService;
    private final OrderStateMachine stateMachine;
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private final AppProperties props;

    public OrderServiceImpl(OrderRepository orderRepo,
                            OrderLineItemRepository lineRepo,
                            PaymentRepository paymentRepo,
                            BranchService branchService,
                            MenuService menuService,
                            TimeSlotService timeSlotService,
                            OrderStateMachine stateMachine,
                            PricingService pricingService,
                            PaymentService paymentService,
                            AppProperties props) {
        this.orderRepo = orderRepo;
        this.lineRepo = lineRepo;
        this.paymentRepo = paymentRepo;
        this.branchService = branchService;
        this.menuService = menuService;
        this.timeSlotService = timeSlotService;
        this.stateMachine = stateMachine;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
        this.props = props;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(UUID branchId, OrderCreateRequest req) {
        Branch branch = branchService.getBranchOrThrow(branchId);

        Instant orderTime = (req.getOrderTime() != null) ? req.getOrderTime() : Instant.now();
        MenuType allowedMenuType = timeSlotService.menuTypeAt(orderTime);

        OrderEntity order = new OrderEntity();
        order.setBranch(branch);
        order.setCustomerName(req.getCustomerName());
        order.setCustomerPhone(req.getCustomerPhone());
        order.setCustomerAddress(req.getCustomerAddress());
        order.setOrderType(req.getOrderType());
        order.setStatus(OrderStatus.CREATED);

        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {
            OrderLineItem li = new OrderLineItem();
            li.setKind(itemReq.getKind());
            li.setQuantity(itemReq.getQuantity());
            li.setInstructions(itemReq.getInstructions());
            li.setStatus(LineItemStatus.ACTIVE);

            if (itemReq.getKind() == LineItemKind.ITEM) {
                MenuItem mi = menuService.getMenuItemOrThrow(itemReq.getRefId());
                validateMenuForOrder(mi.getBranch().getId(), branchId, mi.getMenuType(), allowedMenuType, mi.isAvailable(), "menu item", mi.getId());
                li.setMenuItem(mi);
                li.setUnitPrice(mi.getPrice());
                li.setLineSubtotal(BigDecimal.ZERO);
                li.setLineDiscount(BigDecimal.ZERO);
            } else {
                Combo combo = menuService.getComboOrThrow(itemReq.getRefId());
                validateMenuForOrder(combo.getBranch().getId(), branchId, combo.getMenuType(), allowedMenuType, combo.isAvailable(), "combo", combo.getId());
                li.setCombo(combo);
                li.setUnitPrice(combo.getDiscountedPrice());
                li.setLineSubtotal(BigDecimal.ZERO);
                li.setLineDiscount(BigDecimal.ZERO);
            }
            order.addLineItem(li);
        }

        pricingService.priceOrder(order);
        OrderEntity saved = orderRepo.save(order);
        log.info("Created order {} for branch {}", saved.getId(), branchId);
        return toResponse(saved);
    }

    private void validateMenuForOrder(UUID entityBranchId, UUID orderBranchId, MenuType entityMenuType, MenuType allowedMenuType,
                                      boolean available, String kind, UUID refId) {
        if (!entityBranchId.equals(orderBranchId)) {
            throw new BusinessRuleException(kind + " belongs to a different branch: " + refId);
        }
        if (entityMenuType != allowedMenuType) {
            throw new BusinessRuleException(kind + " is not available for this time slot. Required=" + allowedMenuType + ", got=" + entityMenuType);
        }
        if (!available) {
            throw new BusinessRuleException(kind + " is not available currently: " + refId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity getOrderWithLinesOrThrow(UUID orderId) {
        return orderRepo.findWithLineItemsById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        return toResponse(getOrderWithLinesOrThrow(orderId));
    }

    @Override
    @Transactional
    public OrderResponse transition(UUID orderId, OrderStatus target) {
        OrderEntity order = getOrderWithLinesOrThrow(orderId);
        stateMachine.validateTransition(order.getStatus(), target);
        order.setStatus(target);
        OrderEntity saved = orderRepo.save(order);
        log.info("Order {} transitioned {} -> {}", orderId, order.getStatus(), target);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        OrderEntity order = getOrderWithLinesOrThrow(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Delivered orders cannot be cancelled");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return toResponse(order);
        }

        BigDecimal oldGrand = order.getGrandTotal();

        order.setStatus(OrderStatus.CANCELLED);
        for (OrderLineItem li : order.getLineItems()) {
            li.setStatus(LineItemStatus.CANCELLED);
        }
        pricingService.priceOrder(order);

        OrderEntity saved = orderRepo.save(order);

        BigDecimal newGrand = saved.getGrandTotal();
        BigDecimal refundAmount = oldGrand.subtract(newGrand); // usually full oldGrand
        if (refundAmount.signum() > 0) {
            paymentService.autoRefundIfPaid(orderId, refundAmount, "Order cancelled", null);
        }
        log.info("Cancelled order {}. Refund={}", orderId, refundAmount);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse cancelLineItem(UUID orderId, UUID lineItemId) {
        OrderEntity order = getOrderWithLinesOrThrow(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED) throw new BusinessRuleException("Delivered orders cannot be modified");
        if (order.getStatus() == OrderStatus.CANCELLED) throw new BusinessRuleException("Cancelled order cannot be modified");

        OrderLineItem li = order.getLineItems().stream()
                .filter(x -> x.getId().equals(lineItemId))
                .findFirst().orElseThrow(() -> new NotFoundException("Line item not found in order: " + lineItemId));

        if (li.getStatus() == LineItemStatus.CANCELLED) return toResponse(order);

        BigDecimal oldGrand = order.getGrandTotal();

        li.setStatus(LineItemStatus.CANCELLED);
        pricingService.priceOrder(order);

        // If all items cancelled, set order status to CANCELLED
        boolean allCancelled = order.getLineItems().stream().allMatch(x -> x.getStatus() == LineItemStatus.CANCELLED);
        if (allCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
        }

        OrderEntity saved = orderRepo.save(order);

        BigDecimal newGrand = saved.getGrandTotal();
        BigDecimal refundAmount = oldGrand.subtract(newGrand);
        if (refundAmount.signum() > 0) {
            paymentService.autoRefundIfPaid(orderId, refundAmount, "Line item cancelled", li.getId());
        }
        log.info("Cancelled line item {} from order {}. Refund={}", lineItemId, orderId, refundAmount);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBill(UUID orderId) {
        OrderEntity order = getOrderWithLinesOrThrow(orderId);
        BillResponse bill = new BillResponse();
        bill.setOrderId(order.getId());
        bill.setSubtotal(order.getSubtotal());
        bill.setDiscount(order.getDiscount());
        bill.setTaxableAmount(order.getTaxableAmount());
        bill.setServiceTaxRate(props.getPricing().getServiceTaxRate());
        bill.setTax(order.getTax());
        bill.setDeliveryCharge(order.getDeliveryCharge());
        bill.setGrandTotal(order.getGrandTotal());

        List<BillResponse.Line> lines = new ArrayList<>();
        for (OrderLineItem li : order.getLineItems()) {
            if (li.getStatus() == LineItemStatus.CANCELLED) continue;
            BillResponse.Line l = new BillResponse.Line();
            l.setLineItemId(li.getId());
            String name = (li.getKind() == LineItemKind.ITEM) ? li.getMenuItem().getName() : li.getCombo().getName();
            l.setName(name);
            l.setQuantity(li.getQuantity());
            l.setUnitPrice(li.getUnitPrice());
            l.setLineSubtotal(li.getLineSubtotal());
            l.setLineDiscount(li.getLineDiscount());
            l.setNetAmount(li.getLineSubtotal().subtract(li.getLineDiscount()));
            lines.add(l);
        }
        bill.setLines(lines);
        return bill;
    }

    private OrderResponse toResponse(OrderEntity order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setBranchId(order.getBranch().getId());
        r.setCustomerName(order.getCustomerName());
        r.setCustomerPhone(order.getCustomerPhone());
        r.setCustomerAddress(order.getCustomerAddress());
        r.setOrderType(order.getOrderType());
        r.setStatus(order.getStatus());
        r.setCreatedAt(order.getCreatedAt());
        r.setUpdatedAt(order.getUpdatedAt());

        r.setSubtotal(order.getSubtotal());
        r.setDiscount(order.getDiscount());
        r.setTaxableAmount(order.getTaxableAmount());
        r.setTax(order.getTax());
        r.setDeliveryCharge(order.getDeliveryCharge());
        r.setGrandTotal(order.getGrandTotal());

        List<OrderResponse.LineItem> lineItems = order.getLineItems().stream().map(li -> {
            OrderResponse.LineItem x = new OrderResponse.LineItem();
            x.setId(li.getId());
            x.setKind(li.getKind());
            if (li.getKind() == LineItemKind.ITEM) {
                x.setRefId(li.getMenuItem().getId());
                x.setName(li.getMenuItem().getName());
            } else {
                x.setRefId(li.getCombo().getId());
                x.setName(li.getCombo().getName());
            }
            x.setQuantity(li.getQuantity());
            x.setUnitPrice(li.getUnitPrice());
            x.setLineSubtotal(li.getLineSubtotal());
            x.setLineDiscount(li.getLineDiscount());
            x.setInstructions(li.getInstructions());
            x.setStatus(li.getStatus());
            return x;
        }).collect(Collectors.toList());
        r.setLineItems(lineItems);

        return r;
    }
}

