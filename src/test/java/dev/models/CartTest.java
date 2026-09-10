package dev.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;
    private Product p1;
    private Product p2;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        p1 = new Product("p01", "Product 1", 10.0, "Description 1");
        p2 = new Product("p02", "Product 2", 25.5, "Description 2");
    }

    @Test
    void testAddItem() {
        cart.addItem(new LineItem(p1, 2));
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getCount());
        assertEquals(2, cart.getTotalCount());
        assertEquals(20.0, cart.getTotalAmount());

        // Thêm tiếp cùng sản phẩm thì cộng dồn số lượng
        cart.addItem(new LineItem(p1, 3));
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getCount());
        assertEquals(5, cart.getTotalCount());
        assertEquals(50.0, cart.getTotalAmount());

        // Thêm sản phẩm khác
        cart.addItem(new LineItem(p2, 1));
        assertEquals(2, cart.getItems().size());
        assertEquals(6, cart.getCount());
        assertEquals(75.5, cart.getTotalAmount());
    }

    @Test
    void testUpdateItem() {
        cart.addItem(new LineItem(p1, 2));
        cart.updateItem("p01", 5);
        assertEquals(5, cart.getCount());
        assertEquals(50.0, cart.getTotalAmount());

        // Update về 0 hoặc âm thì xóa sản phẩm
        cart.updateItem("p01", 0);
        assertEquals(0, cart.getCount());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testRemoveItem() {
        cart.addItem(new LineItem(p1, 2));
        cart.addItem(new LineItem(p2, 1));
        assertEquals(2, cart.getItems().size());

        cart.removeItem("p01");
        assertEquals(1, cart.getItems().size());
        assertEquals("p02", cart.getItems().get(0).getProduct().getCode());
        assertEquals(25.5, cart.getTotalAmount());
    }

    @Test
    void testClear() {
        cart.addItem(new LineItem(p1, 2));
        cart.addItem(new LineItem(p2, 1));
        assertFalse(cart.getItems().isEmpty());

        cart.clear();
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, cart.getCount());
        assertEquals(0.0, cart.getTotalAmount());
    }

    @Test
    void testFormattedTotalAmount() {
        cart.addItem(new LineItem(p1, 1));
        String formatted = cart.getFormattedTotalAmount();
        assertNotNull(formatted);
        assertTrue(formatted.contains("10.00") || formatted.contains("10"));
    }
}
