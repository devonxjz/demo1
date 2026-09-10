package dev.services;

import dev.models.Product;
import dev.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductService {

    private final ProductRepository productRepository = new ProductRepository();
    private static final Map<String, Product> PRODUCT_CACHE = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    public List<Product> getAllProducts() {
        ensureLoaded();
        return Collections.unmodifiableList(new ArrayList<>(PRODUCT_CACHE.values()));
    }

    public Product getProductByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        ensureLoaded();
        Product p = PRODUCT_CACHE.get(code.trim().toLowerCase());
        if (p != null) {
            return p;
        }
        return productRepository.findByCode(code.trim()).map(prod -> {
            PRODUCT_CACHE.put(prod.getCode().toLowerCase(), prod);
            return prod;
        }).orElse(null);
    }

    private synchronized void ensureLoaded() {
        if (initialized && !PRODUCT_CACHE.isEmpty()) {
            return;
        }

        try {
            List<Product> fromDb = productRepository.findAll();
            for (Product p : fromDb) {
                if (p != null && p.getCode() != null) {
                    PRODUCT_CACHE.put(p.getCode().toLowerCase(), p);
                }
            }
            if (!fromDb.isEmpty()) {
                initialized = true;
            }
        } catch (Exception e) {
            System.err.println("[ProductService] Error loading products from DB: " + e.getMessage());
        }
    }
}
