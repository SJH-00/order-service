package com.example.ordersystem.controller;

import com.example.ordersystem.domain.Product;
import com.example.ordersystem.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateProductRequest request) {
        Long productId = productService.create(request.name, request.price, request.stock);
        return ResponseEntity.created(URI.create("/products/" + productId)).build();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        Product product = productService.get(id);
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }

    public record CreateProductRequest(
            @NotBlank String name,
            @Min(0) int price,
            @Min(0) int stock) {}

    public record ProductResponse(Long id, String name, int price, int stock) {}
}
