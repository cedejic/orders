package com.demo.orders.controller;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;
import com.demo.orders.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDto createProduct(@Valid @NotNull @RequestBody CreateProductDto createProductDto) {
        return productService.createProduct(createProductDto);
    }

    @GetMapping("/all")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public ProductDto updateProduct(@Valid @NotNull @RequestBody UpdateProductDto updateProductDto,
                                 @NotNull @PathVariable Long id) {
        return productService.updateProduct(id, updateProductDto);
    }
}
