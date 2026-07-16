package com.example.ordersystem.service;

import com.example.ordersystem.domain.Product;
import com.example.ordersystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long create(String name, int price, int stock) {
        Product product = Product.create(name, price, stock);
        return productRepository.save(product).getId();
    }

    @Transactional(readOnly = true)
    public Product get(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(("상품을 찾을 수 없습니다.")));
    }
}
