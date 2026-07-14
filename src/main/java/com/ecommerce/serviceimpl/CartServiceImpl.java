package com.ecommerce.serviceimpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.model.User;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.dto.UpdateCartItemRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.service.CartService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    private User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    public CartResponse getMyCart() {
        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });

        return mapToResponse(cart);
    }

    @Override
    public CartResponse addItem(AddToCartRequest request) {
        if (request.getQuantity() <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        User user = getLoggedInUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.isDeleted())
            throw new RuntimeException("Product is deleted");

        if (product.getStock() < request.getQuantity())
            throw new RuntimeException("Insufficient stock");

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });

        CartItem item = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setPriceAtAddTime(product.getPrice());
            cart.getItems().add(item);
        } else {
            int newQty = item.getQuantity() + request.getQuantity();
            if (newQty > product.getStock())
                throw new RuntimeException("Stock exceeded");
            item.setQuantity(newQty);
        }

        recalculateTotal(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateItem(Long itemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User user = getLoggedInUser();
        if (!item.getCart().getUser().getId().equals(user.getId()))
            throw new RuntimeException("Forbidden");

        if (request.getQuantity() < 0)
            throw new RuntimeException("Invalid quantity");

        if (request.getQuantity() == 0) {
            cartItemRepository.delete(item);
        } else {
            if (request.getQuantity() > item.getProduct().getStock())
                throw new RuntimeException("Stock exceeded");
            item.setQuantity(request.getQuantity());
        }

        Cart cart = item.getCart();
        recalculateTotal(cart);
        return mapToResponse(cart);
    }

    @Override
    public void removeItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User user = getLoggedInUser();
        if (!item.getCart().getUser().getId().equals(user.getId()))
            throw new RuntimeException("Forbidden");

        Cart cart = item.getCart();
        cartItemRepository.delete(item);
        recalculateTotal(cart);
    }

    @Override
    public void clearCart() {
        User user = getLoggedInUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
    }

    private void recalculateTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPriceAtAddTime()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
    }

    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setTotalAmount(cart.getTotalAmount());

        List<CartItemResponse> items = cart.getItems().stream().map(i -> {
            CartItemResponse r = new CartItemResponse();
            r.setItemId(i.getId());
            r.setProductId(i.getProduct().getId());
            r.setProductName(i.getProduct().getName());
            r.setQuantity(i.getQuantity());
            r.setPrice(i.getPriceAtAddTime());
            return r;
        }).toList();

        response.setItems(items);
        return response;
    }
}
