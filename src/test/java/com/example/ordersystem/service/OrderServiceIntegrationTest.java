package com.example.ordersystem.service;

import com.example.ordersystem.domain.Product;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.service.order.OrderPlacementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    ProductService productService;
    @Autowired
    OrderPlacementStrategy orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void tearDown() {
        productRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("순차 주문 테스트")
    void sequential_placeOrder() {

        // given
        // 재고가 100개인 상품 생성
        Long productId = productService.create("test product", 1000, 100);

        // when
        // 순차로 1개씩 10개 주문
        for(int i=0; i<10; i++)
            orderService.place(productId, 1);

        // then1
        // 재고는 90
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        assertThat(product.getStock()).isEqualTo(90);

        // then2
        // 주문이 10건 생성됨
        assertThat(orderRepository.countByProductId(productId)).isEqualTo(10);
    }
}
