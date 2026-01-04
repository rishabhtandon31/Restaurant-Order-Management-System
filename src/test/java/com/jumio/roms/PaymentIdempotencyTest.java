package com.jumio.roms;

import com.jumio.roms.api.dto.menu.MenuItemUpsertRequest;
import com.jumio.roms.api.dto.order.OrderCreateRequest;
import com.jumio.roms.api.dto.payment.PaymentRequest;
import com.jumio.roms.domain.enums.*;
import com.jumio.roms.repo.BranchRepository;
import com.jumio.roms.service.MenuService;
import com.jumio.roms.service.OrderService;
import com.jumio.roms.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PaymentIdempotencyTest {

    @Autowired BranchRepository branchRepo;
    @Autowired MenuService menuService;
    @Autowired OrderService orderService;
    @Autowired PaymentService paymentService;

    @Test
    void payIsIdempotentByClientRequestId() {
        // Create branch
        var b = branchRepo.save(newBranch("B1", "Addr"));

        // Create menu item aligned with current timeslot by using DINNER and passing orderTime within dinner isn't guaranteed.
        // We'll just create orderTime now and set item menuType to whatever is active using the menu endpoint behavior:
        // For test simplicity, use LUNCH and place orderTime within lunch (11:30 IST approx) not feasible in CI.
        // So instead: create BREAKFAST item and force orderTime to 07:00 IST today.
        MenuItemUpsertRequest itemReq = new MenuItemUpsertRequest();
        itemReq.setMenuType(MenuType.BREAKFAST);
        itemReq.setName("Poha");
        itemReq.setDescription("Poha");
        itemReq.setPrice(new BigDecimal("50.00"));
        itemReq.setPrepTimeMinutes(5);
        itemReq.setCategory(ItemCategory.MAIN_COURSE);
        itemReq.setDietType(DietType.VEG);
        itemReq.setAvailable(true);

        UUID itemId = menuService.createMenuItem(b.getId(), itemReq).getId();

        OrderCreateRequest oreq = new OrderCreateRequest();
        oreq.setCustomerName("C");
        oreq.setCustomerPhone("1");
        oreq.setCustomerAddress("A");
        oreq.setOrderType(OrderType.PICKUP);
        // 07:00 IST = 01:30 UTC (approx) -> We'll just set an Instant fixed at epoch day start + 2 hours
        // Safer: use now and accept time-slot mismatch? It would fail.
        // We'll compute a breakfast time in Asia/Kolkata for today.
        var zoned = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Kolkata"))
                .withHour(7).withMinute(0).withSecond(0).withNano(0);
        oreq.setOrderTime(zoned.toInstant());

        OrderCreateRequest.OrderItemRequest oli = new OrderCreateRequest.OrderItemRequest();
        oli.setKind(LineItemKind.ITEM);
        oli.setRefId(itemId);
        oli.setQuantity(1);
        oreq.setItems(List.of(oli));

        UUID orderId = orderService.createOrder(b.getId(), oreq).getId();

        PaymentRequest p1 = new PaymentRequest();
        p1.setMethod(PaymentMethod.UPI);
        p1.setClientRequestId("req-123");
        p1.setSimulateFailure(false);

        var pay1 = paymentService.payOrder(orderId, p1);

        PaymentRequest p2 = new PaymentRequest();
        p2.setMethod(PaymentMethod.UPI);
        p2.setClientRequestId("req-123");
        p2.setSimulateFailure(true); // should be ignored because idempotent returns existing

        var pay2 = paymentService.payOrder(orderId, p2);

        assertEquals(pay1.getId(), pay2.getId());
        assertEquals(PaymentStatus.SUCCESS, pay2.getStatus());
    }

    private com.jumio.roms.domain.entity.Branch newBranch(String name, String addr) {
        var b = new com.jumio.roms.domain.entity.Branch();
        b.setName(name);
        b.setAddress(addr);
        return b;
    }
}
