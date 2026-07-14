package com.ecommerce.cart.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.product.model.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
