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

    public boolean saveProduct(Product product) {
        if (product == null || product.getCode() == null || product.getCode().trim().isEmpty()) {
            return false;
        }
        boolean saved = productRepository.save(product);
        if (saved) {
            PRODUCT_CACHE.put(product.getCode().toLowerCase(), product);
        }
        return saved;
    }

    private synchronized void ensureLoaded() {
        if (initialized && !PRODUCT_CACHE.isEmpty()) {
            return;
        }

        try {
            List<Product> fromDb = productRepository.findAll();
            if (fromDb.isEmpty()) {
                seedInitialProducts();
                fromDb = productRepository.findAll();
            }
            for (Product p : fromDb) {
                if (p != null && p.getCode() != null) {
                    PRODUCT_CACHE.put(p.getCode().toLowerCase(), p);
                }
            }
            initialized = true;
        } catch (Exception e) {
            System.err.println("[ProductService] Error loading products from DB: " + e.getMessage());
        }
    }

    private synchronized void seedInitialProducts() {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.save(new Product("8601", "86 (the band) - True Life Songs and Pictures", 14.95, "Album đĩa CD của nhóm 86"));
        productRepository.save(new Product("pf01", "Paddlefoot - The First CD Album", 12.95, "Album ca nhạc Paddlefoot đầu tay"));
        productRepository.save(new Product("jr01", "Joe Rut - Genuine Wood Grained Finish", 14.95, "Đĩa CD Joe Rut chính hãng"));
        productRepository.save(new Product("jsp01", "Murach's Java Servlets and JSP (4th Edition)", 54.50, "Sách lập trình Java Servlet & JSP chuẩn Murach"));
        productRepository.save(new Product("kb01", "RGB Mechanical Gaming Keyboard", 45.00, "Bàn phím cơ chơi game LED RGB"));
    }
}
