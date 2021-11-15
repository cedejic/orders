package com.demo.orders.service.product.impl;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;
import com.demo.orders.model.Product;
import com.demo.orders.repository.ProductRepository;
import com.demo.orders.service.product.ProductService;
import com.demo.orders.service.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(Long id) {
        return productRepository.findById(id)
            .map(productMapper::mapToDto)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
            .map(productMapper::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDto createProduct(CreateProductDto createProductDto) {
        Product product = productRepository.save(productMapper.mapCreateDtoToEntity(createProductDto));
        return productMapper.mapToDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductDto updateProductDto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " does not exist"));

        if (product.getPrice().compareTo(updateProductDto.getPrice()) != 0) {
            product = productRepository.save(productMapper.mapUpdateDtoToEntity(updateProductDto, product));
        }
        return productMapper.mapToDto(product);
    }
}
