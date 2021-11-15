package com.demo.orders.service.product;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;
import com.demo.orders.model.Product;

public interface ProductMapper {

    ProductDto mapToDto(Product product);
    Product mapCreateDtoToEntity(CreateProductDto createProductDto);
    Product mapUpdateDtoToEntity(UpdateProductDto updateProductDto, Product product);
}
