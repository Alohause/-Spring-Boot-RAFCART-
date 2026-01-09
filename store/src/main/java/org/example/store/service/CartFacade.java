package org.example.store.service;

import java.math.BigDecimal;
import java.util.List;

public interface CartFacade {
    class CartItem {
        public Long id;
        public Long skuId;
        public Long productId;
        public String productName;
        public String skuSpec;
        public BigDecimal price;
        public Integer quantity;
    }

    class CartView {
        public List<CartItem> items;
        public BigDecimal totalAmount;
    }

    CartView getCart(Long userId);

    void clearCart(Long userId);
}
