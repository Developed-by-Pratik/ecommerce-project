package com.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutResponse {

	private List<CheckoutItemResponse> items;
	private BigDecimal totalAmount;
	private boolean eligibleForOrder;

	public List<CheckoutItemResponse> getItems() {
		return items;
	}

	public void setItems(List<CheckoutItemResponse> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public boolean isEligibleForOrder() {
		return eligibleForOrder;
	}

	public void setEligibleForOrder(boolean eligibleForOrder) {
		this.eligibleForOrder = eligibleForOrder;
	}
}
