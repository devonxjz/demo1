package dev.services;

import dev.dao.BaseDao;
import dev.dao.ProductDao;
import dev.models.Bill;
import dev.models.Cart;
import dev.models.LineItem;
import dev.models.Order;
import dev.models.OrderDetail;
import dev.models.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * Service xử lý đơn hàng và hóa đơn
 */
public class OrderService {

    private final ProductDao productDao = new ProductDao();
    private final BaseDao<Order, Long> orderDao = new BaseDao<>(Order.class);
    private final BaseDao<Bill, Long> billDao = new BaseDao<>(Bill.class);
    private final Random random = new Random();

    public Bill createOrderAndBill(Cart cart, String customerIdentifier, String paymentMethod) {
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // 1. Tạo đơn hàng Order
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (random.nextInt(900) + 100);
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerIdentifier(customerIdentifier != null ? customerIdentifier : "Guest")
                .orderDate(LocalDateTime.now())
                .totalAmount(cart.getTotalAmount())
                .status("COMPLETED")
                .orderDetails(new ArrayList<>())
                .build();

        // 2. Tạo chi tiết đơn hàng OrderDetail cho từng món trong giỏ (cascade persist)
        for (LineItem item : cart.getItems()) {
            if (item != null && item.getProduct() != null) {
                Product prod = item.getProduct();
                if (prod.getId() == null) {
                    Product existing = productDao.findByCode(prod.getCode()).orElse(null);
                    if (existing != null) {
                        prod = existing;
                    } else {
                        productDao.save(prod);
                    }
                }

                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .product(prod)
                        .productCode(prod.getCode())
                        .productName(prod.getName())
                        .unitPrice(prod.getPrice())
                        .quantity(item.getQuantity())
                        .lineTotal(item.getTotal())
                        .build();
                order.getOrderDetails().add(detail);
            }
        }

        boolean orderSaved = orderDao.save(order);
        if (!orderSaved) {
            System.err.println("[OrderService] Failed to save order");
            return null;
        }

        // 3. Tạo hóa đơn Bill
        String billNumber = "INV-" + System.currentTimeMillis() + "-" + (random.nextInt(900) + 100);
        Bill bill = Bill.builder()
                .billNumber(billNumber)
                .order(order)
                .billDate(LocalDateTime.now())
                .totalAmount(cart.getTotalAmount())
                .paymentMethod(paymentMethod != null ? paymentMethod : "Mock Payment")
                .paymentStatus("PAID")
                .build();

        boolean billSaved = billDao.save(bill);
        if (!billSaved) {
            System.err.println("[OrderService] Failed to save bill");
        }
        order.setBill(bill);

        return bill;
    }
}
