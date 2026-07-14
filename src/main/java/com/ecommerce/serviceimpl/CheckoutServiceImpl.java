package com.ecommerce.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.model.User;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.dto.CheckoutItemResponse;
import com.ecommerce.dto.CheckoutResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.service.CheckoutService;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;

    public CheckoutServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public CheckoutResponse checkout() {

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal recalculatedTotal = BigDecimal.ZERO;
        List<CheckoutItemResponse> itemResponses = new ArrayList<>();

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            if (product.isDeleted()) {
                throw new RuntimeException("Product removed: " + product.getName());
            }

            if (!product.isActive()) {
                throw new RuntimeException("Product inactive: " + product.getName());
            }

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            BigDecimal subTotal =
                    item.getPriceAtAddTime()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

            recalculatedTotal = recalculatedTotal.add(subTotal);

            CheckoutItemResponse r = new CheckoutItemResponse();
            r.setProductId(product.getId());
            r.setProductName(product.getName());
            r.setQuantity(item.getQuantity());
            r.setPrice(item.getPriceAtAddTime());
            r.setSubTotal(subTotal);

            itemResponses.add(r);
        }

        CheckoutResponse response = new CheckoutResponse();
        response.setItems(itemResponses);
        response.setTotalAmount(recalculatedTotal);
        response.setEligibleForOrder(true);

        return response;
    }
}

