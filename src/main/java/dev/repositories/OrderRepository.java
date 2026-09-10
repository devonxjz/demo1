package dev.repositories;

import dev.models.Order;

public class OrderRepository extends BaseRepository<Order, Long> {

    public OrderRepository() {
        super(Order.class);
    }
}
