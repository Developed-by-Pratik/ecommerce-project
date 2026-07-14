package com.ecommerce.serviceimpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.model.User;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.service.OrderService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(CartRepository cartRepository,
                            OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder() {

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty())
            throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(cart.getTotalAmount());

        for (CartItem cartItem : cart.getItems()) {

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(cartItem.getProduct().getId());
            item.setProductName(cartItem.getProduct().getName());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPriceAtAddTime());

            BigDecimal subTotal =
                    cartItem.getPriceAtAddTime()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            item.setSubTotal(subTotal);

            order.getItems().add(item);
        }

        Order savedOrder = orderRepository.save(order);

        // clear cart AFTER order creation
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrder(Long orderId) {

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Forbidden");

        return mapToResponse(order);
    }
    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> {
                    OrderItemResponse r = new OrderItemResponse();
                    r.setProductId(item.getProductId());
                    r.setProductName(item.getProductName());
                    r.setQuantity(item.getQuantity());
                    r.setPrice(item.getPrice());
                    r.setSubTotal(item.getSubTotal());
                    return r;
                })
                .toList();

        response.setItems(itemResponses);
        return response;
    }

}

