# Restaurant Order Management System

A runnable **Spring Boot 3 + Java 17 + H2** REST API for a simple Restaurant Order Management System.

✅ Works by importing into **IntelliJ IDEA** as a Maven project.  
✅ Swagger UI included.  
✅ Order lifecycle + menu time-slots + combo pricing + idempotent payments + partial refunds.

---

## Tech Stack

- Java 17
- Spring Boot 3 (Web, Validation, Data JPA)
- H2 (in-memory DB)
- OpenAPI + Swagger UI (springdoc)

---

## Run locally (IntelliJ)

1. **Open** this folder in IntelliJ
2. IntelliJ will detect **pom.xml** → click **Load Maven Changes**
3. Run the main class:
   - `src/main/java/com/jumio/roms/RestaurantOrderManagementApplication.java`

App starts on: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`  
H2 Console: `http://localhost:8080/h2-console`

H2 JDBC URL (also present in `application.yml`):
- `jdbc:h2:mem:roms`
- user: `sa`
- password: *(empty)*

---

## Seed Data

On first run, demo data is inserted (see `DataSeeder`):
- 1 Branch: **Jumio Diner**
- Lunch items: Paneer Butter Masala, Butter Naan, Gulab Jamun
- 1 Lunch combo: Paneer Combo (Paneer + 2 Naan)

You can also create your own branches/items/combos through APIs.

---

## Key Business Rules Implemented

### Time-slot menu
Menu type is derived from `orderTime` (Asia/Kolkata):
- **Breakfast**: 06:00–11:00
- **Lunch**: 11:00–16:00
- **Dinner**: 16:00–22:00

When creating an order, each item/combo must match the derived menu type.

### Order status state machine
Allowed transitions:
- CREATED → ACCEPTED → PREPARING → READY → DELIVERED
- Any state except DELIVERED can go → CANCELLED

### Pricing
- Subtotal is based on **original** prices
- Combo discount = (sum of items) - discountedPrice
- Tax: 18% (configurable)
- Delivery charge: ₹40 (configurable) for DELIVERY orders

### Payments
- Supports `CASH`, `CARD`, `UPI`
- `payOrder` is **idempotent** using `(orderId + clientRequestId)`
- Retry supported up to `app.payment.maxAttempts` (default 3)

### Refunds
- When an order is cancelled (full) or a line item is cancelled (partial),
  if the latest payment is SUCCESS, a refund is created automatically.

---

## API Overview

Base: `/api/v1`

### Branch
- `POST /branches`
- `GET /branches`
- `GET /branches/{branchId}`

### Menu (Items + Combos)
- `POST /branches/{branchId}/menu-items`
- `PUT /menu-items/{itemId}`
- `PATCH /menu-items/{itemId}/availability`
- `POST /branches/{branchId}/menu-items/bulk`
- `POST /branches/{branchId}/combos`
- `GET /branches/{branchId}/menu?at=2026-01-04T07:00:00Z`

### Orders
- `POST /branches/{branchId}/orders`
- `GET /orders/{orderId}`
- `GET /orders/{orderId}/bill`
- `POST /orders/{orderId}/accept`
- `POST /orders/{orderId}/prepare`
- `POST /orders/{orderId}/ready`
- `POST /orders/{orderId}/deliver`
- `POST /orders/{orderId}/cancel`
- `POST /orders/{orderId}/items/{lineItemId}/cancel`

### Payments
- `POST /orders/{orderId}/payments`
- `POST /payments/{paymentId}/retry`
- `GET /payments/{paymentId}`

---

## Example cURL (end-to-end)

### 1) Create branch
```bash
curl -s -X POST http://localhost:8080/api/v1/branches \
  -H 'Content-Type: application/json' \
  -d '{"name":"My Branch","address":"Noida"}'
```

### 2) Create breakfast menu item
```bash
curl -s -X POST http://localhost:8080/api/v1/branches/{branchId}/menu-items \
  -H 'Content-Type: application/json' \
  -d '{
    "menuType":"BREAKFAST",
    "name":"Poha",
    "description":"Poha",
    "price":50,
    "prepTimeMinutes":5,
    "category":"MAIN_COURSE",
    "dietType":"VEG",
    "available":true
  }'
```

### 3) Create order at breakfast time
```bash
curl -s -X POST http://localhost:8080/api/v1/branches/{branchId}/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerName":"Rishabh",
    "customerPhone":"9999999999",
    "customerAddress":"Noida",
    "orderType":"DELIVERY",
    "orderTime":"2026-01-04T01:30:00Z",
    "items":[
      {"kind":"ITEM","refId":"{menuItemId}","quantity":1}
    ]
  }'
```

### 4) Pay (idempotent)
```bash
curl -s -X POST http://localhost:8080/api/v1/orders/{orderId}/payments \
  -H 'Content-Type: application/json' \
  -d '{"method":"UPI","clientRequestId":"req-001","simulateFailure":false}'
```

### 5) Cancel order (auto refund if already paid)
```bash
curl -s -X POST http://localhost:8080/api/v1/orders/{orderId}/cancel
```

---

## Notes

- This is a backend-only assignment project (no UI).
- H2 data resets on every restart. If you want persistence, switch to file-based H2 or Postgres in `application.yml`.
