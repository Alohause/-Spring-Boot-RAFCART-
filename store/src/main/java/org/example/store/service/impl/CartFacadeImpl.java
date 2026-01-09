package org.example.store.service.impl;

import org.example.store.service.CartFacade;
import org.example.store.service.ICartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartFacadeImpl implements CartFacade {

    private final ICartService cartService;

    public CartFacadeImpl(ICartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public CartView getCart(Long userId) {
        var cart = cartService.getMyCart(userId);

        CartView v = new CartView();
        v.items = cart.getItems().stream().map(ci -> {
            CartItem it = new CartItem();
            it.id = ci.getId();
            it.skuId = ci.getSkuId();
            it.productId = ci.getProductId();
            it.productName = ci.getProductName();

            StringBuilder sb = new StringBuilder();
            if (ci.getSize() != null && !ci.getSize().isBlank()) sb.append("Size: ").append(ci.getSize());
            if (ci.getColor() != null && !ci.getColor().isBlank()) {
                if (sb.length() > 0) sb.append(" / ");
                sb.append("Color: ").append(ci.getColor());
            }
            it.skuSpec = sb.length() > 0 ? sb.toString() : (ci.getSkuCode() != null ? ci.getSkuCode() : "");

            it.price = ci.getPrice();
            it.quantity = ci.getQuantity();
            return it;
        }).toList();

        v.totalAmount = cart.getTotalAmount() == null ? BigDecimal.ZERO : cart.getTotalAmount();
        return v;
    }

    @Override
    public void clearCart(Long userId) {
        cartService.clearCart(userId);
    }
}
