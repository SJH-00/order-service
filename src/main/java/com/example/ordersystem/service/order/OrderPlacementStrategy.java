package com.example.ordersystem.service.order;

public interface OrderPlacementStrategy {

    Long place(Long productId, int quantity);
}
