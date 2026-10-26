package com.example.ordersystem.benchmark;

import lombok.Getter;

@Getter
public enum ConcurrencyLoadCondition {

    BASIC_ORDER_SERVICE("Basic Order Service", 20, 1);

    private final String strategy;
    private final int threadCount;
    private final int productCount;

    private ConcurrencyLoadCondition(String strategy, int threadCount, int productCount) {
        this.strategy = strategy;
        this.threadCount = threadCount;
        this.productCount = productCount;
    }
}
