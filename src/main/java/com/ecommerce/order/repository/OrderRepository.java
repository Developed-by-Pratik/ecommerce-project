package com.ecommerce.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.auth.model.User;
import com.ecommerce.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUser(User user);
}
