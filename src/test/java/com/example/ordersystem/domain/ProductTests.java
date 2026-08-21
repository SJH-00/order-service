package com.example.ordersystem.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductTests {

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 예외가 발생한다")
    void decreaseStock_moreThanAvailable_throwsException() {

        // given
        Product product = Product.create("test product", 1000, 100);

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () ->
        {
            product.decreaseStock(200);
        });
    }

    @Test
    @DisplayName("재고와 동일한 수량은 차감할 수 있다")
    void decreaseStock_exactlyAvailable_succeeds() {

        // given
        Product product = Product.create("test product", 1000, 100);

        // when
        product.decreaseStock(100);

        //then
        assertThat(product.getStock()).isZero();
    }
}
