package com.example.ordersystem.controller;

import com.example.ordersystem.service.order.OrderPlacementStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderPlacementStrategy orderService;

    @PostMapping
    public ResponseEntity<Void> place(@Valid @RequestBody PlaceOrderRequest request) {
        Long orderId = orderService.place(request.productId, request.quantity);
        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    public record PlaceOrderRequest(
            @NotNull Long productId,
            @Min(1) int quantity
    ) {}
}
