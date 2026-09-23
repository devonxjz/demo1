package dev.services;

import dev.dao.ProductDao;
import dev.models.Product;

import java.util.List;

/**
 * Service quản lý sản phẩm
 */
public class ProductService {

    private final ProductDao productDao = new ProductDao();

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public Product getProductByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return productDao.findByCode(code.trim()).orElse(null);
    }
}
