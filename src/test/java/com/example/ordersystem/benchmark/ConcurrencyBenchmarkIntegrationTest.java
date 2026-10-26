package com.example.ordersystem.benchmark;

import com.example.ordersystem.config.TestcontainersConfiguration;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.service.ProductService;
import com.example.ordersystem.service.order.BasicOrderService;
import com.example.ordersystem.service.order.OrderPlacementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.stream.Stream;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ConcurrencyBenchmarkIntegrationTest {

    @Autowired
    ApplicationContext context;
    @Autowired
    ProductService productService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    ConcurrencyLoadTemplate concurrencyLoadTemplate = new ConcurrencyLoadTemplate();

    static Stream<Arguments> strategies() {
        return Stream.of(
                Arguments.of(BasicOrderService.class, ConcurrencyLoadCondition.BASIC_ORDER_SERVICE)
        );
    }

    @BeforeEach
    void tearDown() {
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("주문 전략별 성능 및 비용 측정")
    void measure(Class<? extends OrderPlacementStrategy> strategy, ConcurrencyLoadCondition condition) throws InterruptedException, IOException {

        int initialStock = 1000;
        int requestCount = 300;
        Long productId = productService.create("test product", 1000, initialStock);
        OrderPlacementStrategy orderService = context.getBean(strategy);
        Runnable task = () -> orderService.place(productId, 1);

        ConcurrencyLoadResult result = concurrencyLoadTemplate.run(requestCount, condition.getThreadCount(), task);
        ConcurrencyLoadReportWriter.write(result, condition);
    }
}
