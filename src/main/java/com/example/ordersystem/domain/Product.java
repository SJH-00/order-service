package com.example.ordersystem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    private Product(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static Product create(String name, int price, int stock) {
        return new Product(name, price, stock);
    }

    public void decreaseStock(int quantity) {
        if(quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        if(this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고=" + this.stock + ", 요청=" + quantity);
        }
        this.stock -= quantity;
    }
}
