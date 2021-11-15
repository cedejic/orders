package com.demo.orders.service.product;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;

import java.util.List;

public interface ProductService {

    ProductDto getProduct(Long productId);
    List<ProductDto> getAllProducts();
    ProductDto createProduct(CreateProductDto createProductDto);
    ProductDto updateProduct(Long id, UpdateProductDto updateProductDto);
}
