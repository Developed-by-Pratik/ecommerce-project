package com.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.CreateProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.dto.UpdateProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.service.ProductService;

import jakarta.validation.Valid;

@RestController()
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {

		Product product = productService.createProduct(createProductRequest);

		ProductResponse response = ProductResponse.from(product);

		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
			@Valid @RequestBody UpdateProductRequest request) {

		Product product = productService.updateProduct(id, request);
		ProductResponse response = ProductResponse.from(product);

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {

		Product product = productService.getProductById(id);
		return ResponseEntity.ok(ProductResponse.from(product));
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping
	public ResponseEntity<Page<ProductResponse>> getAllProducts(
			@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

		Page<ProductResponse> response = productService.getAllActiveProducts(pageable).map(ProductResponse::from);

		return ResponseEntity.ok(response);
	}

}
