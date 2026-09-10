package dev.services;

import dev.models.Bill;
import dev.models.Cart;
import dev.models.LineItem;
import dev.models.Order;
import dev.models.OrderDetail;
import dev.models.Product;
import dev.repositories.BillRepository;
import dev.repositories.OrderRepository;
import dev.repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class OrderService {

    private final ProductRepository productRepository = new ProductRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final BillRepository billRepository = new BillRepository();
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
                    Product existing = productRepository.findByCode(prod.getCode()).orElse(null);
                    if (existing != null) {
                        prod = existing;
                    } else {
                        productRepository.save(prod);
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

        boolean orderSaved = orderRepository.save(order);
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

        boolean billSaved = billRepository.save(bill);
        if (!billSaved) {
            System.err.println("[OrderService] Failed to save bill");
        }
        order.setBill(bill);

        return bill;
    }
}
