package com.ecommerce.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.dto.CreateProductRequest;
import com.ecommerce.dto.UpdateProductRequest;
import com.ecommerce.product.model.Product;


public interface ProductService {

	public Product createProduct(CreateProductRequest createProductRequest);

	public Product updateProduct(Long id, UpdateProductRequest updateProductRequest);

	void deleteProduct(Long id);

	Product getProductById(Long id);

	 Page<Product> getAllActiveProducts(Pageable pageable);

}
