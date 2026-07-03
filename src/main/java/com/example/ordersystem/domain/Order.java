package com.example.ordersystem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Order(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public static Order create(Long productId, int quantity) {
        return new Order(productId, quantity);
    }

    public enum OrderStatus {
        CREATED, CANCELLED
    }
}
