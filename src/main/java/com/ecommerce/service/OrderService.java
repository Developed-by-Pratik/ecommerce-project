package com.ecommerce.service;

import java.util.List;

import com.ecommerce.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder();
    List<OrderResponse> getMyOrders();
    OrderResponse getOrder(Long orderId);
}

