package com.ecommerce.dto;

import java.math.BigDecimal;

import com.ecommerce.product.model.Product;

public class ProductResponse {

	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stock;
	private String category;
	private boolean active;

	// getters & setters

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public static ProductResponse from(Product product) {
		ProductResponse res = new ProductResponse();
		res.setId(product.getId());
		res.setName(product.getName());
		res.setPrice(product.getPrice());
		res.setStock(product.getStock());
		res.setCategory(product.getCategory());
		res.setActive(product.isActive());
		return res;

	}
}
