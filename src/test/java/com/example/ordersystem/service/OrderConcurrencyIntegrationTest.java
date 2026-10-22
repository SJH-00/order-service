package com.example.ordersystem.service;

import com.example.ordersystem.benchmark.ConcurrencyLoadTemplate;
import com.example.ordersystem.config.TestcontainersConfiguration;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.service.order.OrderPlacementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OrderConcurrencyIntegrationTest {

    @Autowired
    ProductService productService;
    @Autowired
    OrderPlacementStrategy orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;

    ConcurrencyLoadTemplate concurrencyLoadTemplate = new ConcurrencyLoadTemplate();

    @BeforeEach
    void tearDown() {
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("멀티스레드 정합성 테스트")
    void concurrent_placeOrder() throws InterruptedException {

        int initialStock = 1000;
        int requestCount = 300;
        int threadCount = 20;
        Long productId = productService.create("test product", 1000, initialStock);
        Runnable task = () -> orderService.place(productId, 1);

        int successCount = concurrencyLoadTemplate.run(requestCount, threadCount, task).successCount().get();

        int remainingStock = productRepository.findById(productId).orElseThrow().getStock();
        assertThat(initialStock - remainingStock).isEqualTo(successCount);
    }

    @Test
    @DisplayName("멀티스레드 정합성 테스트 - 재고 부족 시나리오")
    void concurrent_placeOrder_when_stock_is_low() throws InterruptedException{

        int initialStock = 100;
        int requestCount = 300;
        int threadCount = 20;
        Long productId = productService.create("test product", 1000, initialStock);
        Runnable task = () -> orderService.place(productId, 1);

        int successCount = concurrencyLoadTemplate.run(requestCount, threadCount, task).successCount().get();

        assertThat(successCount).isEqualTo(initialStock);
    }
}
