package com.example.ordersystem.repository;

import com.example.ordersystem.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
