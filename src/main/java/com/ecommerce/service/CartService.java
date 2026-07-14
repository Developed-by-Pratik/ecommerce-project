package com.ecommerce.service;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.dto.UpdateCartItemRequest;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItem(AddToCartRequest request);
    CartResponse updateItem(Long itemId, UpdateCartItemRequest request);
    void removeItem(Long itemId);
    void clearCart();
}
