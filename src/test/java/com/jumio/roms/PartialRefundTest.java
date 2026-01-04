package com.jumio.roms;

import com.jumio.roms.api.dto.menu.MenuItemUpsertRequest;
import com.jumio.roms.api.dto.order.OrderCreateRequest;
import com.jumio.roms.api.dto.payment.PaymentRequest;
import com.jumio.roms.domain.entity.Branch;
import com.jumio.roms.domain.enums.*;
import com.jumio.roms.repo.BranchRepository;
import com.jumio.roms.repo.RefundRepository;
import com.jumio.roms.service.MenuService;
import com.jumio.roms.service.OrderService;
import com.jumio.roms.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PartialRefundTest {

    @Autowired BranchRepository branchRepo;
    @Autowired MenuService menuService;
    @Autowired OrderService orderService;
    @Autowired PaymentService paymentService;
    @Autowired RefundRepository refundRepo;

    @Test
    void cancellingLineItemAfterPaymentCreatesRefund() {
        Branch b = new Branch();
        b.setName("B2");
        b.setAddress("Addr");
        b = branchRepo.save(b);

        // Breakfast time
        ZonedDateTime breakfast = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).withHour(8).withMinute(0).withSecond(0).withNano(0);

        UUID id1 = menuService.createMenuItem(b.getId(), item("Idli", new BigDecimal("40.00"), MenuType.BREAKFAST)).getId();
        UUID id2 = menuService.createMenuItem(b.getId(), item("Dosa", new BigDecimal("80.00"), MenuType.BREAKFAST)).getId();

        OrderCreateRequest req = new OrderCreateRequest();
        req.setCustomerName("C");
        req.setCustomerPhone("1");
        req.setCustomerAddress("A");
        req.setOrderType(OrderType.DELIVERY);
        req.setOrderTime(breakfast.toInstant());

        OrderCreateRequest.OrderItemRequest li1 = new OrderCreateRequest.OrderItemRequest();
        li1.setKind(LineItemKind.ITEM);
        li1.setRefId(id1);
        li1.setQuantity(1);

        OrderCreateRequest.OrderItemRequest li2 = new OrderCreateRequest.OrderItemRequest();
        li2.setKind(LineItemKind.ITEM);
        li2.setRefId(id2);
        li2.setQuantity(1);

        req.setItems(List.of(li1, li2));

        var order = orderService.createOrder(b.getId(), req);
        UUID orderId = order.getId();

        PaymentRequest payReq = new PaymentRequest();
        payReq.setMethod(PaymentMethod.UPI);
        payReq.setClientRequestId("req-pay");
        payReq.setSimulateFailure(false);

        var payment = paymentService.payOrder(orderId, payReq);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());

        // Cancel one line item
        UUID lineItemIdToCancel = order.getLineItems().get(1).getId();
        var updated = orderService.cancelLineItem(orderId, lineItemIdToCancel);

        // Refund should exist
        var refunds = refundRepo.findByPayment_Id(payment.getId());
        assertFalse(refunds.isEmpty());
        // refunded amount should be > 0
        var afterPay = paymentService.getPayment(payment.getId());
        assertTrue(afterPay.getRefundedAmount().compareTo(BigDecimal.ZERO) > 0);
        // New grandTotal should be lower than old
        assertTrue(updated.getGrandTotal().compareTo(order.getGrandTotal()) < 0);
    }

    private MenuItemUpsertRequest item(String name, BigDecimal price, MenuType type) {
        MenuItemUpsertRequest r = new MenuItemUpsertRequest();
        r.setMenuType(type);
        r.setName(name);
        r.setDescription(name);
        r.setPrice(price);
        r.setPrepTimeMinutes(5);
        r.setCategory(ItemCategory.MAIN_COURSE);
        r.setDietType(DietType.VEG);
        r.setAvailable(true);
        return r;
    }
}
