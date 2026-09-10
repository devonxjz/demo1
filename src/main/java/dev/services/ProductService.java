package dev.services;

import dev.models.Product;
import dev.repositories.ProductRepository;

import java.util.List;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return productRepository.findByCode(code.trim()).orElse(null);
    }
}
