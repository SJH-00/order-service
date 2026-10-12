package com.example.ordersystem.service;

import com.example.ordersystem.config.TestcontainersConfiguration;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OrderConcurrencyIntegrationTest {

    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void tearDown() {
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("멀티스레드 정합성 테스트")
    void concurrent_placeOrder() throws InterruptedException {

        // given
        int initialStock = 1000;
        int requestCount = 300;
        int threadCount = 20;
        AtomicInteger successCount = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requestCount);

        Long productId = productService.create("test product", 1000, initialStock);

        // when
        for(int i=0; i<requestCount; i++) {
            executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    orderService.placeOrder(productId, 1);
                    successCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                } finally {
                    done.countDown();
                }
            });
        }
        assertThat(ready.await(30, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        executorService.shutdown();

        // then
        int remainingStock = productRepository.findById(productId).orElseThrow().getStock();
        System.out.println("initial stock: " + initialStock);
        System.out.println("success count: " + successCount);
        System.out.println("remaining stock: " + remainingStock);
        assertThat(initialStock - remainingStock).isEqualTo(successCount);
    }
}
