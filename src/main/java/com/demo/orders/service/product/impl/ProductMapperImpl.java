package com.demo.orders.service.product.impl;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;
import com.demo.orders.model.Product;
import com.demo.orders.service.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {

    private final ModelMapper modelMapper;

    @Override
    public ProductDto mapToDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public Product mapCreateDtoToEntity(CreateProductDto createProductDto) {
        return modelMapper.map(createProductDto, Product.class);
    }

    @Override
    public Product mapUpdateDtoToEntity(UpdateProductDto updateProductDto, Product product) {
        modelMapper.map(updateProductDto, product);
        return product;
    }
}
