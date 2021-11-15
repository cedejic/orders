package com.demo.orders;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;
import com.demo.orders.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.demo.orders.util.ConversionUtils.toBigDecimal;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Test create Product SUCCESS")
    void testCreateProductSuccess() throws Exception {
        CreateProductDto createProductDto = prepareCreateProduct();
        ProductDto firstProduct = prepareFirstProduct();

        when(productService.createProduct(any(CreateProductDto.class))).thenReturn(firstProduct);

        mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createProductDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(5)))
            .andExpect(jsonPath("$.name", equalTo("Coconut oil")))
            .andExpect(jsonPath("$.price", equalTo(15.60d))); // issue with jsonPath casting value to Double

        verify(productService).createProduct(any(CreateProductDto.class));
    }

    @Test
    @DisplayName("Test create Product with invalid request FAIL")
    void testCreateProductWithInvalidRequestFail() throws Exception {
        CreateProductDto invalidCreateProductDto = prepareInvalidCreateProduct();

        mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidCreateProductDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", equalTo("Validation failed")))
            .andExpect(jsonPath("$.details", hasSize(1)))
            .andExpect(jsonPath("$.details[0]", equalTo("must not be empty")));
    }

    @Test
    @DisplayName("Test get all Products SUCCESS")
    void testGetAllProductsSuccess() throws Exception {
        ProductDto firstProduct = prepareFirstProduct();
        ProductDto secondProduct = prepareSecondProduct();

        when(productService.getAllProducts()).thenReturn(List.of(firstProduct, secondProduct));

        mockMvc.perform(get("/products/all"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[?(@.id == 5 && @.name == \"Coconut oil\" && @.price == 15.60)]").exists())
            .andExpect(jsonPath("$.[?(@.id == 6 && @.name == \"Lager beer\" && @.price == 4.50)]").exists());

        verify(productService).getAllProducts();
    }

    @Test
    @DisplayName("Test update Product SUCCESS")
    void testUpdateProductSuccess() throws Exception {
        UpdateProductDto updateProductDto = prepareUpdateProduct();
        ProductDto updatedProduct = prepareUpdatedProduct();

        when(productService.updateProduct(anyLong(), any(UpdateProductDto.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/5")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProductDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(5)))
            .andExpect(jsonPath("$.name", equalTo("Coconut oil")))
            .andExpect(jsonPath("$.price", equalTo(13.40d))); // issue with jsonPath casting value to Double

        verify(productService).updateProduct(any(), any(UpdateProductDto.class));
    }

    @Test
    @DisplayName("Test update Product with invalid request FAIL")
    void testUpdateProductWithInvalidRequestFail() throws Exception {
        UpdateProductDto invalidUpdateProductDto = prepareInvalidUpdateProduct();

        mockMvc.perform(put("/products/5")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUpdateProductDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", equalTo("Validation failed")))
            .andExpect(jsonPath("$.details", hasSize(1)))
            .andExpect(jsonPath("$.details[0]", equalTo("must not be null")));
    }

    private CreateProductDto prepareCreateProduct() {
        return new CreateProductDto("Coconut oil", toBigDecimal(15.60));
    }

    private CreateProductDto prepareInvalidCreateProduct() {
        return new CreateProductDto(null, toBigDecimal(15.60));
    }

    private UpdateProductDto prepareUpdateProduct() {
        return new UpdateProductDto(toBigDecimal(13.40));
    }

    private ProductDto prepareUpdatedProduct() {
        return new ProductDto(5L, "Coconut oil", toBigDecimal(13.40));
    }

    private UpdateProductDto prepareInvalidUpdateProduct() {
        return new UpdateProductDto(null);
    }

    private ProductDto prepareFirstProduct() {
        return new ProductDto(5L, "Coconut oil", toBigDecimal(15.60));
    }

    private ProductDto prepareSecondProduct() {
        return new ProductDto(6L, "Lager beer", toBigDecimal(4.50));
    }
}
