package com.ecommerce.serviceimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.CreateProductRequest;
import com.ecommerce.dto.UpdateProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public Product createProduct(CreateProductRequest createProductRequest) {

		Product product = new Product();

		product.setName(createProductRequest.getName());
		product.setDescription(createProductRequest.getDescription());
		product.setPrice(createProductRequest.getPrice());
		product.setStock(createProductRequest.getStock());
		product.setCategory(createProductRequest.getCategory());
		product.setActive(true);

		return productRepository.save(product);
	}

	@Override
	public Product updateProduct(Long id, UpdateProductRequest updateProductRequest) {

		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product Not Fount"));
		product.setName(updateProductRequest.getName());
		product.setDescription(updateProductRequest.getDescription());
		product.setPrice(updateProductRequest.getPrice());
		product.setStock(updateProductRequest.getStock());
		product.setCategory(updateProductRequest.getCategory());
		product.setActive(updateProductRequest.isActive());

		return productRepository.save(product);
	}

	@Override
	public void deleteProduct(Long id) {

	    Product product = productRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Product not found"));

	    product.setDeleted(true);     // ✅ SOFT DELETE
	    product.setActive(false);     // ✅ also make it unavailable

	    productRepository.save(product);
	}

	@Override
	public Product getProductById(Long id) {

		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product Not Found"));

		if (!product.isActive()) {
			throw new RuntimeException("Product Not Available");
		}

		return product;
	}

	@Override
	public Page<Product> getAllActiveProducts(Pageable pageable) {
		return productRepository.findByActiveTrue(pageable);
	}

}
